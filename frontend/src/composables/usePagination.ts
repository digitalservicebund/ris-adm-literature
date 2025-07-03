import { computed, ref } from 'vue'

/**
 * usePagination
 *
 * A composable for handling server-side paginated data fetching.
 * Manages pagination state, error state and triggers loading indicators.
 *
 * @template T - The type of the data items being paginated.
 * @template S - The type of the search parameters object used for filtering.
 *
 * @param {(page: number, itemsPerPage: number, searchParams?: S) => Promise<any>} fetchData
 *   Async function to fetch paginated data.
 *   It receives the page number, items per page, and the current search parameters.
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
 *   searchParams = ref<S | undefined>(),
 *   ITEMS_PER_PAGE: number,
 *   fetchPaginatedData: (page?: number) => Promise<void>,
 *   error: Ref<Error | null>,
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
  const error = ref<Error | null>(null)
  const pageNumber = ref<number>(0)
  const totalRows = ref<number>(0)
  const items = ref<T[]>([])
  const searchParams = ref<S | undefined>()
  const firstRowIndex = computed<number>(() => pageNumber.value * ITEMS_PER_PAGE)

  const fetchPaginatedData = async (page: number = 0, newSearch?: S) => {
    isLoading.value = true
    error.value = null

    if (newSearch !== undefined) {
      searchParams.value = newSearch
      page = 0
    }

    try {
      const { data, error: serverError } = await fetchData(page, ITEMS_PER_PAGE, searchParams.value)

      if (serverError) {
        error.value = serverError
      } else if (data) {
        items.value = data[paginatedResponseKey]
        totalRows.value = data.page.totalElements
        pageNumber.value = data.page.number
      }
    } catch (err) {
      error.value = err as Error
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
    error,
  }
}
