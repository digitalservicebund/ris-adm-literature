import type { DocumentType, DocumentCategory } from '@/domain/documentType'
import { useApiFetch } from '@/services/apiService'
import { buildUrlWithParams } from '@/utils/urlHelpers'
import type { UseFetchReturn } from '@vueuse/core'
import { computed } from 'vue'

const DOCUMENT_TYPES_URL = '/lookup-tables/document-types'

export function useFetchDocumentTypes(
  documentCategory: DocumentCategory,
): UseFetchReturn<{ documentTypes: DocumentType[] }> {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${DOCUMENT_TYPES_URL}`, {
      usePagination: false,
      documentCategory,
    }),
  )
  return useApiFetch(urlWithParams).json()
}
