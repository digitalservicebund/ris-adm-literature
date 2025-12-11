import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'
import PrimeVue from 'primevue/config'
import Aktivzitierung from './Aktivzitierung.vue'

// Dummy type
type DummyT = { id: string; documentNumber?: string }

describe('Aktivzitierung', () => {
  const initialItem: DummyT = { id: '1', documentNumber: 'DOC123' }

  it('renders creation panel if list is empty', () => {
    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
      slots: {
        item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="doc-number">{{ aktivzitierung.documentNumber }}</div>
          </template>`,
        input: `
          <template #default="{ modelValue, onUpdateModelValue }">
            <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
          </template>
          `,
      },
    })

    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('renders list items if list has entries', async () => {
    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
      // Use defineModel initial value
      props: {
        modelValue: [initialItem], // assuming defineModel allows this initial prop override
      },
      slots: {
        item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="doc-number">{{ aktivzitierung.documentNumber }}</div>
          </template>`,
        input: `
          <template #default="{ modelValue, onUpdateModelValue }">
            <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
          </template>
          `,
      },
    })

    // The item slot should render documentNumber
    expect(screen.getByText('DOC123')).toBeInTheDocument()
  })

  it('renders initial list items and allows adding a new item', async () => {
    const user = userEvent.setup()
    const initialList: DummyT[] = [initialItem]

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
      slots: {
        item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="doc-number">{{ aktivzitierung.documentNumber }}</div>
          </template>`,
        input: `
          <template #default="{ modelValue, onUpdateModelValue }">
            <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
          </template>
          `,
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

    render(Aktivzitierung, {
      global: {
        plugins: [PrimeVue],
      },
      props: {
        modelValue: [initialItem],
      },
      slots: {
        item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="doc-number">{{ aktivzitierung.documentNumber }}</div>
          </template>`,
        input: `
          <template #default="{ modelValue, onUpdateModelValue }">
            <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
          </template>
          `,
      },
    })

    // Click "Weitere Angabe" button
    const addButton = screen.getByRole('button', { name: 'Weitere Angabe' })
    await user.click(addButton)

    // After click, creation panel (input textbox) should appear
    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('handleEditStart sets editingItemId and closes creation panel', async () => {
    const user = userEvent.setup()

    render(Aktivzitierung, {
      props: { modelValue: [...[initialItem]] },
      global: {
        plugins: [PrimeVue],
      },
    })

    // Click "edit" button of first item
    const editButtons = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })
    await user.click(editButtons[0]!)

    // The first item should now be in editing mode → save button visible
    expect(screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })).toBeInTheDocument()
  })

  it('handleEditEnd clears editingItemId', async () => {
    const user = userEvent.setup()

    render(Aktivzitierung, {
      props: { modelValue: [...[initialItem]] },
      global: {
        plugins: [PrimeVue],
      },
    })

    // Start editing first item
    const editButton = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })[0]!
    await user.click(editButton)

    // Click "Abbrechen" → handleCancelEdit → should clear editingItemId
    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    if (cancelButton) await user.click(cancelButton)

    // The save button should no longer exist
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung übernehmen' }),
    ).not.toBeInTheDocument()
  })

  it('handleCancelEdit clears editingItemId', async () => {
    const user = userEvent.setup()

    render(Aktivzitierung, {
      props: { modelValue: [...[initialItem]] },
      global: {
        plugins: [PrimeVue],
      },
    })

    const editButton = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })[0]!
    await user.click(editButton)

    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    if (cancelButton) await user.click(cancelButton)

    // Editing mode ended → save button gone
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung übernehmen' }),
    ).not.toBeInTheDocument()
  })

  it('handleUpdateItem calls onUpdateItem and ends editing', async () => {
    const user = userEvent.setup()
    const newDocNumber = 'UPDATED_DOC'

    render(Aktivzitierung, {
      props: { modelValue: [...[initialItem]] },
      global: {
        plugins: [PrimeVue],
      },
      slots: {
        item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="doc-number">{{ aktivzitierung.documentNumber }}</div>
          </template>`,
        input: `
          <template #default="{ modelValue, onUpdateModelValue }">
            <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
          </template>
          `,
      },
    })

    // Start editing first item
    const editButton = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })[0]!
    await user.click(editButton)

    // Update the documentNumber
    const input = screen.getByRole('textbox') as HTMLInputElement
    await user.clear(input)
    await user.type(input, newDocNumber)

    // Click save → triggers handleUpdateItem
    const saveButton = screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })
    await user.click(saveButton)

    expect(screen.getByText('UPDATED_DOC')).toBeVisible()
    expect(
      screen.queryByRole('button', { name: 'Aktivzitierung übernehmen' }),
    ).not.toBeInTheDocument()
  })
})
