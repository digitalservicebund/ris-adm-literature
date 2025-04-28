import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { OrganType, type Normgeber } from '@/domain/normgeber'
import NormgeberListItem from './NormgeberListItem.vue'
import { nextTick } from 'vue'

const mockInstitution: Normgeber = {
  id: 'id',
  organ: {
    id: 'court1Id',
    label: 'court1',
    type: OrganType.Institution,
  },
  region: { id: 'deuId', label: 'DEU' },
}

const mockLegalEntity: Normgeber = {
  id: 'id',
  organ: {
    id: 'legalEntityId',
    label: 'legalEntity',
    type: OrganType.LegalEntity,
  },
  region: { id: 'deuId', label: 'DEU' },
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
      id: 'id',
      organ: undefined,
      region: undefined,
    })

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    // then
    expect(emitted('removeNormgeber')).toBeTruthy()
  })

  it('updates court and region when normgeber prop changes', async () => {
    const { rerender } = renderComponent(mockInstitution)

    // when updating props
    await rerender({
      normgeber: {
        organ: { label: 'Bundesverfassungsgericht', type: OrganType.Institution },
        region: { label: 'Karlsruhe' },
      },
    })
    await nextTick()

    // then
    expect(screen.getByText('Karlsruhe, Bundesverfassungsgericht')).toBeInTheDocument()
  })

  it('should render a read-only region for legal entity', async () => {
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
