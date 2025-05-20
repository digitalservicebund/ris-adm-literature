<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import type { Fundstelle } from '@/domain/fundstelle'
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

const fundstelleLabel = (fundstellen: Fundstelle[]) =>
  fundstellen.length > 0
    ? fundstellen.map((f: Fundstelle) => `${f.periodikum.abbreviation} ${f.zitatstelle}`).join(', ')
    : '--'
</script>

<template>
  <DataTable
    class="ris-label2-bold"
    :value="docUnits"
    :first="firstRowIndex"
    :rows="rowsPerPage"
    :total-records="totalRows"
    :loading="loading"
    :pt="{
      thead: { style: 'box-shadow: inset 0 -2px #DCE8EF;', class: 'text-gray-900' },
      bodyRow: ({ context: { index } }) => ({ 'data-testid': `row-${index}` }),
    }"
  >
    <Column field="documentNumber" header="Dokumentnummer"></Column>
    <Column field="zitierdatum" header="Zitierdatum">
      <template #body="{ data }">
        {{ data.zitierdatum ? dayjs(data.zitierdatum).format('DD.MM.YYYY') : '--' }}
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
