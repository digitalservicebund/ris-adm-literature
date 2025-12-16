import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'
import AktivzitierungInput from './AktivzitierungInput.vue'

// Dummy type for generic T
type DummyT = { id: string; documentNumber?: string }

describe('AktivzitierungInput', () => {
  it('emits "update" when save button is clicked', async () => {
    const user = userEvent.setup()
    const initial = { id: '123', documentNumber: 'Initial' }

    const { emitted } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: initial,
        showCancelButton: true,
      },
      slots: {
        default: `<template #default="{ modelValue, onUpdateModelValue }">
                    <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
                  </template>`,
      },
    })

    const input = screen.getByTestId('input') as HTMLInputElement
    await user.clear(input)
    await user.type(input, 'Updated')

    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    await user.click(saveButton)

    const events = emitted().update as [DummyT][]
    expect(events).toBeTruthy()
    const payload = events[0]![0]

    expect(payload).toEqual({
      ...initial,
      documentNumber: 'Updated',
      id: '123',
    })
  })

  it('emits "cancel" when cancel button is clicked', async () => {
    const user = userEvent.setup()
    const { emitted } = render(AktivzitierungInput, {
      props: { showCancelButton: true },
    })

    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    await user.click(cancelButton)

    const events = emitted().cancel
    expect(events).toHaveLength(1)
  })

  it('emits "delete" when delete button is clicked for existing entry', async () => {
    const user = userEvent.setup()
    const existing = { id: 'abc' }

    const { emitted } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: existing,
        showCancelButton: true,
      },
    })

    const deleteButton = screen.getByRole('button', { name: 'Eintrag löschen' })
    await user.click(deleteButton)

    const events = emitted().delete as [string][]
    expect(events).toHaveLength(1)
    expect(events[0]![0]).toBe('abc')
  })

  it('updates aktivzitierungRef when aktivzitierung prop changes', async () => {
    const initialValue: DummyT = { id: '1', documentNumber: 'OLD' }
    const newValue: DummyT = { id: '2', documentNumber: 'NEW' }

    const { rerender } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: initialValue,
        showCancelButton: false,
      },
      slots: {
        default: `<template #default="{ modelValue }">
                    <input data-testid="docnumber" :value="modelValue.documentNumber" readonly />
                  </template>`,
      },
    })

    // Initial value should appear in slot
    const input = screen.getByTestId('docnumber') as HTMLInputElement
    expect(input.value).toBe('OLD')

    // Rerender with new aktivzitierung prop
    await rerender({ aktivzitierung: newValue })

    // The slot should now show the updated value
    const updatedInput = screen.getByTestId('docnumber') as HTMLInputElement
    expect(updatedInput.value).toBe('NEW')
  })
})
