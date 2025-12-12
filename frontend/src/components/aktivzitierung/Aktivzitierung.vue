<!-- eslint-disable vue/multi-word-component-names -->
<script lang="ts" setup generic="T extends { id: string }">
import { ref } from 'vue'
import { useEditableList } from '@/views/adm/documentUnit/[documentNumber]/useEditableList'
import AktivzitierungInput from './AktivzitierungInput.vue'
import AktivzitierungItem from './AktivzitierungItem.vue'
import IconAdd from '~icons/material-symbols/add'
import { Button } from 'primevue'

const aktivzitierungList = defineModel<T[]>({ default: () => [] })
defineSlots<{
  // 1. Slot for rendering the READ-ONLY list item
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  item(props: { aktivzitierung: T }): any
  // 2. Slot for rendering the EDITABLE INPUT form (uses v-model structure)
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  input(props: { modelValue: any; onUpdateModelValue: (value: T) => void }): any
}>()

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungList)

const editingItemId = ref<string | undefined>(undefined)

function handleEditStart(itemId: string) {
  if (isCreationPanelOpened.value) {
    isCreationPanelOpened.value = false
  }
  editingItemId.value = itemId
}

function handleEditEnd() {
  editingItemId.value = undefined
}

function handleUpdateItem(item: T) {
  onUpdateItem(item)
  handleEditEnd()
}

function handleAddItem(item: T) {
  onAddItem(item)
  isCreationPanelOpened.value = true
}

function handleCancelEdit() {
  handleEditEnd()
}
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungAdmList">
    <ol
      v-if="aktivzitierungList.length > 0"
      aria-label="Aktivzitierung Liste"
      class="border-t-1 border-blue-300"
    >
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="aktivzitierung in aktivzitierungList"
        :key="aktivzitierung.id"
      >
        <AktivzitierungItem
          :aktivzitierung="aktivzitierung"
          :is-editing="editingItemId === aktivzitierung.id"
          @update="handleUpdateItem"
          @edit-start="handleEditStart(aktivzitierung.id)"
          @cancel-edit="handleCancelEdit"
          @delete="onRemoveItem"
        >
          <template #item="{ aktivzitierung }">
            <slot name="item" :aktivzitierung="aktivzitierung"></slot>
          </template>

          <template #input="{ modelValue, onUpdateModelValue }">
            <slot
              name="input"
              :modelValue="modelValue"
              :onUpdateModelValue="onUpdateModelValue"
            ></slot>
          </template>
        </AktivzitierungItem>
      </li>
    </ol>
    <AktivzitierungInput
      ref="inputRef"
      v-if="isCreationPanelOpened || aktivzitierungList.length === 0"
      class="mt-16"
      @update="handleAddItem"
      @cancel="isCreationPanelOpened = false"
      :show-cancel-button="false"
    >
      <template #default="{ modelValue, onUpdateModelValue }">
        <slot name="input" :modelValue="modelValue" :onUpdateModelValue="onUpdateModelValue"></slot>
      </template>
    </AktivzitierungInput>
    <Button
      v-else-if="!editingItemId"
      class="mt-16"
      aria-label="Weitere Angabe"
      severity="secondary"
      label="Weitere Angabe"
      size="small"
      @click="isCreationPanelOpened = true"
    >
      <template #icon><IconAdd /></template>
    </Button>
  </div>
</template>
