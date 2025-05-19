import { render, screen, within } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import DocumentUnitList, { type DocumentUnitListProps } from './DocumentUnitList.vue'
import userEvent from '@testing-library/user-event'
import type { DocumentUnitListItem } from '@/domain/documentUnit'

const docUnitsMock: DocumentUnitListItem[] = [
  {
    id: 'docUnitId1',
    documentNumber: 'dokumentNummer1',
    zitierdatum: '2025-01-01',
    langueberschrift: 'Berücksichtigung von Sonderleistungen im Rahmen der Übergangsregelung',
    fundstellen: [
      {
        id: 'referenceId1',
        zitatstelle: '§2.1',
        periodikum: {
          id: 'periodicalId1',
          abbreviation: 'ZentrBl',
        },
      },
      {
        id: 'referenceId2',
        zitatstelle: 'Kapitel 4',
        periodikum: {
          id: 'periodicalId2',
          abbreviation: 'DokZ-S',
        },
      },
    ],
  },
  {
    id: 'docUnitId2',
    documentNumber: 'documentNummer2',
    langueberschrift:
      'Verwaltungsvorschrift zur Prüfung von Einmalzahlungen im Rahmen des § 6 Abs. 3 Satz 2 Sozialleistungsharmonisierungsgesetzes (SLHG)',
    fundstellen: [],
  },
]

function renderComponent(props: DocumentUnitListProps) {
  const user = userEvent.setup()
  return {
    user,
    ...render(DocumentUnitList, {
      props,
      global: {
        stubs: { routerLink: { template: '<a><slot/></a>' } },
      },
    }),
  }
}

describe('DocumentUnitList', () => {
  it('renders a list of 2 documents as a table', () => {
    renderComponent({
      docUnits: docUnitsMock,
      rowsPerPage: 100,
      totalRows: 2,
      firstRowIndex: 0,
      loading: false,
    })
    expect(screen.getAllByRole('row')).toHaveLength(3)
  })

  it('shows the document number, zitierdatum, langueberschrift, fundstelle and an edit button in the first row', () => {
    renderComponent({
      docUnits: docUnitsMock,
      rowsPerPage: 100,
      totalRows: 2,
      firstRowIndex: 0,
      loading: false,
    })

    const firstRow = screen.getByTestId('row-0')
    const columns = within(firstRow).getAllByRole('cell')
    expect(columns[0]).toHaveTextContent('dokumentNummer1')
    expect(columns[1]).toHaveTextContent('01.01.2025')
    expect(columns[2]).toHaveTextContent('Berücksichtigung von Sonderleistungen')
    expect(columns[3]).toHaveTextContent('ZentrBl §2.1, DokZ-S Kapitel 4')
    const editButton = within(columns[4]).getByRole('button', {
      name: 'Dokument dokumentNummer1 editieren',
    })
    expect(editButton).toBeInTheDocument()
  })

  it('shows placeholder "--" if no fundstellen or zitierdatum are present', () => {
    renderComponent({
      docUnits: docUnitsMock,
      rowsPerPage: 100,
      totalRows: 2,
      firstRowIndex: 0,
      loading: false,
    })

    const secondRow = screen.getByTestId('row-1')
    const columns = within(secondRow).getAllByRole('cell')
    expect(columns[1]).toHaveTextContent('--')
    expect(columns[3]).toHaveTextContent('--')
  })
})
