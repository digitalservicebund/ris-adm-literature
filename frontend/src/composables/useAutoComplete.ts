import { ref } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import type { AutoCompleteDropdownClickEvent } from 'primevue/autocomplete'

// Should be exported from ris-ui
export interface AutoCompleteSuggestion {
  id: string
  label: string
  secondaryLabel?: string
}

/**
 * A reusable composable that provides debounced dropdown logic for PrimeVue AutoComplete.
 *
 * @param searchFn - A function that returns a list of suggestions based on an optional search query.
 *                   This function allows filtering and mapping of entities.
 *
 * @returns An object containing:
 *   - suggestions: A reactive array of dropdown suggestions
 *   - onComplete: Handler for the AutoComplete `complete` event
 *   - onDropdownClick: Handler for the AutoComplete `dropdown-click` event
 *   - onItemSelect: Handler for the AutoComplete `item-select` event
 *
 * @example
 * ```ts
 * const { suggestions, onComplete, onDropdownClick, onItemSelect } = useAutoComplete(query => {
 *   return items.value
 *     .filter(item => item.name.includes(query ?? ''))
 *     .map(item => ({ id: item.id, label: item.name }))
 * })
 * ```
 */
export function useAutoComplete(searchFn: (query?: string) => AutoCompleteSuggestion[]) {
  const suggestions = ref<AutoCompleteSuggestion[]>([])

  const search = (query?: string) => {
    suggestions.value = searchFn(query)
  }

  const searchDebounced = useDebounceFn(search, 250)

  /*
  Workaround for loading prop being ignored in PrimeVue AutoComplete:
  It is important that the suggestions.value be updated each time. Otherwise, the loading indicator will not disappear
  the second time that the default suggestions are invoked using the dropdown.

  Both onComplete and onDropdownClick are called when the dropdown is opened,
  but only onDropdownClick is called on close.

  See https://github.com/primefaces/primevue/issues/5601 for further information.
  */
  const onComplete = (event: AutoCompleteDropdownClickEvent | { query: undefined }) => {
    if (event.query) {
      // normal search for entered query
      searchDebounced(event.query)
      // } else if (modelValue.value) {
      // user has already made a selection, use that as the query
      //searchDebounced(modelValue.value?.name)
    } else {
      // dropdown was opened without any text entered or value pre-selected
      // a copy of the default suggestions is required since the loading
      searchDebounced()
    }
  }

  const onDropdownClick = (event: AutoCompleteDropdownClickEvent | { query: undefined }) => {
    if (event.query === undefined) {
      // dropdown has been closed
      suggestions.value = []
    } else {
      // onComplete will also fire, but with an empty query
      // therefore, call it again
      onComplete(event)
    }
  }

  const onItemSelect = () => {
    suggestions.value = []
  }

  return {
    suggestions,
    onComplete,
    onDropdownClick,
    onItemSelect,
  }
}
