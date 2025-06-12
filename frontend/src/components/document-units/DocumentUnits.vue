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

onMounted(async () => {
  isLoading.value = true
  try {
    await fetchPaginatedData()
  } finally {
    isLoading.value = false
  }
})

async function handlePageUpdate(pageState: PageState) {
  docUnits.value = []
  isLoading.value = true
  try {
    await fetchPaginatedData(pageState.page)
  } finally {
    isLoading.value = false
  }
}

async function handleSearch(search: DocumentUnitSearchParams) {
  docUnits.value = []
  isLoading.value = true
  try {
    await fetchPaginatedData(0, search)
  } finally {
    isLoading.value = false
  }
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
