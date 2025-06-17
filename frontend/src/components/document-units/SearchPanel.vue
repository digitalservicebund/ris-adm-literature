<script lang="ts" setup>
import type { DocumentUnitSearchParams } from '@/domain/documentUnit'
import { Button, InputText } from 'primevue'
import { computed, ref } from 'vue'
import DateInput from '@/components/input/DateInput.vue'
import InputField from '@/components/input/InputField.vue'
import type { ValidationError } from '@/components/input/types.ts'

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

const isSearchEmpty = computed(() => Object.values(searchParams.value).every((params) => !params))
const hasValidationError = ref<boolean>(false)
async function handleSearch() {
  if (hasValidationError.value) {
    return
  }
  emit('search', searchParams.value)
}

function onValidationError(ve: ValidationError | undefined) {
  hasValidationError.value = !!ve
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
  <form data-testid="search-form" class="p-32 bg-blue-200" @submit.prevent="handleSearch">
    <h2 class="ris-subhead-bold mb-32">Schnellsuche</h2>
    <div class="mb-32 grid grid-cols-[1fr_3fr_1fr_3fr] items-center gap-x-44 gap-y-24">
      <label class="ris-label2-regular" for="documentNumber">Dokumentnummer</label>
      <InputText id="documentNumber" v-model="searchParams.documentNumber" />
      <label class="ris-label2-regular" for="langueberschrift">Amtl. Langüberschrift</label>
      <InputText id="langueberschrift" v-model="searchParams.langueberschrift" />
      <label class="ris-label2-regular" for="fundstelle">Fundstelle</label>
      <InputText id="fundstelle" v-model="searchParams.fundstellen" />
      <label class="ris-label2-regular" for="zitierdatum">Zitierdatum</label>
      <InputField id="zitierdatum" v-slot="slotProps" @update:validation-error="onValidationError">
        <DateInput
          id="zitierdaten"
          v-model="searchParams.zitierdaten"
          ariaLabel="Zitierdatum"
          mask="99.99.9999"
          placeholder="TT.MM.JJJJ"
          :has-error="slotProps.hasError"
          @update:validation-error="slotProps.updateValidationError"
        />
      </InputField>
    </div>
    <div class="flex gap-24">
      <Button
        label="Ergebnisse zeigen"
        :disabled="props.loading || hasValidationError"
        type="submit"
      />
      <Button label="Zurücksetzen" text :disabled="isSearchEmpty" @click="onClickReset" />
    </div>
  </form>
</template>
