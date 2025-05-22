<script setup lang="ts">
import type { AutoCompleteDropdownClickEvent } from 'primevue/autocomplete'
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import { fetchInstitutions } from '@/services/institutionService.ts'
import { InstitutionType, type Institution } from '@/domain/normgeber.ts'

defineProps<{ isInvalid: boolean }>()

const modelValue = defineModel<Institution | undefined>()
const emit = defineEmits<{
  'update:modelValue': [value: Institution]
}>()

const autoComplete = ref<typeof RisAutoComplete | null>(null)

// Should be exported from ris-ui
interface AutoCompleteSuggestion {
    id: string;
    label: string;
    secondaryLabel?: string;
}const suggestions = ref<AutoCompleteSuggestion[]>([])

const institutions = ref<Institution[]>([])
const selectedInstitutionId = ref<string | undefined>(modelValue.value?.id)

const search = async (query?: string) => {
  suggestions.value = institutions.value
    .filter(
      (institution: Institution) =>
        !query || institution.name.toLowerCase().includes(query.toLowerCase()),
    )
    .map((institution: Institution) => ({
      id: institution.id,
      label: institution.name,
      secondaryLabel: institution.officialName,
    }))
}

const searchDebounced = useDebounceFn(search, 250)

/*
Workaround for loading prop being ignored in PrimeVue AutoComplete:
It is important that the suggestions.value be updated each time. Otherwise, the loading indicator will not disappear
the second time that the default suggestions are invoked using the dropdown.

Both onComplete and onDropdownClick are called when the dropdown is opened,
but only onDropdownClick is called on close.

See https://github.com/primefaces/primevue/issues/5601 for further information.
 */
const onComplete = (event: AutoCompleteDropdownClickEvent | { query: undefined }) => {
  if (event.query) {
    // normal search for entered query
    searchDebounced(event.query)
  } else if (modelValue.value) {
    // user has already made a selection, use that as the query
    searchDebounced(modelValue.value?.name)
  } else {
    // dropdown was opened without any text entered or value pre-selected
    // a copy of the default suggestions is required since the loading
    searchDebounced()
  }
}

const onDropdownClick = (event: AutoCompleteDropdownClickEvent | { query: undefined }) => {
  if (event.query === undefined) {
    // dropdown has been closed
    suggestions.value = []
  } else {
    // onComplete will also fire, but with an empty query
    // therefore, call it again
    onComplete(event)
  }
}

const onItemSelect = () => {
  suggestions.value = []
}

function onModelValueChange(id: string | undefined) {
  selectedInstitutionId.value = id
  const selectedInstitution = institutions.value.filter(
    (institution: Institution) => institution.id === id,
  )[0]
  emit('update:modelValue', selectedInstitution)
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
