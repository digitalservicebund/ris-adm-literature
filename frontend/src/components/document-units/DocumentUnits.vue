<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
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
} = usePagination<DocumentUnitListItem>(documentUnitService.getPaginatedDocumentUnitList)

onMounted(async () => {
  await fetchPaginatedData()
})

function handlePageUpdate(pageState: PageState) {
  fetchPaginatedData(pageState.page)
}

function handleSearch() {
  // fetch filtered results from BE
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
