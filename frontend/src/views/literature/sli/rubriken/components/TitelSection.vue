<script setup lang="ts">
import { ref, watch, useId, computed } from 'vue'
import LazyFieldToggle from './LazyFieldToggle.vue'
import InputField from '@/components/input/InputField.vue'
import { Textarea } from 'primevue'

const hauptsachtitel = defineModel<string>('hauptsachtitel')
const hauptsachtitelZusatz = defineModel<string>('hauptsachtitelZusatz')
const dokumentarischerTitel = defineModel<string>('dokumentarischerTitel')

const normalize = (value?: string) => (value ?? '').trim()

const normalizedHauptsachtitel = computed(() => normalize(hauptsachtitel.value))
const normalizedHauptsachtitelZusatz = computed(() => normalize(hauptsachtitelZusatz.value))
const normalizedDokumentarischerTitel = computed(() => normalize(dokumentarischerTitel.value))

const hasHauptsachtitel = computed(
  () => normalizedHauptsachtitel.value !== '' || normalizedHauptsachtitelZusatz.value !== '',
)

const hasDokumentarischerTitel = computed(() => normalizedDokumentarischerTitel.value !== '')

const showDokumentarischerTitel = ref(hasDokumentarischerTitel.value)

watch(hasDokumentarischerTitel, (hasValue) => {
  if (hasValue) {
    showDokumentarischerTitel.value = true
  }
})

function handleDokumentarischerTitelBlur() {
  const normalized = normalizedDokumentarischerTitel.value
  dokumentarischerTitel.value = normalized || undefined

  if (!normalized) {
    showDokumentarischerTitel.value = false
  }
}

const hauptsachtitelLabel = computed(() =>
  hasDokumentarischerTitel.value ? 'Hauptsachtitel' : 'Hauptsachtitel *',
)
const dokumentarischerTitelLabel = computed(() =>
  hasHauptsachtitel.value ? 'Dokumentarischer Titel' : 'Dokumentarischer Titel *',
)

const baseId = useId()
const hauptsachtitelId = `${baseId}-hauptsachtitel`
const hauptsachtitelZusatzId = `${baseId}-hauptsachtitel-zusatz`
const dokumentarischerTitelId = `${baseId}-dokumentarisch`
</script>

<template>
  <div class="flex flex-col gap-24">
    <InputField :id="hauptsachtitelId" :label="hauptsachtitelLabel">
      <Textarea
        :id="hauptsachtitelId"
        v-model="hauptsachtitel"
        :disabled="hasDokumentarischerTitel"
        auto-resize
        rows="1"
        fluid
      />
    </InputField>

    <InputField :id="hauptsachtitelZusatzId" label="Zusatz zum Hauptsachtitel">
      <Textarea
        :id="hauptsachtitelZusatzId"
        v-model="hauptsachtitelZusatz"
        :disabled="hasDokumentarischerTitel"
        auto-resize
        rows="1"
        fluid
      />
    </InputField>

    <LazyFieldToggle
      :input-id="dokumentarischerTitelId"
      button-label="Dokumentarischer Titel"
      v-model:visible="showDokumentarischerTitel"
      :disabled="hasHauptsachtitel"
    >
      <InputField :id="dokumentarischerTitelId" :label="dokumentarischerTitelLabel">
        <Textarea
          :id="dokumentarischerTitelId"
          v-model="dokumentarischerTitel"
          :disabled="hasHauptsachtitel"
          auto-resize
          rows="1"
          fluid
          @blur="handleDokumentarischerTitelBlur"
        />
      </InputField>
    </LazyFieldToggle>
  </div>
</template>
