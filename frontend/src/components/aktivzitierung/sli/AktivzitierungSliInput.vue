<script lang="ts" setup>
import InputField from '@/components/input/InputField.vue'
import InputText from 'primevue/inputtext'
import type { AktivzitierungSli } from '@/domain/AktivzitierungSli'
import { DocumentCategory } from '@/domain/documentType'
import DokumentTyp from '@/views/literature/DokumentTyp.vue'
import { RisChipsInput } from '@digitalservicebund/ris-ui/components'
import type { SliDocUnitSearchParams } from '@/domain/sli/sliDocumentUnit'
import { computed } from 'vue'

const props = defineProps<{
  modelValue: AktivzitierungSli
}>()

const emit = defineEmits<{
  'update:modelValue': [value: AktivzitierungSli]
  search: [searchParams: SliDocUnitSearchParams]
}>()

// Ensure verfasser is always an array - use computed to provide default
const verfasser = computed({
  get: () => props.modelValue.verfasser || [],
  set: (val: string[]) => {
    emit('update:modelValue', { ...props.modelValue, verfasser: val })
  },
})

// Ensure dokumenttypen is always an array
const dokumenttypen = computed({
  get: () => props.modelValue.dokumenttypen || [],
  set: (val) => {
    emit('update:modelValue', { ...props.modelValue, dokumenttypen: val })
  },
})
</script>

<template>
  <div class="flex flex-col gap-24">
    <InputField id="titel" v-slot="slotProps" label="Hauptsachtitel / Dokumentarischer Titel">
      <InputText
        :id="slotProps.id"
        :model-value="modelValue?.titel"
        @update:model-value="(titel) => emit('update:modelValue', { ...modelValue, titel })"
        aria-label="Hauptsachtitel / Dokumentarischer Titel"
        :invalid="slotProps.hasError"
        fluid
      />
    </InputField>
    <div class="flex flex-row gap-24">
      <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr">
        <InputText
          :id="slotProps.id"
          :model-value="modelValue?.veroeffentlichungsJahr"
          @update:model-value="
            (veroeffentlichungsJahr) =>
              emit('update:modelValue', { ...modelValue, veroeffentlichungsJahr })
          "
          aria-label="Veröffentlichungsjahr"
          fluid
        />
      </InputField>

      <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp">
        <DokumentTyp
          :input-id="slotProps.id"
          v-model="dokumenttypen"
          aria-label="Dokumenttyp"
          :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
          :invalid="slotProps.hasError"
        />
      </InputField>
    </div>

    <InputField id="verfasser" v-slot="slotProps" label="Verfasser/in">
      <RisChipsInput
        :input-id="slotProps.id"
        v-model="verfasser"
        aria-label="Verfasser/in"
        :invalid="slotProps.hasError"
      />
    </InputField>
  </div>
</template>
