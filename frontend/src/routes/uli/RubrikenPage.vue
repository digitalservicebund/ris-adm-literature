<script lang="ts" setup>
import { computed } from 'vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import YearInput from '@/components/input/YearInput.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { useUliDocumentUnitStore } from '@/stores/uliDocStore'

const store = useStoreForRoute<ReturnType<typeof useUliDocumentUnitStore>>()

const veroeffentlichungsjahr = computed({
  get: () => store.documentUnit?.veroeffentlichungsjahr,
  set: (newValue) => {
    store.documentUnit!.veroeffentlichungsjahr = newValue
  },
})
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col gap-24 p-24">
    <div id="formaldaten" aria-label="Formaldaten" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Formaldaten</TitleElement>
      <div class="flex flex-row gap-24">
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr">
          <YearInput
            id="veroeffentlichungsjahr"
            v-model="veroeffentlichungsjahr"
            aria-label="Veröffentlichungsjahr"
            :has-error="slotProps.hasError"
            @update:validation-error="slotProps.updateValidationError"
          />
        </InputField>
      </div>
    </div>
  </div>
</template>
