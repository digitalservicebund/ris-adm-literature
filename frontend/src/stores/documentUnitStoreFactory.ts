import { ref } from 'vue'
import type { UseFetchReturn } from '@vueuse/core'

/**
 * Generic factory function for creating a document-unit store.
 *
 * This abstracts common CRUD logic (load, update, unload) so it can be reused
 * for different document types (e.g. ADM, ULI, SLI). It expects two composable
 * functions (`getFn` and `updateFn`) that perform the actual API operations.
 *
 * @template T - The document model (e.g. AdmDocumentationUnit).
 *
 * @param getFn - A function that retrieves a document by its number.
 *                Must return an object conforming to `UseFetchReturn<T>`.
 *
 * @param updateFn - A function that updates an existing document.
 *                   Must return an object conforming to `UseFetchReturn<T>`.
 *
 * @returns An object containing reactive state and CRUD operations:
 *  - `documentUnit`: the currently loaded document (or `null`)
 *  - `isLoading`: boolean flag indicating loading state
 *  - `error`: holds any error that occurred during load or update
 *  - `load(documentNumber)`: loads a document by number
 *  - `update()`: updates the current document and returns `true`/`false`
 *  - `unload()`: clears the loaded document from memory
 */
export function defineDocumentUnitStore<T>(
  getFn: (documentNumber: string) => UseFetchReturn<T>,
  updateFn: (doc: T) => UseFetchReturn<T>,
) {
  const documentUnit = ref<T | null>(null)
  const isLoading = ref(false)
  const error = ref<Error | null>(null)

  async function load(documentNumber: string): Promise<void> {
    isLoading.value = true
    error.value = null

    const { data, error: fetchError, execute } = getFn(documentNumber)
    await execute()

    if (fetchError.value) {
      error.value = fetchError.value
      documentUnit.value = null
    } else {
      documentUnit.value = data.value ?? null
    }

    isLoading.value = false
  }

  async function update(): Promise<boolean> {
    if (!documentUnit.value) return false

    isLoading.value = true
    error.value = null

    const { data, error: putError, statusCode, execute } = updateFn(documentUnit.value)
    await execute()

    if (statusCode.value && statusCode.value >= 200 && statusCode.value < 300 && data.value) {
      documentUnit.value = data.value
      isLoading.value = false
      return true
    }

    error.value = putError.value || new Error('Update failed')
    isLoading.value = false
    return false
  }

  function unload() {
    documentUnit.value = null
  }

  return {
    documentUnit,
    isLoading,
    error,
    load,
    update,
    unload,
  }
}
