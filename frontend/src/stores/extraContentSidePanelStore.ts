import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import useQuery from '@/composables/useQueryFromRoute'

export const useExtraContentSidePanelStore = defineStore('extraSidePanelStore', () => {
  const isExpanded = ref<boolean>(false)

  const { pushQueryToRoute } = useQuery()
  const route = useRoute()

  /**
   * Expands or collapses the panel.
   * Can be forced by passing a boolean parameter. Otherwise, it will collapse when expanded and expand when collapsed.
   * Pushes the state to the route as a query parameter.
   * @param expand optional boolean to enforce expanding or collapsing
   */
  function togglePanel(expand?: boolean): boolean {
    isExpanded.value = expand ?? !isExpanded.value
    pushQueryToRoute({
      ...route.query,
      showNotePanel: isExpanded.value.toString(),
    })
    return isExpanded.value
  }

  return {
    togglePanel,
    isExpanded,
  }
})
