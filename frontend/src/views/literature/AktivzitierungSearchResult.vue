<script setup lang="ts">
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'

const props = defineProps<{
  searchResult: SliDocUnitListItem
}>()

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
  <div class="search-result">
    <p class="ris-body1-regular">
      {{ formatHeading() }}
    </p>
    <p class="ris-body2-regular text-gray-900">
      {{ titel || 'unbekannt' }}
    </p>
  </div>
</template>
