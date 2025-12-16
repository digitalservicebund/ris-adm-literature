import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'
import Aktivzitierung from './Aktivzitierung.vue'
import { ref, type Ref } from 'vue'
import type { UseFetchReturn } from '@vueuse/core'

const mockToastAdd = vi.fn()

vi.mock('primevue', () => {
  // Mock 'useToast' hook
  const useToast = vi.fn(() => ({
    add: mockToastAdd,
  }))

  // Mock components used in the component's template
  const Button = {
    props: ['label', 'severity', 'size', 'aria-label', 'disabled', 'class'],
    template: '<button><slot name="icon"></slot>{{ label }}</button>',
  }

  return {
    useToast,
    Button,
  }
})

// Dummy Aktivzitierung
type DummyT = { id: string; documentNumber?: string }

// Dummy Search Result
type DummySearchResult = {
  id: string
  documentNumber: string
  title: string
}

type FetchResultsFunction = (
  page: Ref<number>,
  itemsPerPage: number,
  searchParams: Ref,
) => UseFetchReturn<DummySearchResult>

const fetchSpy = vi.fn() as unknown as FetchResultsFunction

const mockFetchPaginatedData = vi.fn()
const mockSearchResults = ref<DummySearchResult[]>([])
const mockTotalRows = ref(0)
const mockIsFetching = ref(false)
const mockError = ref(null)

vi.mock('@/composables/usePagination', () => ({
  usePagination: vi.fn(() => ({
    firstRowIndex: ref(0),
    totalRows: mockTotalRows,
    items: mockSearchResults,
    fetchPaginatedData: mockFetchPaginatedData,
    isFetching: mockIsFetching,
    error: mockError,
  })),
}))

function renderComponent(props?: { modelValue: DummyT[] }) {
  return render(Aktivzitierung, {
    props: {
      modelValue: props?.modelValue,
      fetchResultsFn: fetchSpy,
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
      searchResult: `
        <template #default="{ searchResult, isAdded, onAdd }">
          <div data-testid="search-result-title">{{ searchResult.title }}</div>
          <button
            data-testid="search-result-add-btn"
            :disabled="isAdded"
            @click="onAdd(searchResult)"
          >
            Add
          </button>
        </template>
        `,
    },
  })
}

describe('Aktivzitierung', () => {
  const initialItem: DummyT = { id: '1', documentNumber: 'DOC123' }

  it('renders creation panel if list is empty', () => {
    renderComponent()

    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('renders list items if list has entries', async () => {
    renderComponent({
      modelValue: [initialItem],
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

    const { emitted } = renderComponent({
      modelValue: model.value,
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

    renderComponent({
      modelValue: [initialItem],
    })

    // Click "Weitere Angabe" button
    const addButton = screen.getByRole('button', { name: 'Weitere Angabe' })
    await user.click(addButton)

    // After click, creation panel (input textbox) should appear
    expect(screen.getByRole('textbox')).toBeInTheDocument()
  })

  it('handleEditStart sets editingItemId and closes creation panel', async () => {
    const user = userEvent.setup()

    renderComponent({
      modelValue: [initialItem],
    })

    // Click "edit" button of first item
    const editButtons = screen.getAllByRole('button', { name: 'Eintrag bearbeiten' })
    await user.click(editButtons[0]!)

    // The first item should now be in editing mode → save button visible
    expect(screen.getByRole('button', { name: 'Aktivzitierung übernehmen' })).toBeInTheDocument()
  })

  it('handleEditEnd clears editingItemId', async () => {
    const user = userEvent.setup()

    renderComponent({
      modelValue: [initialItem],
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

    renderComponent({
      modelValue: [initialItem],
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

    renderComponent({
      modelValue: [initialItem],
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
