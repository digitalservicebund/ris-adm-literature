import { render, screen, within } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import DocumentUnitList, { type DocumentUnitListProps } from "./DocumentUnitList.vue";
import userEvent from "@testing-library/user-event";
import type { DocumentUnitListItem } from "@/domain/documentUnit";

const docUnitsMock: DocumentUnitListItem[] = [
  {
  id: 'docUnitId1',
  documentNumber: 'documentNumber1',
  zitierdatum: '2025-01-01',
  langueberschrift: 'langueberschrift1',
  references: []
},
{
  id: 'docUnitId2',
  documentNumber: 'documentNumber1',
  zitierdatum: '2025-01-01',
  langueberschrift: 'langueberschrift1',
  references: []
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
  it('renders a list of documents', () => {
    renderComponent({ docUnits: docUnitsMock, rowsPerPage: 100, totalRows: 2, firstRowIndex: 0, loading: false})
    const tbody = screen.getAllByRole('rowgroup').find(group => group.tagName === 'TBODY')!
    const bodyRows = within(tbody).getAllByRole('row')
    screen.debug()
    expect(bodyRows).toHaveLength(2)
  })
})
