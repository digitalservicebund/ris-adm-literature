<script setup lang="ts">
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { useAutoComplete, useNormAbbreviationsSearch } from '@/composables/useAutoComplete'
import type { NormAbbreviation } from '@/domain/normAbbreviation'
import { useFetchNormAbbreviations } from '@/services/normAbbreviationService'

defineProps<{
  inputId: string
  invalid: boolean
}>()

const modelValue = defineModel<NormAbbreviation | undefined>()
const emit = defineEmits<{
  'update:modelValue': [value: NormAbbreviation | undefined]
}>()

const autoComplete = ref<typeof RisAutoComplete | null>(null)
const normAbbreviations = ref<NormAbbreviation[]>([])
const selectedNormAbbrId = ref<string | undefined>(modelValue.value?.id)

const searchFn = useNormAbbreviationsSearch(normAbbreviations)

const { suggestions, onComplete } = useAutoComplete(searchFn)

function onModelValueChange(id: string | undefined) {
  selectedNormAbbrId.value = id
  const selectedAbbr = normAbbreviations.value.find((c: NormAbbreviation) => c.id === id)
  emit('update:modelValue', selectedAbbr)
}

onMounted(async () => {
  const { data } = await useFetchNormAbbreviations()
  normAbbreviations.value = data.value?.normAbbreviations || []
})
</script>

<template>
  <RisAutoComplete
    ref="autoComplete"
    :model-value="selectedNormAbbrId"
    :suggestions="suggestions"
    :input-id="inputId"
    :invalid="invalid"
    :initial-label="modelValue && `${modelValue.abbreviation}`"
    aria-label="RIS-Abkürzung"
    append-to="self"
    typeahead
    dropdown
    complete-on-focus
    :auto-option-focus="!selectedNormAbbrId"
    @update:model-value="onModelValueChange"
    @complete="onComplete"
  />
</template>
