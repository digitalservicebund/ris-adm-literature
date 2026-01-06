import { render, screen } from '@testing-library/vue'
import { describe, it, expect } from 'vitest'
import AktivzitierungAdmInput from './AktivzitierungAdmInput.vue'
import userEvent from '@testing-library/user-event'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import { InputText } from 'primevue'

function renderComponent(modelValue: AktivzitierungAdm) {
  return render(AktivzitierungAdmInput, {
    props: {
      modelValue,
    },
    global: {
      stubs: {
        ZitierArtDropDown: {
          props: ['modelValue'],
          emits: ['update:modelValue'],
          template: `
              <input
                aria-label="Art der Zitierung"
                @input="$emit('update:modelValue', { abbreviation: $event.target.value })"
              />
            `,
        },
        InstitutionDropDown: {
          props: ['modelValue'],
          emits: ['update:modelValue'],
          template: `
              <input
                aria-label="Normgeber"
                :value="modelValue?.name || ''"
                @input="$emit('update:modelValue', { name: $event.target.value })"
              />
            `,
        },
        PeriodikumDropDown: {
          props: ['modelValue'],
          emits: ['update:modelValue'],
          template: `
              <input
                aria-label="Periodikum"
                @input="$emit('update:modelValue', { abbreviation: $event.target.value })"
              />
            `,
        },
        DokumentTypDropDown: {
          props: ['modelValue'],
          emits: ['update:modelValue'],
          template: `
              <input
                aria-label="Dokumenttyp"
                @input="$emit('update:modelValue', $event.target.value)"
              />
            `,
        },
        DateInput: InputText,
      },
    },
  })
}

describe('AktivzitierungAdmInput', () => {
  it('emits updated AktivzitierungAdm when documentNumber changes', async () => {
    const user = userEvent.setup()
    const initialValue = {
      id: '123',
      uuid: undefined,
      documentNumber: 'OLDVALUE',
    }

    const { emitted } = renderComponent(initialValue)

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

  it.each([
    {
      label: 'Art der Zitierung',
      inputValue: 'Abgrenzung',
      expectedPatch: { citationType: 'Abgrenzung' },
    },
    {
      label: 'Normgeber',
      inputValue: 'Bundestag',
      expectedPatch: { normgeber: 'Bundestag' },
    },
    {
      label: 'Inkrafttretedatum',
      inputValue: '01.01.2025',
      expectedPatch: { inkrafttretedatum: '01.01.2025' },
    },
    {
      label: 'Aktenzeichen',
      inputValue: '§3',
      expectedPatch: { aktenzeichen: '§3' },
    },
    {
      label: 'Periodikum',
      inputValue: 'NJW',
      expectedPatch: { periodikum: 'NJW' },
    },
    {
      label: 'Zitatstelle',
      inputValue: 'Kapitel 7',
      expectedPatch: { zitatstelle: 'Kapitel 7' },
    },
    {
      label: 'Dokumentnummer',
      inputValue: 'TheDocNumber',
      expectedPatch: { documentNumber: 'TheDocNumber' },
    },
    {
      label: 'Dokumenttyp',
      inputValue: 'TheDocType',
      expectedPatch: { dokumenttyp: 'TheDocType' },
    },
  ])(
    'emits updated AktivzitierungAdm when $label changes',
    async ({ label, inputValue, expectedPatch }) => {
      const user = userEvent.setup()
      const initialValue: AktivzitierungAdm = {
        id: '123',
      }

      const { emitted } = renderComponent(initialValue)

      const input = screen.getByRole('textbox', { name: label })
      await user.type(input, inputValue)

      const events = emitted()['update:modelValue'] as Array<[AktivzitierungAdm]>
      const finalPayload = events[events.length - 1]![0]

      expect(finalPayload).toEqual({
        ...initialValue,
        ...expectedPatch,
      })
    },
  )

  it('should render correctly when initialize without a citationType', () => {
    const initialValue: AktivzitierungAdm = {
      id: '123',
    }
    renderComponent(initialValue)

    const input = screen.getByRole('textbox', { name: 'Art der Zitierung' })
    expect(input).toHaveValue('')
  })
})
