import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { InstitutionType, type Normgeber } from '@/domain/normgeber'
import NormgeberList from './NormgeberList.vue'
import { createTestingPinia } from '@pinia/testing'
import type { DocumentUnit } from '@/domain/documentUnit'

const mockNormgebers: Normgeber[] = [
  {
    id: 'normgeberId',
    institution: {
      id: 'institutionId',
      label: 'new institution',
      type: InstitutionType.Institution,
    },
    regions: [{ id: 'regionId', label: 'DEU' }],
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
  it('renders creation panel when there is no normgeber', async () => {
    renderComponent()
    expect(screen.getByRole('heading', { level: 2, name: 'Normgeber' })).toBeInTheDocument()
    expect(screen.queryAllByRole('listitem')).toHaveLength(0)
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Normgeber hinzufügen' })).not.toBeInTheDocument()
  })

  it('renders a list of existing normgebers', async () => {
    renderComponent(mockNormgebers)
    expect(screen.queryAllByRole('listitem')).toHaveLength(1)
    expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  })

  it('opens the creation panel on clicking add', async () => {
    const { user } = renderComponent(mockNormgebers)

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber hinzufügen' }))

    // then
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Normgeber hinzufügen' })).not.toBeInTheDocument()
  })
})
