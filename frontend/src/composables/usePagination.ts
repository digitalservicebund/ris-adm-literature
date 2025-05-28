import { computed, ref } from 'vue'

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function usePagination<T>(fetchData: (page: number, itemsPerPage: number) => Promise<any>) {
  const ITEMS_PER_PAGE = 100
  const isLoading = ref(true)
  const pageNumber = ref<number>(0)
  const totalRows = ref<number>(0)
  const items = ref<T[]>([])
  const firstRowIndex = computed<number>(() => pageNumber.value * ITEMS_PER_PAGE)

  const fetchPaginatedData = async (page: number = 0) => {
    isLoading.value = true
    try {
      const response = await fetchData(page, ITEMS_PER_PAGE)
      if (response.data) {
        // Parametrize the documentationUnitsOverview key
        items.value = response.data.documentationUnitsOverview
        totalRows.value = response.data.page.totalElements
        pageNumber.value = response.data.page.number
      }
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
