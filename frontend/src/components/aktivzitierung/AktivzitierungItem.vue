<script lang="ts" setup generic="T extends { id: string }">
import IconEdit from '~icons/ic/outline-edit'
import IconBaselineDescription from '~icons/ic/outline-class'
import AktivzitierungInput from './AktivzitierungInput.vue'
import AktivzitierungAdmInput from './adm/AktivzitierungAdmInput.vue'

const props = defineProps<{
  aktivzitierung: T
  isEditing?: boolean
}>()

const emit = defineEmits<{
  update: [aktivzitierung: T]
  delete: [id: string]
  editStart: [id: string]
  cancelEdit: [void]
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
    :show-cancel-button="false"
  >
    <template #default="{ modelValue, onUpdateModelValue }">
      <AktivzitierungAdmInput :modelValue="modelValue" @update:modelValue="onUpdateModelValue" />
    </template>
  </AktivzitierungInput>
  <div v-else class="flex w-full items-center gap-10">
    <IconBaselineDescription class="text-neutral-800" />

    <div class="flex flex-col gap-2">
      <slot :aktivzitierung="aktivzitierung"></slot>
    </div>
    <div class="ml-auto flex items-center gap-8">
      <button
        v-tooltip.bottom="'Eintrag bearbeiten'"
        aria-label="Eintrag bearbeiten"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer ml-auto"
        @click="onExpandAccordion"
      >
        <IconEdit />
      </button>
    </div>
  </div>
</template>
