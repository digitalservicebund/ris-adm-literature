import { userEvent } from '@testing-library/user-event'
import { render, screen, waitFor } from '@testing-library/vue'
import { describe, expect, it, beforeAll, afterAll } from 'vitest'
import { OrganType, type Normgeber } from '@/domain/normgeber'
import NormgeberList from './NormgeberList.vue'
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'
import { createTestingPinia } from '@pinia/testing'
import type { DocumentUnit } from '@/domain/documentUnit'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'

const institutions = [
  {
    name: 'Erste Jurpn',
    officialName: 'Jurpn Eins',
    type: 'LEGAL_ENTITY',
    regions: [
      {
        code: 'BB',
        longText: null,
      },
      {
        code: 'AA',
        longText: null,
      },
    ],
  },
  {
    name: 'Erstes Organ',
    officialName: 'Organ Eins',
    type: 'INSTITUTION',
    regions: [],
  },
  {
    name: 'Zweite Jurpn',
    officialName: null,
    type: 'LEGAL_ENTITY',
    regions: [],
  },
  {
    name: 'Zweites Organ',
    officialName: null,
    type: 'INSTITUTION',
    regions: [],
  },
]

const paginatedInstitutions = {
  pageable: 'INSTANCE',
  last: true,
  totalElements: 4,
  totalPages: 1,
  first: true,
  size: 4,
  number: 0,
  sort: {
    empty: true,
    sorted: false,
    unsorted: true,
  },
  numberOfElements: 4,
  empty: false,
}

const server = setupServer(
  http.get('/api/lookup-tables/institutions', ({ request }) => {
    const searchTerm = new URL(request.url).searchParams.get('searchTerm')
    const filteredItems = searchTerm
      ? institutions.filter((item) => item.name.toLowerCase().includes(searchTerm.toLowerCase()))
      : institutions

    return HttpResponse.json({
      institutions: filteredItems,
      paginatedInstitutions: { ...paginatedInstitutions, content: filteredItems },
    })
  }),
)

const mockNormgebers: Normgeber[] = [
  {
    institution: { label: 'new institution', type: OrganType.Institution },
    region: { label: 'DEU' },
  },
]

function renderComponent(normgebers?: Normgeber[]) {
  const user = userEvent.setup()

  return {
    user,
    ...render(NormgeberList, {
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: <DocumentUnit>{
                    documentNumber: '1234567891234',
                    normgebers: normgebers ?? [],
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

describe('NormgeberList', () => {
  beforeAll(() => server.listen())
  afterAll(() => server.close())
  it('render empty norm setting normgebers list', async () => {
    renderComponent()
    expect(screen.getByRole('heading', { level: 2, name: 'Normgeber' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Normgeber hinzufügen' })).toBeInTheDocument()
    expect(screen.queryAllByRole('listitem')).toHaveLength(0)
    expect(screen.queryByLabelText('Normgeber *')).not.toBeInTheDocument()
  })

  it('renders a list of existing normgebers', async () => {
    renderComponent(mockNormgebers)
    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  })

  it('adds a new empty normgeber on clicking add', async () => {
    const { user } = renderComponent()
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber hinzufügen' }))

    // then
    expect(screen.getAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(store.documentUnit?.normgebers).toHaveLength(1)
    // opens in edit mode
    expect(screen.queryByLabelText('Normgeber Editieren')).not.toBeInTheDocument()
  })

  it('should not update the existing normgeber on clicking cancel', async () => {
    const { user } = renderComponent(mockNormgebers)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[1])
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    // then
    // leave edit mode and show previous entry
    expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
    // store is not updated
    expect(store.documentUnit?.normgebers?.[0].institution?.label).not.toBe('Erstes Organ')
  })

  it('should update the existing normgeber on clicking update', async () => {
    const { user } = renderComponent(mockNormgebers)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[1])
    await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))

    // then
    expect(screen.getByText('DEU, Erstes Organ')).toBeInTheDocument()
    // store is updated
    expect(store.documentUnit?.normgebers?.[0].institution?.label).toBe('Erstes Organ')
  })

  it('should remove the existing normgeber on clicking delete', async () => {
    const { user } = renderComponent(mockNormgebers)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.click(screen.getByLabelText('Eintrag löschen'))

    // then
    expect(screen.queryByText('DEU, new institution')).not.toBeInTheDocument()
    expect(store.documentUnit?.normgebers).toHaveLength(0)
  })
})
