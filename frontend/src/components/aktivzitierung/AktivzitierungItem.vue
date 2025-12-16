<script lang="ts" setup generic="T extends { id: string; documentNumber?: string }">
import IconEdit from '~icons/ic/outline-edit'
import IconBaselineDescription from '~icons/ic/outline-class'
import AktivzitierungInput from './AktivzitierungInput.vue'
import { computed, type VNodeChild } from 'vue'
import IconClose from '~icons/ic/close'

const props = defineProps<{
  aktivzitierung: T
  isEditing: boolean
}>()

const emit = defineEmits<{
  update: [aktivzitierung: T]
  delete: [id: string]
  editStart: [id: string]
  cancelEdit: [void]
}>()

defineSlots<{
  // 1. Slot for rendering the READ-ONLY list item
  item(props: { aktivzitierung: T }): VNodeChild
  // 2. Slot for rendering the EDITABLE INPUT form (uses v-model structure)
  input(props: { modelValue: T; onUpdateModelValue: (value: T) => void }): VNodeChild
}>()

const onExpandAccordion = () => {
  emit('editStart', props.aktivzitierung.id)
}

const onUpdate = (item: T) => {
  emit('update', item)
}

const onClickCancel = () => {
  emit('cancelEdit')
}

const onDelete = (id: string) => {
  emit('delete', id)
  emit('cancelEdit')
}

const isFromSearch = computed(
  () => !!props.aktivzitierung.documentNumber, // search-based if documentNumber set
)
</script>

<template>
  <AktivzitierungInput
    v-if="isEditing"
    ref="inputRef"
    class="mt-16"
    :aktivzitierung="aktivzitierung"
    @update="onUpdate"
    @cancel="onClickCancel"
    @delete="onDelete"
    :show-cancel-button="true"
  >
    <template #default="{ modelValue, onUpdateModelValue }">
      <slot
        name="input"
        :modelValue="modelValue as T"
        :onUpdateModelValue="onUpdateModelValue"
      ></slot>
    </template>
  </AktivzitierungInput>
  <div v-else class="flex w-full items-center gap-10">
    <IconBaselineDescription class="text-neutral-800" />

    <div class="flex flex-col gap-2">
      <slot name="item" :aktivzitierung="aktivzitierung"></slot>
    </div>
    <div class="ml-auto flex items-center gap-8">
      <button
        v-if="!isFromSearch"
        v-tooltip.bottom="'Eintrag bearbeiten'"
        aria-label="Eintrag bearbeiten"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer ml-auto"
        @click="onExpandAccordion"
      >
        <IconEdit />
      </button>
      <button
        v-else
        v-tooltip.bottom="'Eintrag löschen'"
        aria-label="Eintrag löschen"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer"
        @click="onDelete(aktivzitierung.id)"
      >
        <IconClose />
      </button>
    </div>
  </div>
</template>
