import { fireEvent, render, screen } from '@testing-library/vue'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import { defineComponent, ref } from 'vue'
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import DocumentUnits from './DocumentUnits.vue'

const fetchPaginatedDataMock = vi.fn()

const mockDocUnits: DocumentUnitListItem[] = [
  {
    id: '1',
    documentNumber: 'DOK-001',
    langueberschrift: 'Erstes Gesetz zur Änderung des Gesetzes',
    fundstellen: ['a1', 'b1'],
    zitierdaten: [],
  },
  {
    id: '2',
    documentNumber: 'DOK-002',
    langueberschrift: 'Verordnung über die Durchführung',
    fundstellen: ['a1'],
    zitierdaten: [],
  },
  {
    id: '3',
    documentNumber: 'VER-XYZ',
    langueberschrift: undefined,
    fundstellen: ['d3'],
    zitierdaten: [],
  },
]

vi.mock('@/composables/usePagination', () => ({
  usePagination: () => ({
    firstRowIndex: ref(0),
    totalRows: ref(mockDocUnits.length),
    items: ref(mockDocUnits),
    ITEMS_PER_PAGE: 10,
    fetchPaginatedData: fetchPaginatedDataMock,
  }),
}))

async function renderComponent() {
  const view = render(DocumentUnits, {
    global: {
      stubs: {
        DocumentUnitList: defineComponent({
          template: '<div data-testid="doc-list" :data-docs="JSON.stringify(docUnits)"></div>',
          props: ['docUnits'],
        }),
        RisPaginator: {
          template: '<div data-testid="ris-paginator" @page="$emit(\'page\', { page: 1 })"></div>',
        },
      },
    },
  })
  await vi.dynamicImportSettled()
  return view
}

function getFilteredDocs(): DocumentUnitListItem[] {
  const list = screen.getByTestId('doc-list')
  return JSON.parse(list.getAttribute('data-docs') || '[]')
}

describe('DocumentUnits', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders the component correctly', async () => {
    const { container } = await renderComponent()
    expect(screen.getByRole('form', { name: 'Schnellsuche' })).toBeInTheDocument()
    expect(container).toBeTruthy()
  })

  it('calls fetchPaginatedData on page update from the paginator', async () => {
    await renderComponent()
    const risPaginator = screen.getByTestId('ris-paginator')
    // when
    await fireEvent(risPaginator, new CustomEvent('page', { detail: { page: 1 } }))
    // then
    expect(fetchPaginatedDataMock).toHaveBeenCalledWith(1)
  })

  describe('Search functionality', () => {
    it('should display all document units initially', async () => {
      await renderComponent()
      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(3)
      expect(filteredDocs).toEqual(mockDocUnits)
    })

    it('should filter by documentNumber case-insensitive', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Dokumentnummer'), 'dok-001')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(1)
      expect(filteredDocs[0].documentNumber).toBe('DOK-001')
    })

    it('should filter by langueberschrift', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Amtl. Langüberschrift'), 'verordnung')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(1)
      expect(filteredDocs[0].langueberschrift).toContain('Verordnung')
    })

    it('should filter by fundstelle', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Fundstelle'), 'a1')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(2)
      expect(filteredDocs.map((d) => d.documentNumber)).toEqual(['DOK-001', 'DOK-002'])
    })

    it('should return no results if search term does not match', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Dokumentnummer'), 'nonexistent')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(0)
    })

    it('should combine filters with and logic', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Amtl. Langüberschrift'), 'gesetz')
      await fireEvent.update(screen.getByLabelText('Dokumentnummer'), 'dok-001')
      await fireEvent.update(screen.getByLabelText('Fundstelle'), 'a1')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(1)
      expect(filteredDocs[0].documentNumber).toBe('DOK-001')
    })

    it('should return no results if only one of multiple filters matches', async () => {
      await renderComponent()
      await fireEvent.update(screen.getByLabelText('Amtl. Langüberschrift'), 'gesetz')
      await fireEvent.update(screen.getByLabelText('Dokumentnummer'), 'dok-002')
      await fireEvent.submit(screen.getByRole('form'))

      const filteredDocs = getFilteredDocs()
      expect(filteredDocs.length).toBe(0)
    })

    it('should reset the filter when the reset button is clicked', async () => {
      await renderComponent()

      await fireEvent.update(screen.getByLabelText('Dokumentnummer'), 'dok-001')
      await fireEvent.submit(screen.getByRole('form'))
      expect(getFilteredDocs().length).toBe(1)

      await fireEvent.click(screen.getByRole('button', { name: 'Zurücksetzen' }))

      expect(getFilteredDocs().length).toBe(3)
      expect(screen.getByLabelText('Dokumentnummer')).toHaveValue('')
    })
  })
})
