<script lang="ts" setup>
import type { DocumentUnitSearchParams } from '@/domain/documentUnit'
import { Button, InputText } from 'primevue'
import { computed, ref } from 'vue'
import DateInput from '@/components/input/DateInput.vue'

const props = defineProps<{
  loading: boolean
}>()

const emit = defineEmits<{
  search: [value: DocumentUnitSearchParams]
}>()

const searchParams = ref<DocumentUnitSearchParams>({
  documentNumber: '',
  langueberschrift: '',
  fundstellen: '',
  zitierdaten: '',
})

const isLoading = ref(false)

const isSearchEmpty = computed(() => Object.values(searchParams.value).every((params) => !params))

async function handleSearch() {
  isLoading.value = true
  try {
    emit('search', searchParams.value)
  } catch (error) {
    console.error('Search failed:', error)
  } finally {
    isLoading.value = false
  }
}

function onClickReset() {
  searchParams.value = {
    documentNumber: '',
    langueberschrift: '',
    fundstellen: '',
    zitierdaten: '',
  }
  emit('search', searchParams.value)
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
      <InputText id="fundstelle" v-model="searchParams.fundstellen" />
      <label class="ris-label2-regular" for="zitierdatum">Zitierdatum</label>
      <DateInput
        id="zitierdaten"
        v-model="searchParams.zitierdaten"
        ariaLabel="Zitierdatum"
        mask="99.99.9999"
        placeholder="TT.MM.JJJJ"
      />
    </div>
    <div class="flex gap-24">
      <Button label="Ergebnisse zeigen" :disabled="props.loading" type="submit" />
      <Button label="Zurücksetzen" text :disabled="isSearchEmpty" @click="onClickReset" />
    </div>
  </form>
</template>
