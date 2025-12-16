<script lang="ts" setup>
import InputField from '@/components/input/InputField.vue'
import InputText from 'primevue/inputtext'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import { computed } from 'vue'
import type { ZitierArt } from '@/domain/zitierArt'
import ZitierArtDropDown from '@/components/dropdown/ZitierArtDropDown.vue'
import InstitutionDropDown from '@/components/dropdown/InstitutionDropDown.vue'
import type { Institution } from '@/domain/normgeber'
import DateInput from '@/components/input/DateInput.vue'
import PeriodikumDropDown from '@/components/dropdown/PeriodikumDropDown.vue'
import type { Periodikum } from '@/domain/fundstelle'
import DokumentTypDropDown from '@/components/dropdown/DokumentTypDropDown.vue'
import { DocumentCategory } from '@/domain/documentType'

const props = defineProps<{
  modelValue: AktivzitierungAdm
}>()

const emit = defineEmits<{
  'update:modelValue': [value: AktivzitierungAdm]
}>()

const citationType = computed({
  get: () => {
    return props.modelValue.citationType
      ? ({ id: props.modelValue.citationType, label: props.modelValue.citationType } as ZitierArt)
      : undefined
  },
  set: (val: ZitierArt | undefined) => {
    emit('update:modelValue', { ...props.modelValue, citationType: val?.abbreviation })
  },
})

const normgeber = computed({
  get: () => {
    return props.modelValue.normgeber
      ? ({ id: props.modelValue.normgeber, name: props.modelValue.normgeber } as Institution)
      : undefined
  },
  set: (val: Institution | undefined) => {
    emit('update:modelValue', { ...props.modelValue, normgeber: val?.name })
  },
})

const periodikum = computed({
  get: () => {
    return props.modelValue.periodikum
      ? ({
          id: props.modelValue.periodikum,
          abbreviation: props.modelValue.periodikum,
          title: props.modelValue.periodikum,
        } as Periodikum)
      : undefined
  },
  set: (val: Periodikum | undefined) => {
    emit('update:modelValue', { ...props.modelValue, periodikum: val?.abbreviation })
  },
})
</script>

<template>
  <div class="flex flex-col gap-24">
    <div class="flex flex-row gap-24">
      <InputField id="activeCitationPredicate" label="Art der Zitierung" v-slot="slotProps">
        <ZitierArtDropDown :input-id="slotProps.id" v-model="citationType" :invalid="false" />
      </InputField>
      <InputField id="normgeber" label="Normgeber" v-slot="slotProps">
        <InstitutionDropDown :input-id="slotProps.id" v-model="normgeber" :isInvalid="false" />
      </InputField>
    </div>
    <div class="flex flex-row gap-24">
      <InputField id="inkrafttretedatum" label="Datum des Inkrafttretens" v-slot="slotProps">
        <DateInput
          :id="slotProps.id"
          :model-value="modelValue?.inkrafttretedatum"
          @update:model-value="
            (inkrafttretedatum) =>
              emit('update:modelValue', { ...props.modelValue, inkrafttretedatum })
          "
          ariaLabel="Inkrafttretedatum"
          class="ds-input-medium"
          is-future-date
          :has-error="false"
        ></DateInput>
      </InputField>
      <InputField id="aktenzeichen" v-slot="slotProps" label="Aktenzeichen">
        <InputText
          :id="slotProps.id"
          :model-value="modelValue?.aktenzeichen"
          @update:model-value="
            (aktenzeichen) => emit('update:modelValue', { ...props.modelValue, aktenzeichen })
          "
          aria-label="Aktenzeichen"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>
    </div>
    <div class="flex flex-row gap-24">
      <InputField id="periodikum" label="Periodikum" v-slot="slotProps">
        <PeriodikumDropDown :input-id="slotProps.id" v-model="periodikum" :invalid="false" />
      </InputField>
      <InputField id="zitatstelle" v-slot="slotProps" label="Zitatstelle">
        <InputText
          :id="slotProps.id"
          :model-value="modelValue?.zitatstelle"
          @update:model-value="
            (zitatstelle) => emit('update:modelValue', { ...props.modelValue, zitatstelle })
          "
          aria-label="Zitatstelle"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>
    </div>
    <div class="flex flex-row gap-24">
      <InputField id="documentNumber" v-slot="slotProps" label="Dokumentnummer">
        <InputText
          :id="slotProps.id"
          :model-value="modelValue?.documentNumber"
          @update:model-value="
            (documentNumber) => emit('update:modelValue', { ...props.modelValue, documentNumber })
          "
          aria-label="Dokumentnummer"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>
      <InputField id="docType" label="Dokumenttyp" v-slot="slotProps">
        <DokumentTypDropDown
          :input-id="slotProps.id"
          :model-value="modelValue?.dokumenttyp"
          @update:model-value="
            (dokumenttyp) => emit('update:modelValue', { ...props.modelValue, dokumenttyp })
          "
          :isInvalid="false"
          :document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
        />
      </InputField>
    </div>
  </div>
</template>
