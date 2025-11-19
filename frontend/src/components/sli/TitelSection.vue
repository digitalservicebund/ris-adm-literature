<script setup lang="ts">
import LazyFieldToggle from '@/components/LazyFieldToggle.vue'
import InputField from '@/components/input/InputField.vue'
import { Textarea } from 'primevue'
import { ref, watch, useId } from 'vue'

const hauptsachtitel = defineModel<string>('hauptsachtitel')
const dokumentarischerTitel = defineModel<string>('dokumentarischerTitel')
const showDokumentarischerTitel = ref(!!dokumentarischerTitel.value)

watch(dokumentarischerTitel, (value) => {
  const normalized = (value ?? '').trim()
  if (normalized.length > 0) {
    showDokumentarischerTitel.value = true
  }
})

function handleDokumentarischerTitelBlur() {
  const normalized = (dokumentarischerTitel.value ?? '').trim()
  dokumentarischerTitel.value = normalized
  if (!normalized) {
    showDokumentarischerTitel.value = false
  }
}

const baseId = useId()
const hauptsachtitelId = `${baseId}-hauptsachtitel`
const dokumentarischerTitelId = `${baseId}-dokumentarisch`
</script>

<template>
  <div class="flex flex-col gap-24">
    <InputField :id="hauptsachtitelId" label="Hauptsachtitel *">
      <Textarea
        :id="hauptsachtitelId"
        v-model="hauptsachtitel"
        :disabled="showDokumentarischerTitel && dokumentarischerTitel !== ''"
        auto-resize
        fluid
      />
    </InputField>

    <LazyFieldToggle
      :input-id="dokumentarischerTitelId"
      button-label="Dokumentarischer Titel"
      v-model:visible="showDokumentarischerTitel"
    >
      <InputField :id="dokumentarischerTitelId" label="Dokumentarischer Titel *">
        <Textarea
          :id="dokumentarischerTitelId"
          v-model="dokumentarischerTitel"
          :disabled="!!hauptsachtitel"
          auto-resize
          fluid
          @blur="handleDokumentarischerTitelBlur"
        />
      </InputField>
    </LazyFieldToggle>
  </div>
</template>
