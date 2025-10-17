<script setup lang="ts">
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { useAutoComplete, useVerweisTypSearch } from '@/composables/useAutoComplete'
import type { VerweisTyp } from '@/domain/verweisTyp'
import { useFetchVerweisTypen } from '@/services/verweisTypService'
import { VerweisTypEnum, verweisTypToLabel } from '@/domain/activeReference'

defineProps<{
  inputId: string
  invalid: boolean
  ariaLabel?: string
  placeholder?: string
}>()

const modelValue = defineModel<VerweisTypEnum | undefined>()
const emit = defineEmits<{
  'update:modelValue': [value: VerweisTypEnum | undefined]
}>()

const autoComplete = ref<typeof RisAutoComplete | null>(null)
const verweisTypen = ref<VerweisTyp[]>([])
const selectedVerweisTypId = ref<string | undefined>()

const searchFn = useVerweisTypSearch(verweisTypen)

const { suggestions, onComplete } = useAutoComplete(searchFn)

function onModelValueChange(id: string | undefined) {
  selectedVerweisTypId.value = id
  const selectedVerweisTyp = verweisTypen.value.find((c: VerweisTyp) => c.id === id)
  emit('update:modelValue', selectedVerweisTyp?.name)
}

onMounted(async () => {
  const { data } = await useFetchVerweisTypen()
  verweisTypen.value = data.value?.verweisTypen || []
  if (modelValue.value) {
    const selectedRefType = verweisTypen.value.find((c: VerweisTyp) => c.name === modelValue.value)
    selectedVerweisTypId.value = selectedRefType?.id
  }
})
</script>

<template>
  <RisAutoComplete
    ref="autoComplete"
    :model-value="selectedVerweisTypId"
    :suggestions="suggestions"
    :input-id="inputId"
    :invalid="invalid"
    :initial-label="modelValue && `${verweisTypToLabel[modelValue]}`"
    :aria-label="ariaLabel"
    append-to="self"
    :placeholder="placeholder"
    typeahead
    dropdown
    complete-on-focus
    :auto-option-focus="!selectedVerweisTypId"
    @update:model-value="onModelValueChange"
    @complete="onComplete"
  />
</template>
