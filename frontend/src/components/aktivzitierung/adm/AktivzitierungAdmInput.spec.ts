import { render, screen } from '@testing-library/vue'
import { describe, it, expect } from 'vitest'
import AktivzitierungAdmInput from './AktivzitierungAdmInput.vue'
import userEvent from '@testing-library/user-event'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'

describe('AktivzitierungAdmInput', () => {
  it('emits updated AktivzitierungAdm when documentNumber changes', async () => {
    const user = userEvent.setup()
    const initialValue = {
      id: '123',
      uuid: undefined,
      documentNumber: 'OLDVALUE',
    }

    const { emitted } = render(AktivzitierungAdmInput, {
      props: {
        modelValue: initialValue,
      },
    })

    const input = screen.getByRole('textbox', { name: 'Dokumentnummer' })

    await user.clear(input)
    await user.type(input, 'NEWVALUE')

    const events = emitted()['update:modelValue'] as Array<[AktivzitierungAdm]>
    const finalPayload = events[events.length - 1]![0]

    expect(finalPayload).toEqual({
      ...initialValue,
      documentNumber: 'NEWVALUE',
    })
  })
})
