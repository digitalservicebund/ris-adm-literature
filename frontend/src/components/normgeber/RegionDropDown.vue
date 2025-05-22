<script setup lang="ts">
import type { AutoCompleteDropdownClickEvent } from 'primevue/autocomplete'
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import type { Region } from '@/domain/normgeber.ts'
import { fetchRegions } from '@/services/regionService.ts'

const modelValue = defineModel<Region | undefined>()
const emit = defineEmits<{
  'update:modelValue': [value: Region]
}>()

const autoComplete = ref<typeof RisAutoComplete | null>(null)

// Should we exported from ris-ui
interface AutoCompleteSuggestion {
    id: string;
    label: string;
    secondaryLabel?: string;
}
const suggestions = ref<AutoCompleteSuggestion[]>([])

const regions = ref<Region[]>([])
const selectedRegionId = ref<string | undefined>(modelValue.value?.id)

const search = async (query?: string) => {
  suggestions.value = regions.value
    .filter((region: Region) => !query || region.code.toLowerCase().includes(query.toLowerCase()))
    .map((region: Region) => ({
      id: region.id,
      label: region.code,
      secondaryLabel: region.longText,
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
    // normal search for entered prefix
    searchDebounced(event.query)
  } else if (modelValue.value) {
    // user has already made a selection, use that as the prefix
    searchDebounced(modelValue.value?.code)
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
  selectedRegionId.value = id
  const selectedRegion = regions.value.filter((region: Region) => region.id === id)[0]
  emit('update:modelValue', selectedRegion)
}

onMounted(async () => {
  const response = await fetchRegions()
  regions.value = response.data.value?.regions || []
})
</script>

<template>
  <RisAutoComplete
    ref="autoComplete"
    :model-value="selectedRegionId"
    :suggestions="suggestions"
    input-id="region"
    :initial-label="modelValue?.code"
    aria-label="Region"
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
