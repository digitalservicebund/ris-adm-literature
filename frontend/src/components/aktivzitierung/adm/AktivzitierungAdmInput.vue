<script lang="ts" setup>
import InputField from '@/components/input/InputField.vue'
import InputText from 'primevue/inputtext'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'

const props = defineProps<{
  modelValue: AktivzitierungAdm
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: AktivzitierungAdm): void
}>()
</script>

<template>
  <div class="flex flex-col gap-24">
    <div class="flex flex-row gap-24">
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
