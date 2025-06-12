<script lang="ts" setup>
import type { DocumentUnitListItem, DocumentUnitSearchParams } from '@/domain/documentUnit'
import { onMounted, ref } from 'vue'
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

const isLoading = ref(false)

async function fetchData(page: number = 0, search?: DocumentUnitSearchParams) {
  isLoading.value = true
  docUnits.value = []
  try {
    await fetchPaginatedData(page, search)
  } finally {
    isLoading.value = false
  }
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
