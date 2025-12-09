import { userEvent } from '@testing-library/user-event'
import { render, screen, within } from '@testing-library/vue'
import { describe, expect, it, vi } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import AktivzitierungLiteratures from './AktivzitierungLiteratures.vue'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature'
import { ref } from 'vue'
import { flushPromises } from '@vue/test-utils'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'

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
    veroeffentlichungsJahr: '2025',
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
    expect(screen.queryByRole('button', { name: 'Weitere Angabe' })).not.toBeInTheDocument()
  })

  it('renders a list of existing aktivzitierung entries', () => {
    renderComponent(mockAktivzitierungen)

    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('2025, again and again (Ebs)')).toBeInTheDocument()
    expect(screen.getByText('a new one')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Eintrag bearbeiten' })).toBeInTheDocument()
  })

  it('opens the creation panel when clicking the add button', async () => {
    const { user } = renderComponent(mockAktivzitierungen)

    await user.click(screen.getByRole('button', { name: 'Weitere Angabe' }))

    expect(screen.getByLabelText('Hauptsachtitel / Dokumentarischer Titel')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Weitere Angabe' })).not.toBeInTheDocument()
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
    expect(screen.getByRole('button', { name: 'Weitere Angabe' })).toBeVisible()
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

    await user.click(screen.getByRole('button', { name: 'Weitere Angabe' }))
    expect(screen.getByRole('button', { name: 'Fake cancel' })).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Fake cancel' }))

    expect(screen.queryByRole('button', { name: 'Fake cancel' })).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Weitere Angabe' })).toBeVisible()
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

  it('allows only one aktivzitierung entry to be in edit mode at a time', async () => {
    const twoEntries: AktivzitierungLiterature[] = [
      {
        id: 'aktiv-1',
        titel: 'Titel 1',
      },
      {
        id: 'aktiv-2',
        titel: 'Titel 2',
      },
    ]

    const { user } = renderComponent(twoEntries)

    // both entries are shown in summary mode
    expect(screen.getByText('Titel 1')).toBeInTheDocument()
    expect(screen.getByText('Titel 2')).toBeInTheDocument()

    const editButtons = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })

    await user.click(editButtons[0]!)

    // // then – only one edit form is visible, prefilled with first title
    expect(
      screen.getAllByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      }),
    ).toHaveLength(1)
    expect(
      screen.getByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      }),
    ).toHaveValue('Titel 1')

    // // when – open edit for second entry
    await user.click(editButtons[1]!)

    // // then – still only one edit form, now for second entry
    expect(
      screen.getAllByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      }),
    ).toHaveLength(1)
    expect(
      screen.getByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      }),
    ).toHaveValue('Titel 2')
    expect(screen.queryByDisplayValue('Titel 1')).not.toBeInTheDocument()
  })

  it('hides the creation panel and the add button when an entry is in edit mode', async () => {
    const twoEntries: AktivzitierungLiterature[] = [
      { id: 'aktiv-1', titel: 'Titel 1' },
      { id: 'aktiv-2', titel: 'Titel 2' },
    ]

    const { user } = renderComponent(twoEntries)

    const editButtons = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })
    const firstEditButton = editButtons[0]!
    await user.click(firstEditButton)

    // then – the visible textbox is prefilled with the entry's title (edit form, not creation panel)
    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    expect(titleInput).toHaveValue('Titel 1') // ← This proves it's the edit form, not empty creation panel

    expect(screen.queryByRole('button', { name: 'Weitere Angabe' })).not.toBeInTheDocument()
  })

  it('closes edit mode after cancelling an edited entry', async () => {
    const existing: AktivzitierungLiterature[] = [{ id: 'aktiv-1', titel: 'Alt' }]

    const { user } = renderComponent(existing)

    const editButtons = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })
    const firstEditButton = editButtons[0]!
    await user.click(firstEditButton)

    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    await user.click(cancelButton)

    expect(screen.getByRole('button', { name: 'Eintrag bearbeiten' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Abbrechen' })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Übernehmen' })).not.toBeInTheDocument()
  })

  it('closes the creation panel when starting to edit an existing entry', async () => {
    const existing: AktivzitierungLiterature = {
      id: 'aktiv-1',
      titel: 'Titel 1',
    }

    const { user } = renderComponent([existing])

    await user.click(screen.getByRole('button', { name: 'Weitere Angabe' }))
    expect(
      screen.getByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      }),
    ).toHaveValue('')

    await user.click(screen.getByRole('button', { name: 'Eintrag bearbeiten' }))

    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    expect(titleInput).toHaveValue('Titel 1')
    expect(screen.queryByRole('button', { name: 'Weitere Angabe' })).not.toBeInTheDocument()
  })

  it('adds a search result as non-editable entry, prevents duplicates, clears inputs and allows removing via X', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()
    const clearSearchFieldsSpy = vi.fn()

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(1),
          items: ref([
            {
              id: 'sli-uuid-1',
              documentNumber: 'VALID123456789',
              veroeffentlichungsjahr: '1999-2022',
              verfasser: ['Name 1', 'Name 2'],
              titel: undefined,
            },
          ]),
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
              '<div><button aria-label="Fake search" @click="$emit(\'search\', { titel: \'searched titel\' })"></button></div>',
            setup(props, { expose }) {
              expose({ clearSearchFields: clearSearchFieldsSpy })
            },
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Fake search' }))

    expect(screen.getByText('Passende Suchergebnisse:')).toBeInTheDocument()

    const addButtons = screen.getAllByRole('button', {
      name: 'Aktivzitierung hinzufügen',
    })
    await user.click(addButtons[0]!)

    expect(clearSearchFieldsSpy).toHaveBeenCalledTimes(1)

    expect(screen.queryByText('Passende Suchergebnisse:')).not.toBeInTheDocument()

    expect(screen.getByRole('button', { name: 'Fake search' })).toBeInTheDocument()

    const list = screen.getByRole('list', { name: 'Aktivzitierung Liste' })
    let items = within(list).getAllByRole('listitem')
    expect(items).toHaveLength(1)

    const firstItem = items[0]
    expect(within(firstItem!).getByRole('button', { name: 'Eintrag löschen' })).toBeInTheDocument()
    expect(
      within(firstItem!).queryByRole('button', { name: 'Eintrag bearbeiten' }),
    ).not.toBeInTheDocument()

    await user.click(addButtons[0]!)
    items = within(list).getAllByRole('listitem')
    expect(items).toHaveLength(1)

    await user.click(within(firstItem!).getByRole('button', { name: 'Eintrag löschen' }))
    expect(screen.queryByRole('list', { name: 'Aktivzitierung Liste' })).not.toBeInTheDocument()
  })

  it('calls handleUpdateItem when updating an entry', async () => {
    const existing: AktivzitierungLiterature = {
      id: 'aktiv-1',
      titel: 'Original Title',
    }

    const { user } = renderComponent([existing])

    const editButton = screen.getByRole('button', { name: 'Eintrag bearbeiten' })
    await user.click(editButton)

    const titleInput = screen.getByRole('textbox', {
      name: 'Hauptsachtitel / Dokumentarischer Titel',
    })
    await user.clear(titleInput)
    await user.type(titleInput, 'Updated Title')

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    await user.click(saveButton)

    expect(screen.getByText('Updated Title')).toBeInTheDocument()
    expect(screen.queryByText('Original Title')).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Eintrag bearbeiten' })).toBeInTheDocument()
  })

  it('does not add duplicate search result when documentNumber already exists', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()
    const clearSearchFieldsSpy = vi.fn()

    const existingEntry: AktivzitierungLiterature = {
      id: 'existing-1',
      documentNumber: 'VALID123456789',
      titel: 'Existing Entry',
    }

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(1),
          items: ref([
            {
              id: 'sli-uuid-1',
              documentNumber: 'VALID123456789',
              veroeffentlichungsjahr: '1999-2022',
              verfasser: ['Name 1'],
            },
          ]),
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
                  aktivzitierungenSli: [existingEntry],
                },
              },
            },
          }),
        ],
        stubs: {
          AktivzitierungLiteratureInput: {
            template:
              '<div><button aria-label="Fake search" @click="$emit(\'search\', { titel: \'searched titel\' })"></button></div>',
            setup(props, { expose }) {
              expose({ clearSearchFields: clearSearchFieldsSpy })
            },
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Weitere Angabe' }))

    await user.click(screen.getByRole('button', { name: 'Fake search' }))
    expect(screen.getByText('Passende Suchergebnisse:')).toBeInTheDocument()

    const addButtons = screen.getAllByRole('button', {
      name: 'Aktivzitierung hinzufügen',
    })
    await user.click(addButtons[0]!)

    const list = screen.getByRole('list', { name: 'Aktivzitierung Liste' })
    const items = within(list).getAllByRole('listitem')
    expect(items).toHaveLength(1)
    expect(screen.getByText('Existing Entry')).toBeInTheDocument()
    expect(clearSearchFieldsSpy).not.toHaveBeenCalled()
  })

  it('generates unique IDs for entries missing them when data loads', async () => {
    const entriesWithoutIds: AktivzitierungLiterature[] = [
      {
        id: '',
        titel: 'Entry 1',
      },
      {
        titel: 'Entry 2',
      } as AktivzitierungLiterature,
      {
        id: 'existing-id',
        titel: 'Entry 3',
      },
    ]

    renderComponent(entriesWithoutIds)

    const store = useSliDocumentUnitStore()
    const loadedEntries = store.documentUnit!.aktivzitierungenSli!

    expect(loadedEntries[0]!.id).toBeTruthy()
    expect(loadedEntries[0]!.id).not.toBe('')
    expect(loadedEntries[1]!.id).toBeTruthy()
    expect(loadedEntries[1]!.id).not.toBe('')
    expect(loadedEntries[2]!.id).toBe('existing-id')

    expect(loadedEntries[0]!.id).not.toBe(loadedEntries[1]!.id)
  })

  it('resets editingItemId when data changes', async () => {
    const { user } = renderComponent(mockAktivzitierungen)
    const editButton = screen.getByRole('button', { name: 'Eintrag bearbeiten' })
    await user.click(editButton)

    expect(
      screen.getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' }),
    ).toBeInTheDocument()

    const store = useSliDocumentUnitStore()
    store.documentUnit!.aktivzitierungenSli = [...mockAktivzitierungen]
    await flushPromises()

    expect(
      screen.queryByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' }),
    ).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Eintrag bearbeiten' })).toBeInTheDocument()
  })

  it('filters out the own document from search results', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()

    // Mock results containing the current document (OWN-123) and another one (OTHER-456)
    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(2),
          items: ref([
            { id: '1', documentNumber: 'OWN-123', titel: 'My Own Document' },
            { id: '2', documentNumber: 'OTHER-456', titel: 'Other Document' },
          ]),
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
                  documentNumber: 'OWN-123',
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
              '<button aria-label="Fake search" @click="$emit(\'search\', { titel: \'test\' })"></button>',
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Fake search' }))

    expect(screen.getByText('Passende Suchergebnisse:')).toBeInTheDocument()
    expect(screen.getByText('Other Document')).toBeInTheDocument()
    expect(screen.queryByText('My Own Document')).not.toBeInTheDocument()
  })

  it('marks existing entries in search results as added (shows tag/disabled button)', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()

    const commonDocNumber = 'ALREADY-ADDED-999'

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(1),
          items: ref([
            { id: 'search-id-1', documentNumber: commonDocNumber, titel: 'Duplicate Title' },
          ]),
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
                  documentNumber: 'DOC-MAIN',
                  note: '',
                  aktivzitierungenSli: [
                    {
                      id: 'existing-1',
                      documentNumber: commonDocNumber,
                      titel: 'Existing List Entry',
                    },
                  ],
                },
              },
            },
          }),
        ],
        stubs: {
          AktivzitierungLiteratureInput: {
            template:
              '<button aria-label="Fake search" @click="$emit(\'search\', { titel: \'test\' })"></button>',
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Weitere Angabe' }))
    await user.click(screen.getByRole('button', { name: 'Fake search' }))

    expect(screen.getByText('Duplicate Title')).toBeInTheDocument()
    expect(screen.getByText('Bereits hinzugefügt')).toBeInTheDocument()

    const addButton = screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' })
    expect(addButton).toBeDisabled()
  })

  it('maps doc type names to abbreviations and keeps year when adding from search', async () => {
    vi.resetModules()
    const fetchPaginatedDataMock = vi.fn()
    const clearSearchFieldsSpy = vi.fn()

    vi.doMock('@/services/documentTypeService', () => ({
      useFetchDocumentTypes: () => ({
        data: ref({ documentTypes: [{ name: 'Bibliographie', abbreviation: 'Bib' }] }),
      }),
    }))

    vi.doMock('@/composables/usePagination', () => {
      return {
        usePagination: () => ({
          isFetching: ref(false),
          firstRowIndex: ref(0),
          totalRows: ref(1),
          items: ref([
            {
              id: 'sli-uuid-1',
              documentNumber: 'DOC-123',
              veroeffentlichungsjahr: '2025',
              verfasser: [],
              titel: 'Mapped Entry',
              dokumenttypen: ['Bibliographie'],
            },
          ]),
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
              '<div><button aria-label="Fake search" @click="$emit(\'search\', { titel: \'searched titel\' })"></button></div>',
            setup(props, { expose }) {
              expose({ clearSearchFields: clearSearchFieldsSpy })
            },
          },
        },
      },
    })

    await user.click(screen.getByRole('button', { name: 'Fake search' }))
    const addButtons = screen.getAllByRole('button', { name: 'Aktivzitierung hinzufügen' })
    await user.click(addButtons[0]!)

    // Abbreviation and year should be rendered in the list entry
    const list = screen.getByRole('list', { name: 'Aktivzitierung Liste' })
    const item = within(list).getAllByRole('listitem')[0]!
    expect(within(item).getByText(/Bib/)).toBeInTheDocument()
    expect(within(item).getByText(/2025/)).toBeInTheDocument()
    expect(within(item).getByText('Mapped Entry')).toBeInTheDocument()

    // Search panel closes after add
    expect(screen.queryByText('Passende Suchergebnisse:')).not.toBeInTheDocument()
  })
})
