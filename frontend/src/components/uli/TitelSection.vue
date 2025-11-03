<script lang="ts" setup>
import { computed } from 'vue'
import InputField from '@/components/input/InputField.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { useUliDocumentUnitStore } from '@/stores/uliDocStore'
import { Textarea } from 'primevue'

const store = useStoreForRoute<ReturnType<typeof useUliDocumentUnitStore>>()

const hauptsachtitel = computed({
  get: () => store.documentUnit?.hauptsachtitel,
  set: (newValue) => {
    store.documentUnit!.hauptsachtitel = newValue
  },
})

const dokumentarischerTitel = computed({
  get: () => store.documentUnit?.dokumentarischerTitel,
  set: (newValue) => {
    store.documentUnit!.dokumentarischerTitel = newValue
  },
})
</script>

<template>
  <div class="flex flex-col w-full gap-24">
    <InputField id="hauptsachtitel" v-slot="slotProps" label="Hauptsachtitel *">
      <Textarea
        id="hauptsachtitel"
        v-model="hauptsachtitel"
        aria-label="Hauptsachtitel"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
        rows="1"
        auto-resize
        fluid
      />
    </InputField>
    <InputField id="dokumentarischerTitel" v-slot="slotProps" label="Dokumentarischer Titel *">
      <Textarea
        id="dokumentarischerTitel"
        v-model="dokumentarischerTitel"
        aria-label="Dokumentarischer Titel"
        :has-error="slotProps.hasError"
        @update:validation-error="slotProps.updateValidationError"
        rows="1"
        auto-resize
        fluid
      />
    </InputField>
  </div>
</template>
