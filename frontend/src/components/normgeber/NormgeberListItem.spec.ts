import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { InstitutionType, type Normgeber } from '@/domain/normgeber'
import NormgeberListItem from './NormgeberListItem.vue'

const mockNormgeber: Normgeber = {
  id: 'normgeberId',
  institution: { id: 'institutionId', label: 'new institution', type: InstitutionType.Institution },
  regions: [{ id: 'regionId', label: 'DEU' }],
}

function renderComponent() {
  const user = userEvent.setup()

  return { user, ...render(NormgeberListItem, { props: { normgeber: mockNormgeber } }) }
}

describe('NormgeberListItem', () => {
  it('renders label', async () => {
    renderComponent()
    expect(screen.getByText('DEU, new institution')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Normgeber Editieren' })).toBeInTheDocument()
    expect(screen.queryByLabelText('Normgeber *')).not.toBeInTheDocument()
  })

  it('toggles the edit mode when clicking on expand', async () => {
    const { user } = renderComponent()

    // when
    await user.click(screen.getByRole('button', { name: 'Normgeber Editieren' }))

    // then
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Normgeber hinzufügen' })).not.toBeInTheDocument()
  })
})
