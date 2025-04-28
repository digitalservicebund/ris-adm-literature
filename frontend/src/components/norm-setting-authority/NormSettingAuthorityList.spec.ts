import { userEvent } from '@testing-library/user-event'
import { render, screen, waitFor } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { type NormSettingAuthority } from '@/domain/normSettingAuthority'
import NormSettingAuthorityList from './NormSettingAuthorityList.vue'
import { createTestingPinia } from '@pinia/testing'
import type { DocumentUnit } from '@/domain/documentUnit'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'

const mockAuthorities: NormSettingAuthority[] = [
  {
    id: 'id',
    court: { label: 'court1' },
    region: { label: 'DEU' },
  },
]

function renderComponent(authorities?: NormSettingAuthority[]) {
  const user = userEvent.setup()

  return {
    user,
    ...render(NormSettingAuthorityList, {
      global: {
        plugins: [
          [
            createTestingPinia({
              initialState: {
                docunitStore: {
                  documentUnit: <DocumentUnit>{
                    documentNumber: '1234567891234',
                    normSettingAuthorities: authorities ?? [],
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

describe('NormSettingAuthorityList', () => {
  it('render empty norm setting authorities list', async () => {
    renderComponent()
    expect(screen.getByRole('heading', { level: 2, name: 'Normgeber' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Normgeber hinzufügen' })).toBeInTheDocument()
    expect(screen.queryAllByRole('listitem')).toHaveLength(0)
    expect(screen.queryByLabelText('Normgeber *')).not.toBeInTheDocument()
  })

  it('renders a list of existing authorities', async () => {
    renderComponent(mockAuthorities)
    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  })

  it('adds a new empty authority on clicking add', async () => {
    const { user } = renderComponent()
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber hinzufügen' }))

    // then
    expect(screen.getAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(store.documentUnit?.normSettingAuthorities).toHaveLength(1)
    // opens in edit mode
    expect(screen.queryByLabelText('Normgeber Editieren')).not.toBeInTheDocument()
  })

  it('should not update the existing authority on clicking cancel', async () => {
    const { user } = renderComponent(mockAuthorities)
    const store = useDocumentUnitStore()

    expect(screen.getByText('DEU, court1')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'Be')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[1]).toHaveTextContent(
        'Berufsgericht für Architekten Bremen',
      )
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[1])

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))
    // leave edit mode and show previous entry
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
    // store is not updated
    expect(store.documentUnit?.normSettingAuthorities?.[0].court?.label).not.toBe(
      'Berufsgericht für Architekten Bremen',
    )
  })

  it('should update the existing authority on clicking update', async () => {
    const { user } = renderComponent(mockAuthorities)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))

    // then
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('court1')

    // when
    await user.type(screen.getByRole('textbox', { name: 'Normgeber' }), 'AG')
    await waitFor(() => {
      expect(screen.getAllByLabelText('dropdown-option')[0]).toHaveTextContent('AG Aachen')
    })
    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    // Store should not be updated
    expect(store.documentUnit?.normSettingAuthorities?.[0].court?.label).toBe('court1')

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber übernehmen' }))
    // leave edit mode and show updated
    expect(screen.getByText('DEU, AG Aachen')).toBeInTheDocument()
    // store is updated
    expect(store.documentUnit?.normSettingAuthorities?.[0].court?.label).toBe('AG Aachen')
  })

  it('should remove the existing authority on clicking delete', async () => {
    const { user } = renderComponent(mockAuthorities)
    const store = useDocumentUnitStore()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))
    await user.click(screen.getByLabelText('Eintrag löschen'))

    // then
    expect(screen.queryByText('DEU, court1')).not.toBeInTheDocument()
    // entry removed from store
    expect(store.documentUnit?.normSettingAuthorities).toHaveLength(0)
  })
})
