<script lang="ts" setup>
import { computed } from 'vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import { DocumentCategory } from '@/domain/documentType'
import { useScrollToHash } from '@/composables/useScrollToHash'
import InputText from 'primevue/inputtext'
import TitelSection from '@/components/sli/TitelSection.vue'
import DokumentTyp from '@/components/DokumentTyp.vue'

const store = useStoreForRoute<ReturnType<typeof useSliDocumentUnitStore>>()

const veroeffentlichungsjahr = computed({
  get: () => store.documentUnit?.veroeffentlichungsjahr,
  set: (newValue) => {
    store.documentUnit!.veroeffentlichungsjahr = newValue
  },
})

const dokumenttypen = computed({
  get: () => store.documentUnit?.dokumenttypen || [],
  set: (newValue) => {
    store.documentUnit!.dokumenttypen = newValue
  },
})

const hauptsachtitel = computed({
  get: () => store.documentUnit?.hauptsachtitel ?? '',
  set: (newValue) => {
    store.documentUnit!.hauptsachtitel = newValue
  },
})

const dokumentarischerTitel = computed({
  get: () => store.documentUnit?.dokumentarischerTitel ?? '',
  set: (newValue) => {
    store.documentUnit!.dokumentarischerTitel = newValue
  },
})

const hauptsachtitelZusatz = computed({
  get: () => store.documentUnit?.hauptsachtitelZusatz ?? '',
  set: (newValue) => {
    store.documentUnit!.hauptsachtitelZusatz = newValue
  },
})

useScrollToHash()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col gap-24 p-24">
    <div id="formaldaten" aria-label="Formaldaten" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Formaldaten</TitleElement>
      <div class="flex flex-row gap-24">
        <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp *">
          <DokumentTyp
            inputId="dokumenttypen"
            v-model="dokumenttypen"
            aria-label="Dokumenttyp"
            :invalid="slotProps.hasError"
            :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
          />
        </InputField>
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr *">
          <InputText
            :id="slotProps.id"
            v-model="veroeffentlichungsjahr"
            aria-label="Veröffentlichungsjahr"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
      </div>
      <TitelSection
        v-model:hauptsachtitel="hauptsachtitel"
        v-model:dokumentarischer-titel="dokumentarischerTitel"
        v-model:hauptsachtitel-zusatz="hauptsachtitelZusatz"
      />
      <div>
        <p class="relative pl-12 before:content-['*'] before:absolute before:left-0">
          Pflichtfelder für die Veröffentlichung
        </p>
        <p class="pl-12">
          Hinweis: Hauptsachtitel oder Dokumentarischer Titel muss für die Veröffentlichung erfasst
          werden
        </p>
      </div>
    </div>
  </div>
</template>
