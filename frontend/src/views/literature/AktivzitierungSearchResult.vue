<script setup lang="ts">
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'

const props = defineProps<{
  searchResult: SliDocUnitListItem
  isAdded: boolean
}>()

const emit = defineEmits<{
  add: [searchResult: SliDocUnitListItem]
}>()

function handleAdd() {
  if (!props.isAdded) {
    emit('add', props.searchResult)
  }
}

const { veroeffentlichungsjahr, verfasser, documentNumber, titel } = props.searchResult

function formatHeading(): string {
  const hasAuthor = verfasser && verfasser.length > 0

  const parts = []

  if (veroeffentlichungsjahr) {
    parts.push(veroeffentlichungsjahr)
  }

  if (hasAuthor) {
    parts.push(verfasser.join(', '))
  }

  const citationPart = parts.join(', ')

  if (citationPart) {
    return `${citationPart} | ${documentNumber}`
  }

  return documentNumber
}
</script>

<template>
  <div class="search-result flex flex-row items-start w-full">
    <Button
      aria-label="Aktivzitierung hinzufügen"
      size="small"
      class="mr-16"
      :disabled="isAdded"
      @click="handleAdd"
    >
      <template #icon><IconAdd /></template>
    </Button>

    <div class="flex flex-col">
      <p class="ris-body1-regular">
        {{ formatHeading() }}
      </p>
      <p class="ris-body2-regular text-gray-900">
        {{ titel || 'unbekannt' }}
      </p>
    </div>

    <div
      v-if="isAdded"
      class="ris-label2-regular flex w-[fit-content] items-center rounded-full px-4 py-2 bg-yellow-300"
    >
      <span class="mx-2 text-yellow-900">Bereits hinzugefügt</span>
    </div>
  </div>
</template>
