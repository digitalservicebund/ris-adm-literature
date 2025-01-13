import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen } from '@testing-library/vue'
import DocumentUnitReferenceInput from '@/components/periodical/DocumentUnitReferenceInput.vue'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import Reference from '@/domain/reference.ts'
import { userEvent } from '@testing-library/user-event'

function renderComponent(
  options: {
    modelValue?: Reference
  } = {},
) {
  return {
    ...render(DocumentUnitReferenceInput, {
      props: {
        modelValue: options.modelValue,
      },
    }),
  }
}

const debounceTimeout = 200

describe('DocumentUnitReferenceInput', () => {
  const user = userEvent.setup({ advanceTimers: vi.advanceTimersByTime })
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => {
    vi.runOnlyPendingTimers()
    vi.useRealTimers()
  })

  it('component is rendered', async () => {
    renderComponent()

    expect(screen.getByLabelText('Periodikum')).toBeInTheDocument()
    expect(screen.getByLabelText('Zitatstelle')).toBeInTheDocument()
  })

  it('item is selected', async () => {
    renderComponent({
      modelValue: new Reference(
        new LegalPeriodical({ title: 'Bundesanzeiger', abbreviation: 'BAnz' }),
      ),
    })

    const openDropdownContainer = screen.getByLabelText('Dropdown öffnen')
    await user.click(openDropdownContainer)
    await vi.advanceTimersByTimeAsync(debounceTimeout)

    const dropdownItems = screen.getAllByLabelText('dropdown-option')

    expect(dropdownItems).toHaveLength(2)
    expect(dropdownItems[0]).toHaveTextContent('BAnz | Bundesanzeiger')

    await user.click(dropdownItems[0])
    await vi.advanceTimersByTimeAsync(debounceTimeout)
    expect(screen.getByDisplayValue('BAnz')).toBeInTheDocument()
  })

  it('selection is cleared', async () => {
    renderComponent({
      modelValue: new Reference(
        new LegalPeriodical({ title: 'Bundesanzeiger', abbreviation: 'BAnz' }),
      ),
    })

    const openDropdownContainer = screen.getByLabelText('Dropdown öffnen')
    await user.click(openDropdownContainer)
    await vi.advanceTimersByTimeAsync(debounceTimeout)

    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])
    await vi.advanceTimersByTimeAsync(debounceTimeout)
    const resetButton = screen.getByLabelText('Auswahl zurücksetzen')
    await user.click(resetButton)

    expect(screen.queryByDisplayValue('BAnz')).not.toBeInTheDocument()
  })
})
