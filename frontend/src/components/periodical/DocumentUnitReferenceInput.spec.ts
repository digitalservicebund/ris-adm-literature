import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen } from '@testing-library/vue'
import DocumentUnitReferenceInput from '@/components/periodical/DocumentUnitReferenceInput.vue'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import Reference from '@/domain/reference.ts'
import { userEvent } from '@testing-library/user-event'

function renderComponent(
  options: {
    modelValue?: Reference
    modelValueList?: Reference[]
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
    renderComponent()

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
      modelValue: new Reference({
        legalPeriodical: new LegalPeriodical({ title: 'Bundesanzeiger', abbreviation: 'BAnz' }),
      }),
    })
    const resetButton = screen.getByLabelText('Auswahl zurücksetzen')
    await user.click(resetButton)

    expect(screen.queryByDisplayValue('BAnz')).not.toBeInTheDocument()
  })

  it('add reference', async () => {
    const { emitted } = renderComponent()

    const openDropdownContainer = screen.getByLabelText('Dropdown öffnen')
    await user.click(openDropdownContainer)

    const dropdownItems = screen.getAllByLabelText('dropdown-option')
    await user.click(dropdownItems[0])

    const citationInput = screen.getByLabelText('Zitatstelle')
    await user.type(citationInput, 'abcde')

    expect(screen.getByLabelText('Zitatstelle')).toHaveValue('abcde')

    const addButton = screen.getByLabelText('Fundstelle speichern')
    await user.click(addButton)

    // Expect event 'addEntry' is triggered
    expect(emitted()['addEntry']).toHaveLength(1)
    expect(screen.getByDisplayValue('BAnz')).toBeInTheDocument()
  })

  it('delete reference', async () => {
    const { emitted, rerender } = renderComponent({
      modelValue: new Reference({
        legalPeriodical: new LegalPeriodical({ title: 'Bundesanzeiger', abbreviation: 'BAnz' }),
        citation: 'abcde',
      }),
    })
    const deleteButton = screen.getByLabelText('Eintrag löschen')

    expect(deleteButton).toBeVisible()
    await user.click(deleteButton)
    expect(emitted()['removeEntry']).toHaveLength(1)

    // Simulate the removing of the value (triggered by EditableList)
    await rerender({ modelValue: undefined })
    expect(screen.queryByLabelText('Eintrag löschen')).not.toBeInTheDocument()
    expect(screen.queryByDisplayValue('BAnz')).not.toBeInTheDocument()
  })

  it('cancel editing reference', async () => {
    renderComponent({
      modelValue: new Reference({
        legalPeriodical: new LegalPeriodical({ title: 'Bundesanzeiger', abbreviation: 'BAnz' }),
        citation: 'abcde',
      }),
    })
    const cancelButton = screen.getByLabelText('Abbrechen')

    expect(cancelButton).toBeEnabled()
    await user.click(cancelButton)
    expect(screen.getByDisplayValue('BAnz')).toBeInTheDocument()
  })
})
