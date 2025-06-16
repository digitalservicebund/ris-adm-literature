import { beforeEach, describe, expect, it, vi } from 'vitest'
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

const addToastMock = vi.fn()
vi.mock('primevue', () => ({
  useToast: vi.fn(() => ({
    add: addToastMock,
  })),
}))

describe('usePagination', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should fetch paginated data and update state', async () => {
    const { isLoading, firstRowIndex, totalRows, items, fetchPaginatedData } = usePagination(
      mockFetchData,
      'documentationUnitsOverview',
    )

    expect(isLoading.value).toBe(true)
    expect(items.value).toEqual([])

    await fetchPaginatedData()

    expect(isLoading.value).toBe(false)
    expect(items.value).toEqual(mockData.data.documentationUnitsOverview)
    expect(totalRows.value).toBe(mockData.data.page.totalElements)
    expect(firstRowIndex.value).toBe(mockData.data.page.number * 100)
    expect(mockFetchData).toHaveBeenCalledWith(0, 100, undefined)
  })

  it('should reset to page 0 and fetch with new search parameters', async () => {
    const { fetchPaginatedData } = usePagination(mockFetchData, 'documentationUnitsOverview')
    const searchParams = { documentNumber: '12345', langueberschrift: 'Test Title' }

    await fetchPaginatedData(5, searchParams)

    expect(mockFetchData).toHaveBeenCalledWith(0, 100, searchParams)
  })

  it('should show an error toast when there is a fetching error', async () => {
    const fetchDataMock = vi.fn().mockRejectedValue(new Error('Fetch failed'))

    const { isLoading, items, totalRows, firstRowIndex, fetchPaginatedData } = usePagination(
      fetchDataMock,
      'documentationUnitsOverview',
    )

    // Assert initial test
    expect(isLoading.value).toBe(true)
    expect(items.value).toEqual([])

    // When
    await fetchPaginatedData()

    // Then
    expect(isLoading.value).toBe(false)
    expect(items.value).toEqual([])
    expect(totalRows.value).toEqual(0)
    expect(firstRowIndex.value).toEqual(0)
    expect(addToastMock).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Dokumentationseinheiten konnten nicht geladen werden.',
    })
  })

  it('should show an error toast when the BE returns an error', async () => {
    const fetchDataMock = vi.fn().mockResolvedValue({
      error: 'Error from server',
    })

    const { fetchPaginatedData } = usePagination(fetchDataMock, 'documentationUnitsOverview')

    // When
    await fetchPaginatedData()

    // Then
    expect(addToastMock).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Dokumentationseinheiten konnten nicht geladen werden.',
    })
  })
})
