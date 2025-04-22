import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { afterAll, beforeAll, describe, expect, it } from 'vitest'
import NormReferenceInput from '@/components/NormReferenceInput.vue'
import NormReference from '@/domain/normReference'
import { config } from '@vue/test-utils'
import InputText from 'primevue/inputtext'

function renderComponent(options?: { modelValue?: NormReference }) {
  const user = userEvent.setup()
  const props = {
    modelValue: new NormReference({ ...options?.modelValue }),
  }
  const utils = render(NormReferenceInput, { props })
  return { user, props, ...utils }
}

describe('NormReferenceEntry', () => {
  beforeAll(() => {
    config.global.stubs = {
      InputMask: InputText,
    }
  })

  afterAll(() => {
    config.global.stubs = {}
  })

  it('render empty norm input group on initial load', async () => {
    renderComponent()
    expect(screen.getByLabelText('RIS-Abkürzung')).toBeInTheDocument()

    expect(screen.queryByLabelText('Einzelnorm der Norm')).not.toBeInTheDocument()
    expect(screen.queryByLabelText('Fassungsdatum der Norm')).not.toBeInTheDocument()
    expect(screen.queryByLabelText('Jahr der Norm')).not.toBeInTheDocument()
    expect(screen.queryByLabelText('Norm speichern')).not.toBeInTheDocument()
  })

  it('render values if given', async () => {
    renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '12',
            dateOfVersion: '2022-01-31',
            dateOfRelevance: '2023',
          },
        ],
      } as NormReference,
    })

    const abbreviationField = screen.getByLabelText('RIS-Abkürzung')

    const singleNormField = screen.getByLabelText('Einzelnorm der Norm')

    const versionField = screen.getByLabelText('Fassungsdatum der Norm')

    const relevanceField = screen.getByLabelText('Jahr der Norm')

    expect(abbreviationField).toHaveValue('ABC')
    expect(singleNormField).toHaveValue('12')
    expect(versionField).toHaveValue('31.01.2022')
    expect(relevanceField).toHaveValue('2023')
    expect(screen.getByLabelText('Norm speichern')).toBeEnabled()
  })

  it('renders multiple single norm input groups', async () => {
    renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '§ 000',
            dateOfVersion: '2022-01-31',
            dateOfRelevance: '2023',
          },
          {
            singleNorm: '§ 123',
            dateOfVersion: '2022-01-31',
            dateOfRelevance: '2023',
          },
          {
            singleNorm: '§ 345',
            dateOfVersion: '2022-01-31',
            dateOfRelevance: '2023',
          },
        ],
      } as NormReference,
    })
    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(3)
  })

  it('adds new single norm', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '12',
          },
        ],
      } as NormReference,
    })

    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(1)
    const addSingleNormButton = screen.getByLabelText('Weitere Einzelnorm')
    await user.click(addSingleNormButton)
    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(2)
  })

  it('removes single norm', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '12',
          },
          {
            singleNorm: '34',
          },
        ],
      } as NormReference,
    })

    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(2)
    const removeSingleNormButtons = screen.getAllByLabelText('Einzelnorm löschen')
    await user.click(removeSingleNormButtons[0])
    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(1)
  })

  it('removes last single norm in list', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '§ 34',
          },
        ],
      } as NormReference,
    })

    expect((await screen.findAllByLabelText('Einzelnorm der Norm')).length).toBe(1)
    const removeSingleNormButtons = screen.getAllByLabelText('Einzelnorm löschen')
    await user.click(removeSingleNormButtons[0])
    expect(screen.queryByText('Einzelnorm der Norm')).not.toBeInTheDocument()
  })

  it('validates invalid norm input on blur', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const singleNormInput = await screen.findByLabelText('Einzelnorm der Norm')
    await user.type(singleNormInput, '2021, Seite 21')
    expect(singleNormInput).toHaveValue('2021, Seite 21')
    await user.tab()

    await screen.findByText(/Inhalt nicht valide/)
  })

  it('validates invalid norm input on mount', async () => {
    renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '2021, Seite 21',
          },
        ],
      } as NormReference,
    })

    const singleNormInput = await screen.findByLabelText('Einzelnorm der Norm')
    expect(singleNormInput).toHaveValue('2021, Seite 21')

    await screen.findByText(/Inhalt nicht valide/)
  })

  it('does not add norm with invalid single norm input', async () => {
    renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '2021, Seite 21',
          },
        ],
      } as NormReference,
    })

    const singleNormInput = await screen.findByLabelText('Einzelnorm der Norm')
    expect(singleNormInput).toHaveValue('2021, Seite 21')

    await screen.findByText(/Inhalt nicht valide/)
    screen.getByLabelText('Norm speichern').click()
    expect(singleNormInput).toBeVisible()
  })

  it('does not add norm with invalid version date input', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const dateInput = await screen.findByLabelText('Fassungsdatum der Norm')
    expect(dateInput).toHaveValue('')

    await user.type(dateInput, '00.00.0231')

    await screen.findByText(/Kein valides Datum/)
    screen.getByLabelText('Norm speichern').click()
    expect(dateInput).toBeVisible()
  })

  it('does not add norm with incomplete version date input', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const dateInput = await screen.findByLabelText('Fassungsdatum der Norm')
    expect(dateInput).toHaveValue('')

    await user.type(dateInput, '01')
    await user.tab()

    await screen.findByText(/Unvollständiges Datum/)
    screen.getByLabelText('Norm speichern').click()
    expect(dateInput).toBeVisible()
  })

  it('does not add norm with invalid year input', async () => {
    renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            dateOfRelevance: '0000',
          },
        ],
      } as NormReference,
    })

    const yearInput = await screen.findByLabelText('Jahr der Norm')
    expect(yearInput).toHaveValue('0000')

    await screen.findByText(/Kein valides Jahr/)
    screen.getByLabelText('Norm speichern').click()
    expect(yearInput).toBeVisible()
  })

  it('validates ambiguous norm reference input', async () => {
    renderComponent({
      modelValue: {
        normAbbreviationRawValue: 'EWGAssRBes 1/80',
      } as NormReference,
    })

    expect(screen.getByText('Mehrdeutiger Verweis')).toBeInTheDocument()
  })

  it('new input removes error message', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
        singleNorms: [
          {
            singleNorm: '2021, Seite 21',
          },
        ],
      } as NormReference,
    })

    const risAbbreviation = screen.getByLabelText('RIS-Abkürzung')
    expect(risAbbreviation).toHaveValue('ABC')

    const singleNormInput = screen.getByLabelText('Einzelnorm der Norm')
    expect(singleNormInput).toHaveValue('2021, Seite 21')

    await screen.findByText(/Inhalt nicht valide/)

    await user.type(singleNormInput, '{backspace}')

    expect(screen.queryByText(/Inhalt nicht valide/)).not.toBeInTheDocument()
  })

  it('correctly updates the value of ris abbreviation input', async () => {
    const { user, emitted } = renderComponent()
    const abbreviationField = screen.getByLabelText('RIS-Abkürzung')

    await user.type(abbreviationField, 'SGB')
    const dropdownItems = screen.getAllByLabelText('dropdown-option') as HTMLElement[]
    expect(dropdownItems[0]).toHaveTextContent('SGB 5')
    await user.click(dropdownItems[0])

    const button = screen.getByLabelText('Norm speichern')
    await user.click(button)
    expect(emitted('update:modelValue')).toEqual([
      [
        {
          normAbbreviation: {
            abbreviation: 'SGB 5',
            officialLongTitle: 'Sozialgesetzbuch (SGB) Fünftes Buch (V)',
          },
          singleNorms: [],
          normAbbreviationRawValue: undefined,
        },
      ],
    ])
  })

  it('correctly updates the value of the single norm input', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })
    const singleNormInput = await screen.findByLabelText('Einzelnorm der Norm')

    await user.type(singleNormInput, '§ 123')
    expect(singleNormInput).toHaveValue('§ 123')
  })

  it('correctly updates the value of the version date input', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const versionField = screen.getByLabelText('Fassungsdatum der Norm')
    await user.type(versionField, '31.01.2022')

    expect(versionField).toHaveValue('31.01.2022')
  })

  it('correctly updates the value of the version date input', async () => {
    const { user } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const relevanceField = screen.getByLabelText('Jahr der Norm')
    await user.type(relevanceField, '2023')

    expect(relevanceField).toHaveValue('2023')
  })

  it('emits add event', async () => {
    const { user, emitted } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const addButton = screen.getByLabelText('Norm speichern')
    await user.click(addButton)

    expect(emitted('addEntry')).toBeTruthy()
  })

  it('emits delete event', async () => {
    const { user, emitted } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const deleteButton = screen.getByLabelText('Eintrag löschen')
    await user.click(deleteButton)

    expect(emitted('removeEntry')).toBeTruthy()
  })

  it('emits cancel edit', async () => {
    const { user, emitted } = renderComponent({
      modelValue: {
        normAbbreviation: { id: '123', abbreviation: 'ABC' },
      } as NormReference,
    })

    const cancelEdit = screen.getByLabelText('Abbrechen')
    await user.click(cancelEdit)

    expect(emitted('cancelEdit')).toBeTruthy()
  })

  it('removes entry on cancel edit, when not previously saved yet', async () => {
    const { user, emitted } = renderComponent()

    const abbreviationField = screen.getByLabelText('RIS-Abkürzung')
    await user.type(abbreviationField, 'SGB')
    const dropdownItems = screen.getAllByLabelText('dropdown-option') as HTMLElement[]
    expect(dropdownItems[0]).toHaveTextContent('SGB 5')
    await user.click(dropdownItems[0])

    const cancelEdit = screen.getByLabelText('Abbrechen')
    await user.click(cancelEdit)

    expect(emitted('cancelEdit')).toBeTruthy()
    expect(emitted('removeEntry')).toBeTruthy()
  })
})
