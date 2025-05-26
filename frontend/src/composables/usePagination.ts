import { ref } from 'vue'

const ITEMS_PER_PAGE = 100

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function usePagination<T>(fetchData: (page: number, itemsPerPage: number) => Promise<any>) {
  const isLoading = ref(true)
  const firstRowIndex = ref<number>(0)
  const totalRows = ref<number>(0)
  const items = ref<T[]>([])

  const fetchPaginatedData = async (page: number = 0) => {
    isLoading.value = true
    try {
      const response = await fetchData(page, ITEMS_PER_PAGE)
      items.value = response.data?.documentationUnitsOverview || []
      totalRows.value = response.data?.page.totalElements || 0
      firstRowIndex.value = response.data?.page.number || 0
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
