<script lang="ts" setup>
import { computed } from 'vue'
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import { useScrollToHash } from '@/composables/useScrollToHash'
import InputText from 'primevue/inputtext'

const store = useStoreForRoute<ReturnType<typeof useSliDocumentUnitStore>>()

const veroeffentlichungsjahr = computed({
  get: () => store.documentUnit?.veroeffentlichungsjahr,
  set: (newValue) => {
    store.documentUnit!.veroeffentlichungsjahr = newValue
  },
})

useScrollToHash()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col gap-24 p-24">
    <div id="formaldaten" aria-label="Formaldaten" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Formaldaten</TitleElement>
      <div class="flex flex-row gap-24">
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
    </div>
  </div>
</template>
