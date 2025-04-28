import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import { type NormSettingAuthority } from '@/domain/normSettingAuthority'
import NormSettingAuthorityListItem from './NormSettingAuthorityListItem.vue'
import { nextTick } from 'vue'

const mockAuthority: NormSettingAuthority = {
  id: 'id',
  court: { label: 'court1' },
  region: { label: 'DEU' },
}

function renderComponent(authority: NormSettingAuthority) {
  const user = userEvent.setup()

  return {
    user,
    ...render(NormSettingAuthorityListItem, {
      props: { authority },
    }),
  }
}

describe('NormSettingAuthorityListItem', () => {
  it('renders norm setting authority label', async () => {
    renderComponent(mockAuthority)
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber Editieren')).toBeInTheDocument()
  })

  it('enters edit mode on clicking on expand', async () => {
    const { user } = renderComponent(mockAuthority)
    expect(screen.getByText('DEU, court1')).toBeInTheDocument()

    // when
    await user.click(screen.getByLabelText('Normgeber Editieren'))

    // then
    expect(screen.queryByLabelText('Normgeber Editieren')).not.toBeInTheDocument()
    expect(screen.getByLabelText('Normgeber *')).toBeInTheDocument()
    expect(screen.getByRole('textbox', { name: 'Normgeber' })).toHaveValue('court1')
  })

  it('leaves edit mode when clicking save/cancel', async () => {
    const { user } = renderComponent(mockAuthority)

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

  it('should remove empty authority item from list when clicking cancel', async () => {
    const { user, emitted } = renderComponent({
      id: 'id',
      court: undefined,
      region: undefined,
    })

    // when
    await user.click(screen.getByRole('button', { name: 'Abbrechen' }))

    // then
    expect(emitted('removeAuthority')).toBeTruthy()
  })

  it('updates court and region when authority prop changes', async () => {
    const { rerender } = renderComponent(mockAuthority)

    // when updating props
    await rerender({
      authority: {
        court: { label: 'Bundesverfassungsgericht' },
        region: { label: 'Karlsruhe' },
      },
    })
    await nextTick()

    // then
    expect(screen.getByText('Karlsruhe, Bundesverfassungsgericht')).toBeInTheDocument()
  })
})
