<script lang="ts" setup generic="T extends { id: string }">
import { computed, ref, watch } from 'vue'
import Button from 'primevue/button'

const props = defineProps<{
  aktivzitierung?: T
  showCancelButton: boolean
}>()

const emit = defineEmits<{
  update: [aktivzitierung: T]
  delete: [id: string]
  cancel: [void]
}>()

const createInitialT = (): T => {
  return { id: crypto.randomUUID() } as T
}

const aktivzitierungRef = ref<T>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitialT(),
)

const isExistingEntry = computed(() => !!props.aktivzitierung?.id)

function onClickSave() {
  emit('update', aktivzitierungRef.value)
  if (!isExistingEntry.value) {
    aktivzitierungRef.value = createInitialT()
  }
}

function onClickCancel() {
  emit('cancel')
}

function onClickDelete() {
  if (props.aktivzitierung?.id) {
    emit('delete', props.aktivzitierung?.id)
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const onUpdate = (newValue: any) => {
  aktivzitierungRef.value = newValue
}

watch(
  () => props.aktivzitierung,
  (newVal: T | undefined) => {
    if (newVal) {
      aktivzitierungRef.value = { ...newVal } as T
    }
  },
)
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <slot :modelValue="aktivzitierungRef" :onUpdateModelValue="onUpdate"></slot>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        aria-label="Aktivzitierung übernehmen"
        label="Übernehmen"
        severity="secondary"
        size="small"
        @click.stop="onClickSave"
      />
      <Button
        v-if="showCancelButton"
        aria-label="Abbrechen"
        label="Abbrechen"
        size="small"
        text
        @click.stop="onClickCancel"
      />
      <Button
        v-if="isExistingEntry"
        class="ml-auto"
        aria-label="Eintrag löschen"
        severity="danger"
        label="Eintrag löschen"
        size="small"
        @click.stop="onClickDelete"
      />
    </div>
  </div>
</template>
