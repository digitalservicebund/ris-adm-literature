import { userEvent } from '@testing-library/user-event'
import { render, screen, waitFor } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { OrganType, type Normgeber } from '@/domain/normgeber'
import NormgeberList from './NormgeberList.vue'
import { createTestingPinia } from '@pinia/testing'
import type { DocumentUnit } from '@/domain/documentUnit'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'

const mockNormgebers: Normgeber[] = [
  {
    id: 'id',
    organ: { id: 'organId', label: 'court1', type: OrganType.Institution },
    region: { id: 'deu', label: 'DEU' },
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
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
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

    expect(screen.getByText('DEU, court1')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'La')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent(
        'Landesverbände der Betriebskrankenkassen',
      )
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[1])

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))
    // leave edit mode and show previous entry
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
    // store is not updated
    expect(store.documentUnit?.normgebers?.[0].organ?.label).not.toBe(
      'Berufsgericht für Architekten Bremen',
    )
  })

  it('should update the existing normgeber on clicking update', async () => {
    const { user } = renderComponent(mockNormgebers)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))

    // then
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('court1')

    // when
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Se')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent(
        'Senatsverwaltung für Integration, Arbeit und Soziales',
      )
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    // Store should not be updated
    expect(store.documentUnit?.normgebers?.[0].organ?.label).toBe('court1')

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))
    // leave edit mode and show updated
    expect(
      screen.getByText('DEU, Senatsverwaltung für Integration, Arbeit und Soziales'),
    ).toBeInTheDocument()
    // store is updated
    expect(store.documentUnit?.normgebers?.[0].organ?.label).toBe(
      'Senatsverwaltung für Integration, Arbeit und Soziales',
    )
  })

  it('should remove the existing normgeber on clicking delete', async () => {
    const { user } = renderComponent(mockNormgebers)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.click(screen.getByLabelText('Eintrag löschen'))

    // then
    expect(screen.queryByText('DEU, court1')).not.toBeInTheDocument()
    // entry removed from store
    expect(store.documentUnit?.normgebers).toHaveLength(0)
  })
})
