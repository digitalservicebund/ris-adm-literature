<script setup lang="ts">
import Button from 'primevue/button'
import IconCheck from '~icons/material-symbols/check'
import TitleElement from '@/components/TitleElement.vue'
import { computed } from 'vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import type { DocumentUnit } from '@/domain/documentUnit'
import { missingDocumentUnitFields } from '@/utils/validators'
import Plausibilitaetspruefung from '@/components/publication/Plausibilitaetspruefung.vue'

const store = useDocumentUnitStore()

const missingFields = computed(() => missingDocumentUnitFields(store.documentUnit as DocumentUnit))
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Abgabe" class="flex flex-col bg-white p-24">
      <TitleElement class="mb-24">Abgabe</TitleElement>
      <Plausibilitaetspruefung :missing-fields="missingFields" />
      <hr class="text-blue-500 my-24" />
      <div class="flex flex-row">
        <Button
          :disabled="missingFields.length > 0"
          label="Zur Veröffentlichung freigeben"
          :loading="false"
          :text="false"
        >
          <template #icon>
            <IconCheck />
          </template>
        </Button>
      </div>
    </div>
  </div>
</template>
