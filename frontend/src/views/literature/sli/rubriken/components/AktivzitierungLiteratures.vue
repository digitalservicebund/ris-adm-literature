<script lang="ts" setup>
import { computed } from 'vue'
import { useEditableList } from '@/views/adm/documentUnit/[documentNumber]/useEditableList'
import AktivzitierungLiteratureItem from './AktivzitierungLiteratureItem.vue'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature.ts'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import SearchResults from '@/components/SearchResults.vue'
import AktivzitierungSearchResult from '@/views/literature/AktivzitierungSearchResult.vue'

const store = useSliDocumentUnitStore()

const aktivzitierungLiteratures = computed({
  get: () => store.documentUnit!.aktivzitierungenSli ?? [],
  set: (newValue: AktivzitierungLiterature[]) => {
    store.documentUnit!.aktivzitierungenSli = newValue
  },
})

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungLiteratures)

const searchResults = []
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungLiteraturesList">
    <ol v-if="aktivzitierungLiteratures.length > 0" aria-label="Aktivzitierung Liste">
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="aktivzitierungLiterature in aktivzitierungLiteratures"
        :key="aktivzitierungLiterature.id"
      >
        <AktivzitierungLiteratureItem
          :aktivzitierung-literature="aktivzitierungLiterature"
          @update-aktivzitierung-literature="onUpdateItem"
          @delete-aktivzitierung-literature="onRemoveItem"
        />
      </li>
    </ol>
    <AktivzitierungLiteratureInput
      v-if="isCreationPanelOpened || aktivzitierungLiteratures.length === 0"
      class="mt-16"
      @update-aktivzitierung-literature="onAddItem"
      @cancel="isCreationPanelOpened = false"
      :show-cancel-button="false"
    />
    <Button
      v-else
      class="mt-16"
      aria-label="Aktivzitierung hinzufügen"
      severity="secondary"
      label="Aktivzitierung hinzufügen"
      size="small"
      @click="isCreationPanelOpened = true"
    >
      <template #icon><IconAdd /></template>
    </Button>
    <SearchResults :search-results="searchResults" :is-loading="false">
      <template #default="{ searchResult }">
        <AktivzitierungSearchResult :search-result="searchResult" />
      </template>
    </SearchResults>
  </div>
</template>
