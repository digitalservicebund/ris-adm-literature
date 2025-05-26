import { describe, it, expect, vi, beforeEach } from 'vitest'
import { usePagination } from './usePagination'

const mockData = {
  data: {
    documentationUnitsOverview: [
      { id: 1, name: 'Item 1' },
      { id: 2, name: 'Item 2' },
    ],
    page: {
      totalElements: 2,
      number: 0,
    },
  },
}

const mockFetchData = vi.fn(() => {
  return Promise.resolve(mockData)
})

describe('usePagination', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should fetch paginated data and update state', async () => {
    const { isLoading, firstRowIndex, totalRows, items, fetchPaginatedData } =
      usePagination(mockFetchData)

    expect(isLoading.value).toBe(true)
    expect(items.value).toEqual([])

    await fetchPaginatedData()

    expect(isLoading.value).toBe(false)
    expect(items.value).toEqual(mockData.data.documentationUnitsOverview)
    expect(totalRows.value).toBe(mockData.data.page.totalElements)
    expect(firstRowIndex.value).toBe(mockData.data.page.number * 100)
    expect(mockFetchData).toHaveBeenCalledWith(0, 100)
  })

  it('should handle errors gracefully', async () => {
    const errorFetchData = vi.fn(() => {
      return Promise.reject(new Error('Failed to fetch data'))
    })

    const { isLoading, items, totalRows, firstRowIndex, fetchPaginatedData } =
      usePagination(errorFetchData)

    expect(isLoading.value).toBe(true)
    expect(items.value).toEqual([])

    await expect(fetchPaginatedData()).rejects.toThrow('Failed to fetch data')

    expect(isLoading.value).toBe(false)
    expect(items.value).toEqual([])
    expect(totalRows.value).toEqual(0)
    expect(firstRowIndex.value).toEqual(0)
  })
})
