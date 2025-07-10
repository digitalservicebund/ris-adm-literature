import { ref } from 'vue'
import { describe, it, expect, vi } from 'vitest'
import { usePagination } from '@/composables/usePagination'

const mockData = {
  documentationUnitsOverview: [
    { id: 1, title: 'Doc 1' },
    { id: 2, title: 'Doc 2' },
  ],
  page: {
    totalElements: 2,
    number: 0,
  },
}

describe('usePagination', () => {
  it('should initialize with default values', () => {
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: vi.fn().mockResolvedValue(undefined),
    })

    const { items, totalRows, firstRowIndex, ITEMS_PER_PAGE } = usePagination(
      mockFetchData,
      'documentationUnitsOverview',
    )

    expect(items.value.length).toBe(2)
    expect(totalRows.value).toBe(2)
    expect(firstRowIndex.value).toBe(0)
    expect(ITEMS_PER_PAGE).toBe(100)
  })

  it('should call execute when fetchPaginatedData is called', async () => {
    const executeSpy = vi.fn().mockResolvedValue(undefined)
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: executeSpy,
    })

    const { fetchPaginatedData } = usePagination(mockFetchData, 'documentationUnitsOverview')

    await fetchPaginatedData(1)

    expect(executeSpy).toHaveBeenCalled()
  })

  it('should reset page to 0 and update searchParams when new search is provided', async () => {
    const executeSpy = vi.fn().mockResolvedValue(undefined)
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: executeSpy,
    })

    const { fetchPaginatedData } = usePagination(mockFetchData, 'documentationUnitsOverview')

    const newSearch = { documentNumber: 'abc' }

    await fetchPaginatedData(5, newSearch)

    // Because new search resets page to 0
    expect(mockFetchData).toHaveBeenCalledWith(expect.anything(), 100, expect.anything())
    expect(executeSpy).toHaveBeenCalled()
  })

  it('should expose the error when fetch fails', async () => {
    const mockError = new Error('Something went wrong')

    const executeSpy = vi.fn().mockResolvedValue(undefined)
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(null),
      error: ref(mockError),
      isFetching: ref(false),
      execute: executeSpy,
    })

    const { error, fetchPaginatedData } = usePagination(mockFetchData, 'documentationUnitsOverview')

    await fetchPaginatedData()

    expect(error.value).toBe(mockError)
    expect(executeSpy).toHaveBeenCalled()
  })
})
