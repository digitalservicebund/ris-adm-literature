<script setup lang="ts">
import ProgressSpinner from 'primevue/progressspinner'
import errorMessages from '@/i18n/errors.json'
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'
import { ref, watch } from 'vue'
import { useScrollToElement } from '@/composables/useScroll'

const props = defineProps<{
  searchResults: SliDocUnitListItem[]
  isLoading: boolean
}>()

const resultsContainer = ref<HTMLElement | null>(null)

watch(
  () => props.searchResults,
  (newResults, oldResults) => {
    if (newResults !== oldResults && newResults?.length > 0) {
      useScrollToElement(resultsContainer)
    }
  },
)
</script>

<template>
  <div ref="resultsContainer" class="mb-20">
    <div v-if="isLoading" class="grid justify-items-center">
      <ProgressSpinner />
    </div>

    <div v-else-if="searchResults.length === 0" class="grid justify-items-center">
      <p>{{ errorMessages.SEARCH_RESULTS_NOT_FOUND.title }}</p>
    </div>

    <div v-else-if="searchResults.length > 0">
      <p class="ris-label1-bold mb-20">Passende Suchergebnisse:</p>
      <ul class="flex flex-col gap-10" aria-label="Passende Suchergebnisse">
        <li v-for="searchResult in searchResults" :key="searchResult.id">
          <slot :searchResult="searchResult"></slot>
        </li>
      </ul>
    </div>
  </div>
</template>
