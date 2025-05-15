import { render, screen, within } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import DocumentUnitList, { type DocumentUnitListProps } from './DocumentUnitList.vue'
import userEvent from '@testing-library/user-event'
import type { DocumentUnitListItem } from '@/domain/documentUnit'

const docUnitsMock: DocumentUnitListItem[] = [
  {
    id: 'docUnitId1',
    dokumentNummer: 'dokumentNummer1',
    zitierdatum: '2025-01-01',
    langueberschrift: 'Berücksichtigung von Sonderleistungen im Rahmen der Übergangsregelung',
    fundstellen: [
      {
        id: 'referenceId1',
        zitatstelle: '§2.1',
        periodika: [
          {
            id: 'periodicalId1',
            abbreviation: 'ZentrBl',
          },
        ],
      },
      {
        id: 'referenceId2',
        zitatstelle: 'Kapitel 4',
        periodika: [
          {
            id: 'periodicalId2',
            abbreviation: 'DokZ-S',
          },
        ],
      },
    ],
  },
  {
    id: 'docUnitId2',
    dokumentNummer: 'documentNummer2',
    zitierdatum: '2025-01-01',
    langueberschrift:
      'Verwaltungsvorschrift zur Prüfung von Einmalzahlungen im Rahmen des § 6 Abs. 3 Satz 2 Sozialleistungsharmonisierungsgesetzes (SLHG)',
    fundstellen: [],
  },
]

function renderComponent(props: DocumentUnitListProps) {
  const user = userEvent.setup()
  return {
    user,
    ...render(DocumentUnitList, { props }),
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
    const tbody = screen.getAllByRole('rowgroup').find((group) => group.tagName === 'TBODY')!
    const bodyRows = within(tbody).getAllByRole('row')
    expect(bodyRows).toHaveLength(2)
  })

  it('shows the document number, zitierdatum, langueberschrift and fundstelle in the first row', () => {
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
  })

  it('shows "--" as fundstelle if no fundstellen is present', () => {
    renderComponent({
      docUnits: docUnitsMock,
      rowsPerPage: 100,
      totalRows: 2,
      firstRowIndex: 0,
      loading: false,
    })
    const firstRow = screen.getByTestId('row-1')
    const columns = within(firstRow).getAllByRole('cell')
    expect(columns[3]).toHaveTextContent('--')
  })
})
