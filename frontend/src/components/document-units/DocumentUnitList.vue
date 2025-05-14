<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import type { Fundstelle, Periodikum } from '@/domain/fundstelle'
import dayjs from 'dayjs'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'

export interface DocumentUnitListProps {
  docUnits: DocumentUnitListItem[]
  firstRowIndex: number
  rowsPerPage: number
  totalRows: number
  loading: boolean
}
defineProps<DocumentUnitListProps>()

const fundstelleLabel = (sources: Fundstelle[]) =>
  sources.length > 0
    ? sources
        .map((f: Fundstelle) => {
          const abbreviations = f.periodika.map((p: Periodikum) => p.abbreviation).join(' ')
          return `${abbreviations} ${f.zitatstelle}`
        })
        .join(', ')
    : '--'
</script>

<template>
  <DataTable
    class="ris-label2-bold text-gray-900"
    :value="docUnits"
    :first="firstRowIndex"
    :rows="rowsPerPage"
    :total-records="totalRows"
    :loading="loading"
    :pt="{ thead: { style: 'box-shadow: inset 0 -2px #DCE8EF;' } }"
  >
    <Column field="documentNumber" header="Dokumentnummer"></Column>
    <Column field="zitierdatum" header="Zitierdatum">
      <template #body="{ data }">
        {{ data.zitierdatum ? dayjs(data.zitierdatum).format('DD.MM.YYYY') : '-' }}
      </template>
    </Column>
    <Column
      class="truncate whitespace-nowrap overflow-hidden w-full max-w-0"
      field="langueberschrift"
      header="Amtl. Langueberschrift"
    />
    <Column
      class="truncate whitespace-nowrap overflow-hidden w-full"
      field="sources"
      header="Fundstelle"
    >
      <template #body="{ data }">
        {{ fundstelleLabel(data.fundstellen) }}
      </template>
    </Column>
  </DataTable>
</template>
