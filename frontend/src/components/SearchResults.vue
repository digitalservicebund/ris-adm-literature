<script setup lang="ts">
import ProgressSpinner from 'primevue/progressspinner'
import errorMessages from '@/i18n/errors.json'
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'

defineProps<{
  searchResults: SliDocUnitListItem[]
  isLoading: boolean
}>()
</script>

<template>
  <div class="bg-blue-200 p-16">
    <div v-if="isLoading" class="grid justify-items-center">
      <ProgressSpinner />
    </div>

    <div v-else-if="searchResults.length === 0" class="grid justify-items-center">
      <p>{{ errorMessages.SEARCH_RESULTS_NOT_FOUND.title }}</p>
    </div>

    <div v-else-if="searchResults.length > 0" class="">
      <p class="ris-label1-bold mb-20">Passende Suchergebnisse:</p>
      <ul class="flex flex-col gap-10" aria-label="Passende Suchergebnisse">
        <li v-for="searchResult in searchResults" :key="searchResult.id">
          <slot :searchResult="searchResult"></slot>
        </li>
      </ul>
    </div>
  </div>
</template>
