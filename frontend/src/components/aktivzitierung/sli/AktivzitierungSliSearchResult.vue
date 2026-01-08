<script setup lang="ts">
import { computed } from 'vue'
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'

const props = defineProps<{
  searchResult: SliDocUnitListItem
  isAdded: boolean
  documentTypeNameToAbbreviation?: Map<string, string>
}>()

const emit = defineEmits<{
  add: [searchResult: SliDocUnitListItem]
}>()

function handleAdd() {
  if (!props.isAdded) {
    emit('add', props.searchResult)
  }
}

const { veroeffentlichungsjahr, verfasser, documentNumber, titel, dokumenttypen } =
  props.searchResult

const documentTypeAbbreviations = computed(() => {
  if (!dokumenttypen || dokumenttypen.length === 0 || !props.documentTypeNameToAbbreviation) {
    return dokumenttypen || []
  }
  return dokumenttypen
    .map((name) => props.documentTypeNameToAbbreviation?.get(name) || name)
    .filter(Boolean)
})

function formatHeading(): string {
  const parts = []

  if (veroeffentlichungsjahr) {
    parts.push(veroeffentlichungsjahr)
  }

  if (verfasser && verfasser.length > 0) {
    parts.push(verfasser.join(', '))
  }

  const citationPart = parts.join(', ')

  let result = citationPart
  if (documentTypeAbbreviations.value.length > 0) {
    const docTypes = documentTypeAbbreviations.value.join(', ')
    if (docTypes) {
      if (citationPart) {
        result = `${citationPart} (${docTypes})`
      } else {
        result = `(${docTypes})`
      }
    }
  }
  if (result) {
    return `${result} | ${documentNumber}`
  }

  return documentNumber
}
</script>

<template>
  <div class="search-result flex items-center w-full">
    <Button
      aria-label="Aktivzitierung hinzufügen"
      size="small"
      class="mr-16 shrink-0"
      :disabled="isAdded"
      @click="handleAdd"
    >
      <template #icon><IconAdd /></template>
    </Button>

    <div class="flex flex-col w-full">
      <div class="relative">
        <p class="ris-body1-regular inline-block relative pr-8">
          {{ formatHeading() }}

          <span
            v-if="isAdded"
            class="absolute top-1/2 -translate-y-1/2 ris-label2-regular rounded-full ml-10 px-6 py-2 bg-yellow-300 whitespace-nowrap"
            aria-label="Bereits hinzugefügt"
          >
            <span class="text-yellow-900">Bereits hinzugefügt</span>
          </span>
        </p>
      </div>

      <p class="ris-body2-regular text-gray-900">
        {{ titel || 'unbekannt' }}
      </p>
    </div>
  </div>
</template>
