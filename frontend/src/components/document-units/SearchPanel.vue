<script lang="ts" setup>
import type { DocumentUnitSearchParams } from '@/domain/documentUnit'
import { Button, InputText } from 'primevue'
import { computed, ref } from 'vue'

const emit = defineEmits<{
  search: [value: DocumentUnitSearchParams]
}>()

const searchParams = ref<DocumentUnitSearchParams>({
  documentNumber: '',
})

const isSearchEmpty = computed(() =>
  Object.values(searchParams.value).every((params) => params === ''),
)

function onClickSearch() {
  emit('search', searchParams.value)
}

function onClickReset() {
  searchParams.value = {
    documentNumber: '',
  }
}
</script>

<template>
  <div class="p-32 bg-blue-200">
    <h2 class="ris-subhead-bold mb-32">Schnellsuche</h2>
    <div class="mb-32 grid grid-cols-[1fr_3fr_1fr_3fr] items-center gap-x-44 gap-y-24">
      <label class="ris-label2-regular" for="documentNumber">Dokumentnummer</label>
      <InputText id="documentNumber" v-model="searchParams.documentNumber" />
    </div>
    <div class="flex gap-24">
      <Button label="Ergebnisse zeigen" @click="onClickSearch" />
      <Button label="Zurücksetzen" text :disabled="isSearchEmpty" @click="onClickReset" />
    </div>
  </div>
</template>
