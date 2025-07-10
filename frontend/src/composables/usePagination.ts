import type { UseFetchReturn } from '@vueuse/core'
import { computed, ref, type Ref } from 'vue'

/**
 * usePagination
 *
 * A composable for handling server-side paginated data fetching.
 * Manages pagination state, error state and triggers loading indicators.
 *
 * @template T - The type of the data items being paginated.
 * @template S - The type of the search parameters object used for filtering.
 *
 * @param {(page: Ref<number>, itemsPerPage: number, searchParams?: Ref<S>) => UseFetchReturn<any>} fetchData
 *   Function to fetch paginated data.
 *   It receives the page number, items per page, and the current search parameters.
 *   Must return a UseFetchReturn object with `data` and `error` refs.
 *
 * @param {string} paginatedResponseKey
 *   The key in the response `data` object that contains the array of items (e.g., 'documents').
 *
 * @returns {{
 *   isFetching: Ref<boolean>,
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
  fetchData: (
    page: Ref<number>,
    itemsPerPage: number,
    searchParams: Ref<S | undefined>,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  ) => UseFetchReturn<any>,
  paginatedResponseKey: string,
) {
  const ITEMS_PER_PAGE = 100
  const pageNumber = ref<number>(0)
  const searchParams = ref<S | undefined>()

  const { data, error, isFetching, execute } = fetchData(pageNumber, ITEMS_PER_PAGE, searchParams)

  const items = computed<T[]>(() => data.value?.[paginatedResponseKey] || [])
  const totalRows = computed<number>(() => data.value?.page?.totalElements ?? 0)
  const firstRowIndex = computed<number>(() => pageNumber.value * ITEMS_PER_PAGE)

  const fetchPaginatedData = async (page: number = 0, newSearch?: S) => {
    if (newSearch !== undefined) {
      searchParams.value = newSearch
      page = 0
    } else {
      pageNumber.value = page
    }

    await execute()
  }

  return {
    isFetching,
    firstRowIndex,
    totalRows,
    items,
    ITEMS_PER_PAGE,
    fetchPaginatedData,
    error,
  }
}
