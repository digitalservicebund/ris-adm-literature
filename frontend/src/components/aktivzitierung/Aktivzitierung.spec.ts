import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'
import PrimeVue from 'primevue/config'
import Aktivzitierung from './Aktivzitierung.vue'

// Dummy type
type DummyT = { id: string; documentNumber?: string }

describe('Aktivzitierung', () => {
  it('renders creation panel if list is empty', () => {
    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
    })

    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('renders list items if list has entries', async () => {
    const initialItem: DummyT = { id: '1', documentNumber: 'DOC123' }

    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
      // Use defineModel initial value
      props: {
        modelValue: [initialItem], // assuming defineModel allows this initial prop override
      },
    })

    // The item slot should render documentNumber
    expect(screen.getByText('DOC123')).toBeInTheDocument()
  })

  it('renders initial list items and allows adding a new item', async () => {
    const user = userEvent.setup()
    const initialList: DummyT[] = [{ id: '1', documentNumber: 'DOC123' }]

    // Use v-model via "modelValue" prop + "update:modelValue" emit
    const model = {
      value: [...initialList],
    }

    const { emitted } = render(Aktivzitierung, {
      props: {
        modelValue: model.value,
      },
      global: {
        plugins: [PrimeVue],
      },
    })

    expect(screen.getByText('DOC123')).toBeInTheDocument()

    const addButton = screen.getByRole('button', { name: 'Weitere Angabe' })
    await user.click(addButton)

    // After click, creation panel (input textbox) should appear
    const input = screen.getByRole('textbox')
    expect(input).toBeInTheDocument()

    // Type a new documentNumber
    await user.type(input, 'NEWDOC')

    // Click "Übernehmen" button
    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    await user.click(saveButton)

    // Check that the component emitted updated list
    const updateEvents = emitted()['update:modelValue']
    expect(updateEvents).toBeTruthy()

    // The last emitted value should contain the new document
    const events = emitted()['update:modelValue'] as Array<[DummyT[]]>
    const finalPayload = events[events.length - 1]![0]

    expect(finalPayload.map((i) => i.documentNumber)).toContain('NEWDOC')
    expect(finalPayload.map((i) => i.documentNumber)).toContain('DOC123')
  })

  it('opens creation panel when "Weitere Angabe" button is clicked', async () => {
    const user = userEvent.setup()

    const initialItem: DummyT = { id: '1', documentNumber: 'DOC123' }

    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
      props: {
        modelValue: [initialItem],
      },
    })

    // Click "Weitere Angabe" button
    const addButton = screen.getByRole('button', { name: 'Weitere Angabe' })
    await user.click(addButton)

    // After click, creation panel (input textbox) should appear
    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })
})
