import { useRoute } from 'vue-router'
import { useAdmDocUnitStore } from '@/stores/admDocumentUnitStore'
import { useUliDocumentUnitStore } from '@/stores/uliDocStore'

const storeMap = {
  admDocumentUnit: useAdmDocUnitStore,
  uliDocumentUnit: useUliDocumentUnitStore,
} as const

type StoreId = keyof typeof storeMap

/**
 * Returns the Pinia store associated with the current route.
 * Requires route.meta.storeId to be set.
 */
export function useStoreForRoute<T>() {
  const route = useRoute()
  const storeId = route.meta.storeId as StoreId
  const factory = storeMap[storeId]

  if (!factory) throw new Error(`No store found for route meta.storeId="${storeId}"`)

  return factory() as T
}
