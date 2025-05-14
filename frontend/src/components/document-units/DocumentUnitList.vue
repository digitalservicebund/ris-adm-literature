<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import DataTable from 'primevue/datatable'

export interface DocumentUnitListProps {
  docUnits: DocumentUnitListItem[]
  firstRowIndex: number
  rowsPerPage: number
  totalRows: number
  loading: boolean
}
defineProps<DocumentUnitListProps>()
</script>

<template>
  <DataTable
    :value="docUnits"
    :first="firstRowIndex"
    :rows="rowsPerPage"
    :total-records="totalRows"
    :loading="loading"
    tableStyle="min-width: 1300px"
    paginator
  >
  </DataTable>
</template>
