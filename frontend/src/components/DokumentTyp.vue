<script setup lang="ts">
import { RisAutoCompleteMultiple } from '@digitalservicebund/ris-ui/components'
import { computed, onMounted, ref } from 'vue'
import {
  useAutoComplete,
  useDokumentTypSearch,
  type AutoCompleteSuggestion,
} from '@/composables/useAutoComplete'
import { useFetchDocumentTypes } from '@/services/documentTypeService'
import type { DocumentCategory, DocumentType } from '@/domain/documentType'

const props = defineProps<{
  inputId: string
  invalid: boolean
  ariaLabel?: string
  placeholder?: string
  documentCategory: DocumentCategory
}>()

const model = defineModel<DocumentType[]>()

const selectedItems = computed<AutoCompleteSuggestion[]>({
  get: () =>
    model.value?.map((docType) => ({
      id: docType.abbreviation,
      label: docType.abbreviation,
      secondaryLabel: docType.name,
    })) ?? [],
  set: (selectedItems) => {
    model.value = selectedItems.map((item) => ({
      uuid: item.id,
      abbreviation: item.label,
      name: item.secondaryLabel,
    })) as DocumentType[]
  },
})

const autoComplete = ref<typeof RisAutoCompleteMultiple | null>(null)
const documentTypeOptions = ref<DocumentType[]>([])

const searchFn = useDokumentTypSearch(documentTypeOptions)

const { suggestions, onComplete } = useAutoComplete(searchFn)

onMounted(async () => {
  const { data } = await useFetchDocumentTypes(props.documentCategory)
  documentTypeOptions.value = data.value?.documentTypes || []
})
</script>

<template>
  <RisAutoCompleteMultiple
    ref="autoComplete"
    v-model="selectedItems"
    :suggestions="suggestions"
    :input-id="inputId"
    :aria-label="ariaLabel"
    append-to="self"
    :placeholder="placeholder"
    typeahead
    dropdown
    fluid
    complete-on-focus
    auto-option-focus
    @complete="onComplete"
  />
</template>
