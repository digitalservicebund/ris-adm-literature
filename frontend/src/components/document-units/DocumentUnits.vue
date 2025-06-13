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
  isLoading,
} = usePagination<DocumentUnitListItem, DocumentUnitSearchParams>(
  documentUnitService.getPaginatedDocumentUnitList,
  'documentationUnitsOverview',
)

async function fetchData(page: number = 0, search?: DocumentUnitSearchParams) {
  docUnits.value = []
  await fetchPaginatedData(page, search)
}

onMounted(() => {
  fetchData()
})

async function handlePageUpdate(pageState: PageState) {
  await fetchData(pageState.page)
}

async function handleSearch(search: DocumentUnitSearchParams) {
  await fetchData(0, search)
}
</script>

<template>
  <SearchPanel :loading="isLoading" @search="handleSearch" />
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
    :loading="isLoading"
  />
  <RisPaginator
    v-if="docUnits.length > 0"
    :first="firstRowIndex"
    :rows="ITEMS_PER_PAGE"
    :total-records="totalRows"
    @page="handlePageUpdate"
    :is-loading="isLoading"
  />
</template>
