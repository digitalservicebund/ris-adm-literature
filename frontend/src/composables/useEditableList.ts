import { ref, type Ref } from 'vue'

export function useEditableList<T extends { id: string }>(list: Ref<T[]>) {
  const isCreationPanelOpened = ref(false)

  const onRemoveItem = (id: string) => {
    list.value = list.value.filter((n) => n.id !== id)
  }

  const onAddItem = (item: T) => {
    list.value = [...list.value, item]
    isCreationPanelOpened.value = false
  }

  const onUpdateItem = (item: T) => {
    const index = list.value.findIndex((n) => n.id === item.id)
    list.value[index] = item
  }

  return {
    list,
    onAddItem,
    onUpdateItem,
    onRemoveItem,
    isCreationPanelOpened,
  }
}
