<script lang="ts" setup>
import type { DocumentUnitListItem, DocumentUnitSearchParams } from '@/domain/documentUnit'
import { onMounted, watch } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'
import documentUnitService from '@/services/documentUnitService'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'
import { usePagination } from '@/composables/usePagination'
import { useToast, type PageState } from 'primevue'
import SearchPanel from './SearchPanel.vue'
import errorMessages from '@/i18n/errors.json'

const toast = useToast()

const {
  firstRowIndex,
  totalRows,
  items: docUnits,
  ITEMS_PER_PAGE,
  fetchPaginatedData,
  isLoading,
  error,
} = usePagination<DocumentUnitListItem, DocumentUnitSearchParams>(
  documentUnitService.getPaginatedDocumentUnitList,
  'documentationUnitsOverview',
)

watch(error, (err) => {
  if (err) {
    toast.add({
      severity: 'error',
      summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
    })
  }
})

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
