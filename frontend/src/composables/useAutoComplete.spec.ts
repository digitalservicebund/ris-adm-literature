import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useAutoComplete, type AutoCompleteSuggestion } from '@/composables/useAutoComplete'
import type { AutoCompleteDropdownClickEvent } from 'primevue/autocomplete'

// Mock debounce to avoid delay
vi.mock('@vueuse/core', async () => {
  const actual = await vi.importActual('@vueuse/core')

  return {
    ...actual,
    useDebounceFn: (fn: unknown) => fn,
  }
})

const mockItems = [
  { id: '1', label: 'Berlin' },
  { id: '2', label: 'Bremen' },
  { id: '3', label: 'Bavaria' },
]

function mockSearchFn(query?: string): AutoCompleteSuggestion[] {
  return mockItems.filter((item) => item.label.toLowerCase().includes((query ?? '').toLowerCase()))
}

describe('useAutoComplete', () => {
  let composable: ReturnType<typeof useAutoComplete>

  beforeEach(() => {
    composable = useAutoComplete(mockSearchFn)
  })

  it('returns filtered suggestions based on query (via onComplete)', async () => {
    composable.onComplete({ query: 'br' } as AutoCompleteDropdownClickEvent)

    expect(composable.suggestions.value).toEqual([{ id: '2', label: 'Bremen' }])
  })

  it('clears suggestions when dropdown is closed', () => {
    // First set something
    composable.suggestions.value = mockItems

    // Simulate dropdown close
    composable.onDropdownClick({ query: undefined })

    expect(composable.suggestions.value).toEqual([])
  })

  it('calls onComplete from onDropdownClick when dropdown opened', async () => {
    composable.onDropdownClick({ query: 'ba' } as AutoCompleteDropdownClickEvent)

    expect(composable.suggestions.value).toEqual([{ id: '3', label: 'Bavaria' }])
  })

  it('clears suggestions on item select', () => {
    composable.suggestions.value = mockItems

    composable.onItemSelect()

    expect(composable.suggestions.value).toEqual([])
  })

  it('returns all suggestions when query is empty', async () => {
    composable.onComplete({ query: '' } as AutoCompleteDropdownClickEvent)

    expect(composable.suggestions.value).toEqual(mockItems)
  })

  it('returns all suggestions when query is undefined', () => {
    composable.onComplete({ query: undefined })

    expect(composable.suggestions.value).toEqual(mockItems)
  })

  it('returns empty array when no matches found', () => {
    composable.onComplete({ query: 'zzz' } as AutoCompleteDropdownClickEvent)

    expect(composable.suggestions.value).toEqual([])
  })
})
