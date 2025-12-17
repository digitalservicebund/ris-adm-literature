<script setup lang="ts">
import { computed } from 'vue'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import type { AdmDocUnitListItem } from '@/domain/adm/admDocumentUnit'
import { parseIsoDateToLocal } from '@/utils/dateHelpers'

type AdmSearchResult = AdmDocUnitListItem &
  Partial<{
    inkrafttretedatum: string
    normgeber: string
    dokumenttyp: string
    aktenzeichen: string
    periodikum: string
    zitatstelle: string
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

const line1 = computed(() => {
  const parts: string[] = []

  // Normgeber
  const normgeber = props.searchResult.normgeber
  if (normgeber) parts.push(normgeber)

  // Datum des Inkrafttretens
  const formattedDate = props.searchResult.inkrafttretedatum
    ? parseIsoDateToLocal(props.searchResult.inkrafttretedatum)
    : null
  if (formattedDate) parts.push(formattedDate)

  // Aktenzeichen
  const aktenzeichen = props.searchResult.aktenzeichen
  if (aktenzeichen) parts.push(aktenzeichen)

  // Fundstelle = Periodikum + Zitatstelle
  const periodikum = props.searchResult.periodikum
  const zitatstelle = props.searchResult.zitatstelle
  const fundstelle =
    periodikum && zitatstelle ? `${periodikum} ${zitatstelle}` : periodikum || zitatstelle || null

  // Dokumenttyp
  const dokumenttyp = props.searchResult.dokumenttyp

  if (fundstelle && dokumenttyp) {
    parts.push(`${fundstelle} (${dokumenttyp})`)
  } else if (fundstelle) {
    parts.push(fundstelle)
  } else if (dokumenttyp) {
    parts.push(`(${dokumenttyp})`)
  }

  return parts.join(', ')
})

const line2 = computed(() => props.searchResult.langueberschrift || 'unbekannt')
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
        <p class="ris-body1-regular">{{ line1 || '—' }} | {{ searchResult.documentNumber }}</p>
        <div
          v-if="isAdded"
          class="ris-label2-regular flex w-[fit-content] items-center rounded-full px-4 py-2 bg-yellow-300 ml-1 shrink-0"
        >
          <span class="mx-2 text-yellow-900">Bereits hinzugefügt</span>
        </div>
      </div>

      <p class="ris-body2-regular text-gray-900">
        {{ line2 }}
      </p>
    </div>
  </div>
</template>
