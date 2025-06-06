<script lang="ts" setup>
import type { DocumentUnitSearchParams } from '@/domain/documentUnit'
import { Button, InputText } from 'primevue'
import { computed, ref } from 'vue'

const emit = defineEmits<{
  search: [value: DocumentUnitSearchParams]
}>()

const searchParams = ref<DocumentUnitSearchParams>({
  documentNumber: '',
  langueberschrift: '',
  fundstelle: '',
})

const isSearchEmpty = computed(() =>
  Object.values(searchParams.value).every((params) => params === ''),
)

function handleSearch() {
  emit('search', searchParams.value)
}

function onClickReset() {
  searchParams.value = {
    documentNumber: '',
    langueberschrift: '',
    fundstelle: '',
  }
}
</script>

<template>
  <form class="p-32 bg-blue-200" @submit.prevent="handleSearch">
    <h2 class="ris-subhead-bold mb-32">Schnellsuche</h2>
    <div class="mb-32 grid grid-cols-[1fr_3fr_1fr_3fr] items-center gap-x-44 gap-y-24">
      <label class="ris-label2-regular" for="documentNumber">Dokumentnummer</label>
      <InputText id="documentNumber" v-model="searchParams.documentNumber" />
      <label class="ris-label2-regular" for="langueberschrift">Amtl. Langüberschrift</label>
      <InputText id="langueberschrift" v-model="searchParams.langueberschrift" />
      <label class="ris-label2-regular" for="fundstelle">Fundstelle</label>
      <InputText id="fundstelle" v-model="searchParams.fundstelle" />
    </div>
    <div class="flex gap-24">
      <Button label="Ergebnisse zeigen" disabled type="submit" />
      <Button label="Zurücksetzen" text :disabled="isSearchEmpty" @click="onClickReset" />
    </div>
  </form>
</template>
