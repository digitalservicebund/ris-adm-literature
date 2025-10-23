import { useRoute } from 'vue-router'
import { useAdmDocUnitStore } from '@/stores/admDocumentUnitStore'
import { useUliDocumentUnitStore } from '@/stores/uliDocStore'
import { DocumentTypeCode } from '@/domain/documentType'

const storeMap = {
  [DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN]: useAdmDocUnitStore,
  [DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG]: useUliDocumentUnitStore,
} as const

type StoreMap = typeof storeMap

/**
 * Returns the Pinia store associated with the current route.
 * Requires route.meta.documentTypeCode to be set.
 */
export function useStoreForRoute<T>() {
  const route = useRoute()
  const documentTypeCode = route.meta.documentTypeCode as keyof StoreMap
  const factory = storeMap[documentTypeCode]

  if (!factory)
    throw new Error(`No store found for route meta.documentTypeCode="${documentTypeCode}"`)

  return factory() as T
}
