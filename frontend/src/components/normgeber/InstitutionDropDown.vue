<script setup lang="ts">
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { fetchInstitutions } from '@/services/institutionService.ts'
import { type Institution } from '@/domain/normgeber.ts'
import { useAutoComplete } from '@/composables/useAutoComplete'

defineProps<{ isInvalid: boolean }>()

const modelValue = defineModel<Institution | undefined>()
const emit = defineEmits<{
  'update:modelValue': [value: Institution]
}>()

const autoComplete = ref<typeof RisAutoComplete | null>(null)
const institutions = ref<Institution[]>([])
const selectedInstitutionId = ref<string | undefined>(modelValue.value?.id)

const searchFn = (query?: string) => {
  return institutions.value
    .filter((inst) => !query || inst.name.toLowerCase().includes(query.toLowerCase()))
    .map((inst) => ({
      id: inst.id,
      label: inst.name,
      secondaryLabel: inst.officialName,
    }))
}

const { suggestions, onComplete, onDropdownClick, onItemSelect } = useAutoComplete(searchFn)

function onModelValueChange(id: string | undefined) {
  selectedInstitutionId.value = id
  const selectedInstitution = institutions.value.find((inst: Institution) => inst.id === id)
  if (selectedInstitution) {
    emit('update:modelValue', selectedInstitution)
  }
}

onMounted(async () => {
  const response = await fetchInstitutions()
  institutions.value = response.data.value?.institutions || []
})
</script>

<template>
  <RisAutoComplete
    ref="autoComplete"
    :model-value="selectedInstitutionId"
    :suggestions="suggestions"
    :invalid="isInvalid"
    :initial-label="modelValue?.name"
    input-id="institution"
    aria-label="Normgeber"
    append-to="self"
    typeahead
    dropdown
    dropdown-mode="blank"
    @update:model-value="onModelValueChange"
    @complete="onComplete"
    @dropdown-click="onDropdownClick"
    @item-select="onItemSelect"
  />
</template>
