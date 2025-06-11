import { useToast } from 'primevue'
import { computed, ref } from 'vue'
import errorMessages from '@/i18n/errors.json'

/**
 * usePagination
 *
 * A composable for handling server-side paginated data fetching.
 * Manages pagination state, triggers loading indicators, and displays toast notifications on error.
 *
 * @template T - The type of the data items being paginated.
 *
 * @param {function(page: number, itemsPerPage: number): Promise<any>} fetchData
 *   Async function to fetch paginated data.
 *   Must return a Promise that resolves with an object containing either `data` or `error`.
 *
 * @param {string} paginatedResponseKey
 *   The key in the response `data` object that contains the array of items (e.g., 'documents').
 *
 * @returns {{
 *   isLoading: Ref<boolean>,
 *   firstRowIndex: ComputedRef<number>,
 *   totalRows: Ref<number>,
 *   items: Ref<T[]>,
 *   ITEMS_PER_PAGE: number,
 *   fetchPaginatedData: (page?: number) => Promise<void>
 * }}
 *
 * Example response format expected from fetchData:
 * {
 *   data?: {
 *     [key: string]: T[], // e.g., documents
 *     page: {
 *       totalElements: number,
 *       number: number
 *     }
 *   },
 *   error?: unknown
 * }
 */
export function usePagination<T, S>(
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  fetchData: (page: number, itemsPerPage: number, searchParams?: S) => Promise<any>,
  paginatedResponseKey: string,
) {
  const ITEMS_PER_PAGE = 100
  const isLoading = ref(true)
  const pageNumber = ref<number>(0)
  const totalRows = ref<number>(0)
  const items = ref<T[]>([])
  const searchParams = ref<S | undefined>()
  const firstRowIndex = computed<number>(() => pageNumber.value * ITEMS_PER_PAGE)
  const toast = useToast()
  const fetchPaginatedData = async (page: number = 0, newSearch?: S) => {
    isLoading.value = true

    if (newSearch !== undefined) {
      searchParams.value = newSearch
      page = 0
    }

    try {
      const { data, error } = await fetchData(page, ITEMS_PER_PAGE, searchParams.value)

      if (error) {
        // Showing server error
        console.error(error)
        toast.add({
          severity: 'error',
          summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
        })
      } else if (data) {
        items.value = data[paginatedResponseKey]
        totalRows.value = data.page.totalElements
        pageNumber.value = data.page.number
      }
    } catch (error) {
      // Showing promise/network error
      console.error(error)
      toast.add({
        severity: 'error',
        summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
      })
    } finally {
      isLoading.value = false
    }
  }

  return {
    isLoading,
    firstRowIndex,
    totalRows,
    items,
    ITEMS_PER_PAGE,
    fetchPaginatedData,
  }
}
