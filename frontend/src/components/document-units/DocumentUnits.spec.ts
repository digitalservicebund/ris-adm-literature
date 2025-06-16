import { fireEvent, render, screen } from '@testing-library/vue'
import { describe, expect, it, vi } from 'vitest'
import DocumentUnits from './DocumentUnits.vue'

const fetchPaginatedDataMock = vi.fn()

vi.mock('@/composables/usePagination', () => {
  return {
    usePagination: () => ({
      isLoading: false,
      firstRowIndex: 0,
      totalRows: 2,
      items: [
        { id: 1, name: 'Item 1' },
        { id: 2, name: 'Item 2' },
      ],
      ITEMS_PER_PAGE: 10,
      fetchPaginatedData: fetchPaginatedDataMock,
    }),
  }
})

function renderComponent(props = {}) {
  return {
    ...render(DocumentUnits, {
      props,
      global: {
        stubs: {
          DocumentUnitList: true,
          RisPaginator: {
            template:
              '<div data-testid="ris-paginator" @page="$emit(\'page\', { page: 1 })"></div>',
          },
          SearchPanel: {
            template:
              '<div data-testid="search-form" @search="$emit(\'search\', $event.detail)"></div>',
          },
        },
      },
    }),
  }
}

describe('DocumentUnits', () => {
  it('renders', async () => {
    const { container } = renderComponent()

    expect(container).toBeTruthy()
  })

  it('calls fetchPaginatedData on mount to load initial data', () => {
    // when
    renderComponent()
    // then
    expect(fetchPaginatedDataMock).toHaveBeenCalled()
    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(0, undefined)
  })

  it('calls fetchPaginatedData on page update', async () => {
    renderComponent()

    const risPaginator = screen.getByTestId('ris-paginator')
    // when
    await fireEvent(risPaginator, new CustomEvent('page', { detail: { page: 1 } }))
    // then
    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(1, undefined)
  })

  it('clears items and calls fetchPaginatedData with new search parameters on search', async () => {
    renderComponent()
    fetchPaginatedDataMock.mockClear()

    const searchForm = screen.getByTestId('search-form')
    const searchParams = { langueberschrift: 'test' }

    await fireEvent(searchForm, new CustomEvent('search', { detail: searchParams }))

    expect(fetchPaginatedDataMock).toHaveBeenCalled()

    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(0, searchParams)
  })
})
