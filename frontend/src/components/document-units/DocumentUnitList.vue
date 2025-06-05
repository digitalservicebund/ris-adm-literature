<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
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

const zitierdatenLabel = (zitierdaten: string[]) =>
  zitierdaten.length > 0
    ? zitierdaten.map((zitierdatum) => dayjs(zitierdatum).format('DD.MM.YYYY')).join(', ')
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
      thead: {
        style: 'box-shadow: inset 0 -2px #DCE8EF;',
        class: 'sticky top-0 z-1 bg-white text-gray-900',
      },
      bodyRow: ({ context: { index } }) => ({ 'data-testid': `row-${index}` }),
    }"
    scrollable
    scroll-height="400px"
  >
    <Column field="documentNumber" header="Dokumentnummer"></Column>
    <Column field="zitierdatum" header="Zitierdatum">
      <template #body="{ data }">
        {{ zitierdatenLabel(data.zitierdaten) }}
      </template>
    </Column>
    <Column field="langueberschrift" header="Amtl. Langueberschrift" />
    <Column field="sources" header="Fundstelle">
      <template #body="{ data }">
        <ul v-for="(fundstelle, index) in data.fundstellen" :key="index">
          <li>{{ fundstelle }}</li>
        </ul>
        <template v-if="data.fundstellen.length === 0"> -- </template>
      </template>
    </Column>
    <Column field="documentNumber" class="flex items-center justify-end">
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
