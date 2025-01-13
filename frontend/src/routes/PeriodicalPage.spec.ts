import { userEvent } from '@testing-library/user-event'
import { render, screen, fireEvent } from '@testing-library/vue'
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import PeriodicalPage from '@/routes/PeriodicalPage.vue'

function renderComponent() {
  return {
    ...render(PeriodicalPage),
  }
}

const debounceTimeout = 200

describe('PeriodicalPage', () => {
  const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime })
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => {
    vi.runOnlyPendingTimers()
    vi.useRealTimers()
  })

  it('is closed', () => {
    renderComponent()

    expect(screen.queryByDisplayValue('Bundesanzeiger')).not.toBeInTheDocument()
    expect(screen.queryByDisplayValue('Arbeitsrecht aktiv')).not.toBeInTheDocument()
  })

  it('enter should select top value', async () => {
    renderComponent()
    const input = screen.getByLabelText('Periodikum')
    await fireEvent.focus(input)
    await vi.advanceTimersByTimeAsync(debounceTimeout)
    expect(screen.getAllByLabelText('dropdown-option')).toHaveLength(2)

    input.focus()
    await user.keyboard('{ArrowDown}')
    await user.keyboard('{enter}')

    await vi.advanceTimersByTimeAsync(debounceTimeout)
    expect(screen.getByLabelText('Periodikum')).toHaveValue('BAnz')
  })
})
