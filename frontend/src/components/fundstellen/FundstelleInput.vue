<script lang="ts" setup>
import { computed, ref } from 'vue'
import InputField from '../input/InputField.vue'
import { type Fundstelle, type Periodikum } from '@/domain/fundstelle'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import { useValidationStore } from '@/composables/useValidationStore'
import PeriodikumDropDown from './PeriodikumDropDown.vue'

const props = defineProps<{
  fundstelle?: Fundstelle
  showCancelButton: boolean
}>()

const emit = defineEmits<{
  updateFundstelle: [fundstelle: Fundstelle]
  deleteFundstelle: [id: string]
  cancel: [void]
}>()

const validationStore = useValidationStore<['periodikum', 'zitatstelle'][number]>()

const periodikum = ref<Periodikum | undefined>(props.fundstelle?.periodikum || undefined)
const zitatstelle = ref<string>(props.fundstelle?.zitatstelle || '')

const isInvalid = computed(() => !periodikum.value)

const onClickSave = () => {
  const isValid = validate()
  if (isValid) {
    const fundstelle = {
      id: props.fundstelle ? props.fundstelle.id : crypto.randomUUID(),
      periodikum: periodikum.value!,
      zitatstelle: zitatstelle.value,
    }

    emit('updateFundstelle', fundstelle)
  }
}

const onClickCancel = () => {
  periodikum.value = props.fundstelle?.periodikum || undefined
  zitatstelle.value = props.fundstelle?.zitatstelle || ''
  emit('cancel')
}

const onClickDelete = () => {
  emit('deleteFundstelle', props.fundstelle!.id)
}

const validate = () => {
  // validationStore.reset()
  let isValid = true
  console.log(periodikum.value)
  if (!periodikum.value) {
    validationStore.add('Pflichtfeld nicht befüllt', 'periodikum')
    isValid = false
  }
  if (!zitatstelle.value) {
    validationStore.add('Pflichtfeld nicht befüllt', 'zitatstelle')
    isValid = false
  }
  return isValid
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <InputField
        id="periodikum"
        label="Periodikum *"
        class="w-full"
        :validation-error="validationStore.getByField('periodikum')"
        v-slot="slotProps"
      >
        <PeriodikumDropDown
          input-id="periodikum"
          v-model="periodikum"
          :is-invalid="slotProps.hasError"
          @focus="validationStore.remove('periodikum')"
        />
      </InputField>
      <div class="flex w-full flex-col">
        <InputField
          id="zitatstelle"
          v-slot="slotProps"
          label="Zitatstelle *"
          :validation-error="validationStore.getByField('zitatstelle')"
        >
          <InputText
            id="zitatstelle"
            v-model="zitatstelle"
            :is-invalid="slotProps.hasError"
            aria-label="zitatstelle"
            size="small"
            fluid
            @focus="validationStore.remove('zitatstelle')"
          />
        </InputField>
        <span v-if="periodikum" class="ris-label3-regular pt-4">
          Zitierbeispiel: {{ periodikum.citationStyle }}
        </span>
      </div>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        :disabled="isInvalid"
        aria-label="Fundstelle übernehmen"
        label="Übernehmen"
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
        v-if="props.fundstelle"
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
