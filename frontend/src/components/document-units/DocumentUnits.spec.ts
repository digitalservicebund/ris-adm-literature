import { fireEvent, render, screen } from '@testing-library/vue'
import { describe, expect, it, vi } from 'vitest'
import DocumentUnits from './DocumentUnits.vue'
import { ref } from 'vue'
import { flushPromises } from '@vue/test-utils'

const fetchPaginatedDataMock = vi.fn()

vi.mock('@/composables/usePagination', () => {
  return {
    usePagination: () => ({
      isLoading: ref(false),
      firstRowIndex: ref(0),
      totalRows: ref(2),
      items: ref([
        { id: 1, name: 'Item 1' },
        { id: 2, name: 'Item 2' },
      ]),
      ITEMS_PER_PAGE: ref(10),
      fetchPaginatedData: fetchPaginatedDataMock,
      error: ref(null),
    }),
  }
})

const addToastMock = vi.fn()
vi.mock('primevue', () => ({
  useToast: vi.fn(() => ({
    add: addToastMock,
  })),
}))

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
    expect(addToastMock).not.toHaveBeenCalled()
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

  it('should show an error toast on fetching error', async () => {
    vi.resetModules()
    const errorRef = ref(new Error('fetch error'))

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isLoading: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(2),
          items: ref([]),
          ITEMS_PER_PAGE: ref(10),
          fetchPaginatedData: vi.fn(async () => {
            errorRef.value = new Error('fetch error')
          }),
          error: errorRef,
        }),
      }
    })

    const { default: DocumentUnits } = await import('./DocumentUnits.vue')

    render(DocumentUnits, {
      global: {
        stubs: {
          DocumentUnitList: true,
          RisPaginator: true,
          SearchPanel: true,
        },
      },
    })

    await flushPromises()

    expect(addToastMock).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Dokumentationseinheiten konnten nicht geladen werden.',
    })
  })
})
