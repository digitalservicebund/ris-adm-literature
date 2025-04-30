import { userEvent } from '@testing-library/user-event'
import { render, screen, waitFor } from '@testing-library/vue'
import { describe, expect, it, beforeAll, afterAll } from 'vitest'
import { InstitutionType, type Normgeber } from '@/domain/normgeber'
import NormgeberList from './NormgeberList.vue'
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'
import { createTestingPinia } from '@pinia/testing'
import type { DocumentUnit } from '@/domain/documentUnit'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import NormgeberInput from './NormgeberInput.vue'

const institutions = [
  {
    id: crypto.randomUUID(),
    name: 'Erste Jurpn',
    officialName: 'Jurpn Eins',
    type: 'LEGAL_ENTITY',
    regions: [
      {
        id: crypto.randomUUID(),
        code: 'BB',
        longText: null,
      },
      {
        id: crypto.randomUUID(),
        code: 'AA',
        longText: null,
      },
    ],
  },
  {
    id: crypto.randomUUID(),
    name: 'Erstes Organ',
    officialName: 'Organ Eins',
    type: 'INSTITUTION',
    regions: [],
  },
  {
    id: crypto.randomUUID(),
    name: 'Zweite Jurpn',
    officialName: null,
    type: 'LEGAL_ENTITY',
    regions: [],
  },
  {
    id: crypto.randomUUID(),
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

const regions = [
  {
    id: crypto.randomUUID(),
    code: 'BB',
    longText: null,
  },
  {
    id: crypto.randomUUID(),
    code: 'AA',
    longText: null,
  },
]

const paginatedRegions = {
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

  http.get('/api/lookup-tables/regions', ({ request }) => {
    const searchTerm = new URL(request.url).searchParams.get('searchTerm')
    const filteredItems = searchTerm
      ? regions.filter((item) => item.code?.toLowerCase().includes(searchTerm.toLowerCase()))
      : regions

    return HttpResponse.json({
      regions: filteredItems,
      paginatedRegions: { ...paginatedRegions, content: filteredItems },
    })
  }),
)

const mockInstitutionNormgeber: Normgeber = {
  id: crypto.randomUUID(),
  institution: institutions[1],
  regions: [{ id: crypto.randomUUID(), label: 'DEU' }],
}

const mockLegalEntityNormgeber: Normgeber = {
  id: crypto.randomUUID(),
  institution: { 
    id: crypto.randomUUID(),
    label: 'legal entity',
    type: InstitutionType.LegalEntity,
    regions: [{ id: crypto.randomUUID(), label: 'DEU' }],
  },
  regions: [],
}

function renderComponent(props: {
  normgeber?: Normgeber
  showCancelButton: boolean
}) {
  const user = userEvent.setup()

  return {
    user,
    ...render(NormgeberInput, {
      props,
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: <DocumentUnit>{
                    documentNumber: '1234567891234',
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

describe('NormgeberInput', () => {
  beforeAll(() => server.listen())
  afterAll(() => server.close())

  it('render an empty normgeber input', async () => {
    renderComponent({showCancelButton: false})
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByLabelText('Region')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveAttribute('readonly')
    expect(screen.getByRole('button', { name: 'Normgeber übernehmen' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Normgeber übernehmen' })).toHaveAttribute('disabled')
    expect(screen.queryByRole('button', { name: 'Eintrag löschen' })).not.toBeInTheDocument()
  })

  it('renders the cancel button when prop set', async () => {
    renderComponent({showCancelButton: true})
    expect(screen.getByRole('button', { name: 'Abbrechen' })).toBeInTheDocument()
  })

  it('renders an existing institution normgeber if set', async () => {
    renderComponent({normgeber: mockInstitutionNormgeber, showCancelButton: true})
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('new institution')
    expect(screen.getByLabelText('Region *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('DEU')
    expect(screen.getByRole('button', { name: 'Abbrechen' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Eintrag löschen' })).toBeInTheDocument()
  })

  it('region is readonly for legal entity', async () => {
    renderComponent({normgeber: mockLegalEntityNormgeber, showCancelButton: true})
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('legal entity')
    expect(screen.getByLabelText('Region')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('DEU')
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveAttribute('readonly')
  })

  it('should reset local state when clicking cancel', async () => {
    const {user, emitted} =renderComponent({normgeber: mockLegalEntityNormgeber, showCancelButton: true})

    // when
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[1])
    // then
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('Erstes Organ')

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))
    // then
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('legal entity')
    expect(emitted('cancel')).toBeTruthy()
  })

  it('should save updated entity', async () => {
    const {user, emitted} =renderComponent({normgeber: mockLegalEntityNormgeber, showCancelButton: true})

    // when
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
    })
    await user.click(screen.getAllByLabelText('dropdown-option')[1])
    // then
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('Erstes Organ')
    expect(screen.getByLabelText('Region *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('')
    expect(screen.getByRole('button', { name: 'Normgeber übernehmen' })).toHaveAttribute('disabled')

    // when
    await user.type(screen.getByRole('textbox', { name: 'Region' }), 'BB')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('BB')
    })
    await user.click(screen.getAllByLabelText('dropdown-option')[0])
    // then
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('BB')
    expect(screen.getByRole('button', { name: 'Normgeber übernehmen' })).not.toHaveAttribute('disabled')

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))
    // then
    const updatedEntity = emitted('updateNormgeber')[0]
    // expect(updatedEntity).toEqual([{
    //   id: mockLegalEntityNormgeber.id,
    //   institution: institutions[1],
    //   regions: regions[0]
    // }])
  })

  // it('renders a list of existing normgebers', async () => {
  //   renderComponent(mockNormgebers)
  //   expect(screen.queryAllByRole('listitem')).toHaveLength(1)
  //   expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
  //   expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  // })

  // it('adds a new empty normgeber on clicking add', async () => {
  //   const { user } = renderComponent()
  //   const store = useDocumentUnitStore()

  //   // when
  //   await user.click(screen.getByRole('button', { name: 'Normgeber hinzufügen' }))

  //   // then
  //   expect(screen.getAllByRole('listitem')).toHaveLength(1)
  //   expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
  //   expect(store.documentUnit?.normgebers).toHaveLength(1)
  //   // opens in edit mode
  //   expect(screen.queryByLabelText('Normgeber Editieren')).not.toBeInTheDocument()
  // })

  // it('should not update the existing normgeber on clicking cancel', async () => {
  //   const { user } = renderComponent(mockNormgebers)
  //   const store = useDocumentUnitStore()

  //   // when
  //   await user.click(screen.getByLabelText('Normgeber Editieren'))
  //   await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
  //   await waitFor(() => {
  //     expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
  //   })
  //   const dropdownItems = screen.getAllByLabelText('dropdown-option')
  //   await user.click(dropdownItems[1])
  //   await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

  //   // then
  //   // leave edit mode and show previous entry
  //   expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
  //   // store is not updated
  //   expect(store.documentUnit?.normgebers?.[0].institution?.label).not.toBe('Erstes Organ')
  // })

  // it('should update the existing normgeber on clicking update', async () => {
  //   const { user } = renderComponent(mockNormgebers)
  //   const store = useDocumentUnitStore()

  //   // when
  //   await user.click(screen.getByLabelText('Normgeber Editieren'))
  //   await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
  //   await waitFor(() => {
  //     expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent('Erstes Organ')
  //   })
  //   const dropdownItems = screen.getAllByLabelText('dropdown-option')
  //   await user.click(dropdownItems[1])
  //   await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))

  //   // then
  //   expect(screen.getByText('DEU, Erstes Organ')).toBeInTheDocument()
  //   // store is updated
  //   expect(store.documentUnit?.normgebers?.[0].institution?.label).toBe('Erstes Organ')
  // })

  // it('should remove the existing normgeber on clicking delete', async () => {
  //   const { user } = renderComponent(mockNormgebers)
  //   const store = useDocumentUnitStore()

  //   // when
  //   await user.click(screen.getByLabelText('Normgeber Editieren'))
  //   await user.click(screen.getByLabelText('Eintrag löschen'))

  //   // then
  //   expect(screen.queryByText('DEU, new institution')).not.toBeInTheDocument()
  //   expect(store.documentUnit?.normgebers).toHaveLength(0)
  // })

  // it('When selecting a legal entity that has regions, then regions are joined comma separated in a read only input', async () => {
  //   const { user } = renderComponent()

  //   // when
  //   await user.click(screen.getByRole('button', { name: 'Normgeber hinzufügen' }))
  //   await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Erste')
  //   await waitFor(() => {
  //     expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('Erste Jurpn')
  //   })
  //   const dropdownItems = screen.getAllByLabelText('dropdown-option')
  //   await user.click(dropdownItems[0])

  //   // then
  //   expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('BB, AA')
  //   expect(screen.getByRole('textbox', { name: 'Region' })).toHaveAttribute('readonly')
  // })
})
