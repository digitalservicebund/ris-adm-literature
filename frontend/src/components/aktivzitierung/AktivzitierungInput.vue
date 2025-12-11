<script lang="ts" setup generic="T extends { id: string }">
import { computed, ref } from 'vue'
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

const aktivzitierungRef = ref<T>((props.aktivzitierung as T) || {})

const isExistingEntry = computed(() => !!props.aktivzitierung?.id)

function onClickSave() {
  emit('update', {
    ...aktivzitierungRef.value,
    ...{ id: props.aktivzitierung?.id || crypto.randomUUID() },
  })
  aktivzitierungRef.value = {}
}

function onClickCancel() {
  emit('cancel')
}

function onClickDelete() {
  if (props.aktivzitierung?.id) {
    emit('delete', props.aktivzitierung.id)
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const onUpdate = (newValue: any) => {
  aktivzitierungRef.value = newValue
}
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
