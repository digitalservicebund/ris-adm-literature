import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it, beforeAll, afterAll } from 'vitest'
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'
import { InstitutionType, type Normgeber } from '@/domain/normgeber'
import NormgeberListItem from './NormgeberListItem.vue'
import { nextTick } from 'vue'

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

const mockInstitution: Normgeber = {
  institution: {
    label: 'court1',
    type: InstitutionType.Institution,
  },
  regions: { label: 'DEU' },
}

const mockLegalEntity: Normgeber = {
  institution: {
    label: 'legalEntity',
    type: InstitutionType.LegalEntity,
  },
  regions: { label: 'DEU' },
}

function renderComponent(normgeber: Normgeber) {
  const user = userEvent.setup()

  return {
    user,
    ...render(NormgeberListItem, {
      props: { normgeber: normgeber },
    }),
  }
}

describe('NormgeberListItem', () => {
  beforeAll(() => server.listen())
  afterAll(() => server.close())
  it('renders norm setting normgeber label', async () => {
    renderComponent(mockInstitution)
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  })

  it('enters edit mode on clicking on expand', async () => {
    const { user } = renderComponent(mockInstitution)
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))

    // then
    expect(screen.queryByLabelText('Normgeber Editieren')).not.toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('court1')
  })

  it('leaves edit mode when clicking save/cancel', async () => {
    const { user } = renderComponent(mockInstitution)

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    // then
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))

    // then
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
  })

  it('should remove empty normgeber item from list when clicking cancel', async () => {
    const { user, emitted } = renderComponent({
      institution: undefined,
      regions: undefined,
    })

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    // then
    expect(emitted('removeNormgeber')).toBeTruthy()
  })

  it.skip('updates court and region when normgeber prop changes', async () => {
    const { rerender } = renderComponent(mockInstitution)

    // when updating props
    await rerender({
      normgeber: {
        organ: { label: 'Bundesverfassungsgericht', type: InstitutionType.Institution },
        region: { label: 'Karlsruhe' },
      },
    })
    await nextTick()

    // then
    expect(screen.getByText('Karlsruhe, Bundesverfassungsgericht')).toBeInTheDocument()
  })

  it.only('should render a read-only region for legal entity', async () => {
    const { user } = renderComponent(mockLegalEntity)
    expect(screen.getByText('DEU, legalEntity')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))

    // then
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByLabelText('Region *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('legalEntity')
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveAttribute('readonly')
    expect(screen.getByRole('textbox', { name: 'Region' })).toHaveValue('DEU')
  })
})
