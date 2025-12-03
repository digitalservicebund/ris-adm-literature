import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it, vi } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import AktivzitierungLiteratures from './AktivzitierungLiteratures.vue'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'
import { ref } from 'vue'
import { flushPromises } from '@vue/test-utils'

const addToastMock = vi.fn()
vi.mock('primevue', () => ({
  useToast: vi.fn(() => ({
    add: addToastMock,
  })),
}))

const mockAktivzitierungen: AktivzitierungLiterature[] = [
  {
    id: 'aktiv-1',
    uuid: 'aktiv-1',
    veroeffentlichungsjahr: '2025',
    verfasser: ['again and again'],
    dokumenttypen: [{ uuid: 'Ebs', abbreviation: 'Ebs', name: 'Ebs' }],
    titel: 'a new one',
  },
]

function renderComponent(aktivzitierungLiteratures: AktivzitierungLiterature[] = []) {
  const user = userEvent.setup()

  return {
    user,
    ...render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                sliDocumentUnit: {
                  documentUnit: <SliDocumentationUnit>{
                    id: '123',
                    documentNumber: 'KSLS2025000001',
                    note: '',
                    aktivzitierungenSli: aktivzitierungLiteratures,
                  },
                },
              },
            }),
          ],
        ],
      },
    }),
  }
}

describe('AktivzitierungLiteratures', () => {
  it('renders creation panel when there are no aktivzitierung entries', () => {
    renderComponent()

    // Component wrapper from AktivzitierungLiteratures.vue
    expect(screen.getByLabelText('Aktivzitierung')).toBeInTheDocument()

    // No list items yet
    expect(screen.queryAllByRole('listitem')).toHaveLength(0)

    // Creation form from AktivzitierungLiteratureInput is visible
    expect(screen.getByLabelText('Hauptsachtitel / Dokumentarischer Titel')).toBeInTheDocument()

    // Add button should not be visible in this state
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung hinzufügen' }),
    ).not.toBeInTheDocument()
  })

  it('renders a list of existing aktivzitierung entries', () => {
    renderComponent(mockAktivzitierungen)

    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('2025, again and again, (Ebs)')).toBeInTheDocument()
    expect(screen.getByText('a new one')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung Editieren' })).toBeInTheDocument()
  })

  it('opens the creation panel when clicking the add button', async () => {
    const { user } = renderComponent(mockAktivzitierungen)

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' }))

    expect(screen.getByLabelText('Hauptsachtitel / Dokumentarischer Titel')).toBeInTheDocument()
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung hinzufügen' }),
    ).not.toBeInTheDocument()
  })

  it('shows creation panel when list is empty', () => {
    renderComponent([])
    expect(screen.getByLabelText('Aktivzitierung übernehmen')).toBeInTheDocument()
  })
  it('hides creation input and shows add button when list has entries and panel is closed', () => {
    const mockAktivzitierung: AktivzitierungLiterature = {
      id: '1',
      titel: 'Titel',
    }

    renderComponent([mockAktivzitierung])
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung übernehmen' }),
    ).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' })).toBeVisible()
  })

  it('adds a new entry via onAddItem and updates the list', async () => {
    const user = userEvent.setup()

    render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              sliDocumentUnit: {
                documentUnit: <SliDocumentationUnit>{
                  id: '123',
                  documentNumber: 'KSLS2025000001',
                  note: '',
                  aktivzitierungenSli: [],
                },
              },
            },
          }),
        ],
        stubs: {
          AktivzitierungLiteratureInput: {
            template:
              "<button aria-label=\"Fake add\" @click=\"$emit('update-aktivzitierung-literature', { id: '1', titel: 'Neu' })\"></button>",
          },
        },
      },
    })

    screen.debug()

    expect(screen.queryAllByRole('listitem')).toHaveLength(0)

    await user.click(screen.getByRole('button', { name: 'Fake add' }))

    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('Neu')).toBeInTheDocument()
  })

  it('closes the creation panel when cancel is emitted from the input', async () => {
    const user = userEvent.setup()

    const existing: AktivzitierungLiterature = {
      id: '1',
      uuid: '1',
      titel: 'Titel',
    }

    render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              sliDocumentUnit: {
                documentUnit: <SliDocumentationUnit>{
                  id: '123',
                  documentNumber: 'KSLS2025000001',
                  note: '',
                  aktivzitierungenSli: [existing],
                },
              },
            },
          }),
        ],
        stubs: {
          AktivzitierungLiteratureInput: {
            template: '<button aria-label="Fake cancel" @click="$emit(\'cancel\')"></button>',
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' }))
    expect(screen.getByRole('button', { name: 'Fake cancel' })).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Fake cancel' }))

    expect(screen.queryByRole('button', { name: 'Fake cancel' })).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' })).toBeVisible()
  })

  it('triggers a search when clicking on search, shows the results and the paginator, navigates to next result page', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(20),
          items: ref(
            Array.from({ length: 20 }, (_, index) => ({
              id: `searchResultId${index + 1}`,
            })),
          ),
          fetchPaginatedData: fetchPaginatedDataMock,
          error: null,
        }),
      }
    })

    const { default: AktivzitierungLiteratures } = await import('./AktivzitierungLiteratures.vue')

    const user = userEvent.setup()

    render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              sliDocumentUnit: {
                documentUnit: <SliDocumentationUnit>{
                  id: '123',
                  documentNumber: 'KSLS2025000001',
                  note: '',
                  aktivzitierungenSli: [],
                },
              },
            },
          }),
        ],
        stubs: {
          AktivzitierungLiteratureInput: {
            template:
              '<button aria-label="Fake search" @click="$emit(\'search\', { titel: \'searched titel\' })"></button>',
          },
        },
      },
    })

    expect(screen.queryByText('Passende Suchergebnisse:')).not.toBeInTheDocument()

    // when
    await user.click(screen.getByRole('button', { name: 'Fake search' }))
    // then
    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(0, { titel: 'searched titel' })
    expect(screen.getByText('Passende Suchergebnisse:')).toBeInTheDocument()
    expect(screen.queryAllByRole('listitem')).toHaveLength(20)
    expect(screen.getByLabelText('Zurück')).toBeInTheDocument()
    expect(screen.getByText('Seite 1')).toBeInTheDocument()
    expect(screen.getByLabelText('Weiter')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Weiter'))
    // then
    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(1, { titel: 'searched titel' })
    expect(screen.getByText('Seite 2')).toBeInTheDocument()
  })

  it('should show an error toast on fetching error', async () => {
    vi.resetModules()
    const errorRef = ref()

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(2),
          items: ref([]),
          ITEMS_PER_PAGE: ref(10),
          fetchPaginatedData: vi.fn(),
          error: errorRef,
        }),
      }
    })

    const { default: AktivzitierungLiteratures } = await import('./AktivzitierungLiteratures.vue')

    render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              sliDocumentUnit: {
                documentUnit: <SliDocumentationUnit>{
                  id: '123',
                  documentNumber: 'KSLS2025000001',
                  note: '',
                  aktivzitierungenSli: [],
                },
              },
            },
          }),
        ],
      },
    })

    errorRef.value = new Error('fetch error')
    await flushPromises()

    expect(addToastMock).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Dokumentationseinheiten konnten nicht geladen werden.',
    })
  })

  it('aktivzitierung list is empty when there is no existing aktivzitierungen', async () => {
    render(AktivzitierungLiteratures, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: {
              sliDocumentUnit: {
                documentUnit: <SliDocumentationUnit>{
                  id: '123',
                  documentNumber: 'KSLS2025000001',
                  note: '',
                },
              },
            },
          }),
        ],
      },
    })

    expect(screen.queryByRole('list', { name: 'Aktivzitierung Liste' })).not.toBeInTheDocument()
  })
})
