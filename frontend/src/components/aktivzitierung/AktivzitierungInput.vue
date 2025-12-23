<script lang="ts" setup generic="T extends { id: string; documentNumber?: string }">
import { computed, ref, watch } from 'vue'
import Button from 'primevue/button'
import type { AktivzitierungSearchParams } from '@/domain/documentUnit'

const props = defineProps<{
  aktivzitierung?: T
  showCancelButton: boolean
}>()

const emit = defineEmits<{
  update: [aktivzitierung: T]
  delete: [id: string]
  cancel: [void]
  search: [params: AktivzitierungSearchParams]
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
  emit('delete', aktivzitierungRef.value.id)
}

const onUpdate = (newValue: T) => {
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

const isEmpty = computed(() => {
  const value = aktivzitierungRef.value as Record<string, unknown>
  const entries = Object.entries(value).filter(([key]) => key !== 'id')

  if (entries.length === 0) return true

  return entries.every(([, v]) => {
    if (v === undefined || v === null) return true
    if (typeof v === 'string') return v.trim() === ''
    if (Array.isArray(v)) return v.length === 0
    return false
  })
})

function clearSearchFields() {
  aktivzitierungRef.value = createInitialT()
}

function onClickSearch() {
  emit('search', aktivzitierungRef.value)
}

defineExpose({
  clearSearchFields,
})
</script>

<template>
  <div>
    <div class="flex flex-col gap-24" :key="aktivzitierungRef.id">
      <slot :modelValue="aktivzitierungRef" :onUpdateModelValue="onUpdate"></slot>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        aria-label="Dokumente Suchen"
        label="Suchen"
        size="small"
        @click.stop="onClickSearch"
      />
      <Button
        aria-label="Aktivzitierung übernehmen"
        label="Übernehmen"
        severity="secondary"
        size="small"
        :disabled="isEmpty"
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
