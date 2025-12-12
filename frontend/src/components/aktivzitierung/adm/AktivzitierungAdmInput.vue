<script lang="ts" setup>
import InputField from '@/components/input/InputField.vue'
import InputText from 'primevue/inputtext'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import { computed } from 'vue'
import ZitierArtDropDown from '@/views/adm/documentUnit/[documentNumber]/rubriken/components/active-citation/ZitierArtDropDown.vue'
import type { ZitierArt } from '@/domain/zitierArt'

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
    emit('update:modelValue', { ...props.modelValue, ...{ citationType: val?.abbreviation } })
  },
})
</script>

<template>
  <div class="flex flex-col gap-24">
    <div class="flex flex-row gap-24">
      <InputField id="activeCitationPredicate" label="Art der Zitierung">
        <ZitierArtDropDown
          input-id="activeCitationPredicate"
          v-model="citationType"
          :invalid="false"
        />
      </InputField>
      <InputField id="documentNumber" v-slot="slotProps" label="Dokumentnummer">
        <InputText
          :id="slotProps.id"
          :model-value="modelValue?.documentNumber"
          @update:model-value="
            (documentNumber) =>
              emit('update:modelValue', { ...props.modelValue, ...{ documentNumber } })
          "
          aria-label="Dokumentnummer"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>
    </div>
  </div>
</template>
