import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import TitelSection from './TitelSection.vue'

function renderComponent(props?: { hauptsachtitel?: string; dokumentarischerTitel?: string }) {
  return render(TitelSection, {
    props: {
      hauptsachtitel: props?.hauptsachtitel ?? '',
      dokumentarischerTitel: props?.dokumentarischerTitel ?? '',
    },
  })
}

describe('Sli TitelSection', () => {
  it('shows Hauptsachtitel input and hides documentary title by default', () => {
    renderComponent()
    expect(screen.getByRole('textbox', { name: /Hauptsachtitel/ })).toBeVisible()
    expect(
      screen.queryByRole('textbox', { name: /Dokumentarischer Titel/ }),
    ).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Dokumentarischer Titel' })).toBeVisible()
  })

  it('reveals documentary title when button is clicked', async () => {
    const user = userEvent.setup()
    renderComponent()
    await user.click(screen.getByRole('button', { name: 'Dokumentarischer Titel' }))
    expect(screen.getByRole('textbox', { name: /Dokumentarischer Titel/ })).toBeVisible()
  })

  it('auto-opens documentary title when value is provided', () => {
    renderComponent({ dokumentarischerTitel: 'Doc title' })
    expect(screen.getByRole('textbox', { name: /Dokumentarischer Titel/ })).toBeVisible()
    expect(screen.getByRole('textbox', { name: /Hauptsachtitel/ })).toBeDisabled()
  })

  it('collapses documentary title on blur when cleared', async () => {
    const user = userEvent.setup()
    renderComponent()
    await user.click(screen.getByRole('button', { name: 'Dokumentarischer Titel' }))
    const docInput = screen.getByRole('textbox', { name: /Dokumentarischer Titel/ })
    await user.type(docInput, 'foo')
    await user.clear(docInput)
    await user.tab() // blur
    expect(
      screen.queryByRole('textbox', { name: /Dokumentarischer Titel/ }),
    ).not.toBeInTheDocument()
  })
})
