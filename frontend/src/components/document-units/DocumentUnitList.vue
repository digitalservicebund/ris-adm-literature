<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import type { Fundstelle } from '@/domain/fundstelle'
import dayjs from 'dayjs'
import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import IconEdit from '~icons/ic/outline-edit'

export interface DocumentUnitListProps {
  docUnits: DocumentUnitListItem[]
  firstRowIndex: number
  rowsPerPage: number
  totalRows: number
}
defineProps<DocumentUnitListProps>()

const fundstelleLabel = (fundstellen: Fundstelle[]) =>
  fundstellen.length > 0
    ? fundstellen.join(', ')
    : '--'
</script>

<template>
  <DataTable
    class="ris-label2-bold"
    :value="docUnits"
    :first="firstRowIndex"
    :rows="rowsPerPage"
    :total-records="totalRows"
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
    <Column field="langueberschrift" header="Amtl. Langueberschrift" />
    <Column field="sources" header="Fundstelle">
      <template #body="{ data }">
        {{ fundstelleLabel(data.fundstellen) }}
      </template>
    </Column>
    <Column field="documentNumber" class="flex justify-end">
      <template #body="{ data }">
        <router-link
          :to="{
            name: 'documentUnit-documentNumber',
            params: { documentNumber: data.documentNumber },
          }"
        >
          <Button
            :aria-label="`Dokument ${data.documentNumber} editieren`"
            severity="secondary"
            size="small"
          >
            <template #icon>
              <IconEdit />
            </template>
          </Button>
        </router-link>
      </template>
    </Column>
  </DataTable>
</template>
