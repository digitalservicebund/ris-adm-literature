<!-- eslint-disable vue/multi-word-component-names -->
<script lang="ts" setup generic="T extends { id: string }">
import { ref } from 'vue'
import { useEditableList } from '@/views/adm/documentUnit/[documentNumber]/useEditableList'
import AktivzitierungInput from './AktivzitierungInput.vue'
import AktivzitierungAdmInput from './adm/AktivzitierungAdmInput.vue'
import AktivzitierungAdmItem from './adm/AktivzitierungAdmItem.vue'
import AktivzitierungItem from './AktivzitierungItem.vue'
import IconAdd from '~icons/material-symbols/add'
import { Button } from 'primevue'

const aktivzitierungList = defineModel<T[]>({ default: () => [] })

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
          <template #default="{ aktivzitierung }">
            <AktivzitierungAdmItem :aktivzitierung="aktivzitierung" />
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
        <AktivzitierungAdmInput :modelValue="modelValue" @update:modelValue="onUpdateModelValue" />
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
