<script setup lang="ts">
import TitleElement from '@/components/TitleElement.vue'
import { computed, ref } from 'vue'
import { missingAdmDocumentUnitFields, missingUliDocumentUnitFields } from '@/utils/validators'
import Plausibilitaetspruefung from '@/components/publication/Plausibilitaetspruefung.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { DocumentUnitStore } from '@/stores/types'
import { storeToRefs } from 'pinia'
import Publication from '@/components/publication/Publication.vue'
import { useRoute } from 'vue-router'
import { DocumentCategory } from '@/domain/documentType'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'

const route = useRoute()
const store = useStoreForRoute<DocumentUnitStore>()

const missingFields = computed(() =>
  route.meta.documentCategory === DocumentCategory.LITERATUR_UNSELBSTSTAENDIG
    ? missingUliDocumentUnitFields(store.documentUnit as UliDocumentationUnit)
    : missingAdmDocumentUnitFields(store.documentUnit as AdmDocumentationUnit),
)

const { error, isLoading } = storeToRefs(store)
const isPublished = ref(false)

async function onClickPublish() {
  isPublished.value = await store.publish()
}
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Abgabe" class="flex flex-col bg-white p-24">
      <TitleElement class="mb-24">Abgabe</TitleElement>
      <Plausibilitaetspruefung :missing-fields="missingFields" />
      <hr class="text-blue-500 my-24" />
      <Publication
        :is-published="isPublished"
        :is-loading="isLoading"
        :is-disabled="missingFields.length > 0"
        :error="error"
        @publish="onClickPublish"
      />
    </div>
  </div>
</template>
