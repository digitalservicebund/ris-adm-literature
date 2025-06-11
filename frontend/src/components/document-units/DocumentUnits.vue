<script lang="ts" setup>
import type { DocumentUnitListItem, DocumentUnitSearchParams } from '@/domain/documentUnit'
import { onMounted } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'
import documentUnitService from '@/services/documentUnitService'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'
import { usePagination } from '@/composables/usePagination'
import type { PageState } from 'primevue'
import SearchPanel from './SearchPanel.vue'

const {
  firstRowIndex,
  totalRows,
  items: docUnits,
  ITEMS_PER_PAGE,
  fetchPaginatedData,
} = usePagination<DocumentUnitListItem, DocumentUnitSearchParams>(
  documentUnitService.getPaginatedDocumentUnitList,
  'documentationUnitsOverview',
)

onMounted(async () => {
  await fetchPaginatedData()
})

function handlePageUpdate(pageState: PageState) {
  fetchPaginatedData(pageState.page)
}

function handleSearch(search: DocumentUnitSearchParams) {
  fetchPaginatedData(0, search)
}
</script>

<template>
  <SearchPanel @search="handleSearch" />
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
  />
  <RisPaginator
    v-if="docUnits.length > 0"
    :first="firstRowIndex"
    :rows="ITEMS_PER_PAGE"
    :total-records="totalRows"
    @page="handlePageUpdate"
  />
</template>
