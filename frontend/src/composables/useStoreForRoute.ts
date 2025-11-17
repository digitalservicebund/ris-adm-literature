import { useRoute } from 'vue-router'
import { useAdmDocUnitStore } from '@/stores/admDocumentUnitStore'
import { useUliDocumentUnitStore } from '@/stores/uliDocStore'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import { DocumentCategory } from '@/domain/documentType'

const storeMap = {
  [DocumentCategory.VERWALTUNGSVORSCHRIFTEN]: useAdmDocUnitStore,
  [DocumentCategory.LITERATUR_UNSELBSTSTAENDIG]: useUliDocumentUnitStore,
  [DocumentCategory.LITERATUR_SELBSTSTAENDIG]: useSliDocumentUnitStore,
} as const

type StoreMap = typeof storeMap

/**
 * Returns the Pinia store associated with the current route.
 * Requires route.meta.documentCategory to be set.
 */
export function useStoreForRoute<T>() {
  const route = useRoute()
  const documentCategory = route.meta.documentCategory as keyof StoreMap
  const factory = storeMap[documentCategory]

  if (!factory)
    throw new Error(`No store found for route meta.documentCategory="${documentCategory}"`)

  return factory() as T
}
