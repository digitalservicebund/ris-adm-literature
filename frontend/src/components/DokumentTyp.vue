<script setup lang="ts">
import { RisAutoComplete } from '@digitalservicebund/ris-ui/components'
import { onMounted, ref } from 'vue'
import { useAutoComplete, useDokumentTypSearch } from '@/composables/useAutoComplete'
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

const autoComplete = ref<typeof RisAutoComplete | null>(null)
const documentTypes = ref<DocumentType[]>([])

const searchFn = useDokumentTypSearch(documentTypes)

const { suggestions, onComplete } = useAutoComplete(searchFn)

onMounted(async () => {
  const { data } = await useFetchDocumentTypes(props.documentCategory)
  documentTypes.value = data.value?.documentTypes || []
})
</script>

<template>
  <RisAutoComplete
    ref="autoComplete"
    v-model="model"
    :suggestions="suggestions"
    :input-id="inputId"
    :aria-label="ariaLabel"
    append-to="self"
    :placeholder="placeholder"
    typeahead
    dropdown
    complete-on-focus
    @complete="onComplete"
  />
</template>
