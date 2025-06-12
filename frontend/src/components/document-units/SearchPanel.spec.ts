import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import SearchPanel from './SearchPanel.vue'
import userEvent from '@testing-library/user-event'

function renderComponent(props = {}) {
  const user = userEvent.setup()

  return {
    user,
    ...render(SearchPanel, { props }),
  }
}

describe('SearchPanel', () => {
  it('renders', async () => {
    renderComponent()

    expect(screen.getByLabelText('Dokumentnummer')).toBeInTheDocument()
    expect(screen.getByLabelText('Amtl. Langüberschrift')).toBeInTheDocument()
    expect(screen.getByLabelText('Fundstelle')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Ergebnisse zeigen' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
  })

  it('should clear search params on click reset', async () => {
    const { user } = renderComponent()

    await user.type(screen.getByLabelText('Dokumentnummer'), 'KSNR')
    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
    await user.click(screen.getByRole('button', { name: 'Zurücksetzen' }))
    expect(screen.getByLabelText('Dokumentnummer')).toHaveValue('')
    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
  })

  it('should emit search params on click search', async () => {
    const { emitted, user } = renderComponent()

    await user.type(screen.getByLabelText('Dokumentnummer'), 'KSNR')
    await user.click(screen.getByRole('button', { name: 'Ergebnisse zeigen' }))
    expect(emitted()['search'].length).toBe(1)
  })
})
