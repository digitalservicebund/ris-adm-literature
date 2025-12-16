<script setup lang="ts">
import { computed } from 'vue'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import type { AdmDocUnitListItem } from '@/domain/adm/admDocumentUnit'
import { useStatusBadge } from '@/composables/useStatusBadge'
import type { PublicationStatus } from '@/domain/publicationStatus'
import { parseIsoDateToLocal } from '@/utils/dateHelpers'

type AdmSearchResult = AdmDocUnitListItem &
  Partial<{
    inkrafttretedatum: string
    normgeber: string
    dokumenttyp: string
    status: PublicationStatus
  }>

const props = defineProps<{
  searchResult: AdmSearchResult
  isAdded: boolean
}>()

const emit = defineEmits<{
  add: [searchResult: AdmSearchResult]
}>()

function handleAdd() {
  if (!props.isAdded) emit('add', props.searchResult)
}

const statusBadge = computed(() => {
  return props.searchResult.status ? useStatusBadge(props.searchResult.status).value : undefined
})

const heading = computed(() => {
  const parts: string[] = []

  const formatted = props.searchResult.inkrafttretedatum
    ? parseIsoDateToLocal(props.searchResult.inkrafttretedatum)
    : null
  if (formatted) parts.push(formatted)

  const normgeber = props.searchResult.normgeber
  const dokTyp = props.searchResult.dokumenttyp
  if (normgeber && dokTyp) parts.push(`${normgeber} (${dokTyp})`)
  else if (normgeber) parts.push(normgeber)
  else if (dokTyp) parts.push(`(${dokTyp})`)

  return parts.join(', ')
})
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

    <div class="flex flex-col w-full">
      <div class="flex flex-row items-center gap-12 flex-wrap">
        <p class="ris-body1-regular">{{ heading || '—' }} | {{ searchResult.documentNumber }}</p>

        <span
          v-if="statusBadge?.label"
          class="rounded-full px-10 py-4"
          :class="statusBadge.backgroundColor"
        >
          {{ statusBadge.label }}
        </span>

        <div
          v-if="isAdded"
          class="ris-label2-regular flex w-[fit-content] items-center rounded-full px-4 py-2 bg-yellow-300 ml-1 shrink-0"
        >
          <span class="mx-2 text-yellow-900">Bereits hinzugefügt</span>
        </div>
      </div>

      <p class="ris-body2-regular text-gray-900">
        {{ searchResult.langueberschrift || 'unbekannt' }}
      </p>
    </div>
  </div>
</template>
