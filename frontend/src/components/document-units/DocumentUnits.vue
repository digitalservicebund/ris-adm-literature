<script lang="ts" setup>
import type { AdmDocUnitListItem, AdmDocUnitSearchParams } from '@/domain/adm/admDocumentUnit'
import { watch } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'
import { useGetAdmPaginatedDocUnits } from '@/services/adm/admDocumentUnitService'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'
import { usePagination } from '@/composables/usePagination'
import { useToast, type PageState } from 'primevue'
import SearchPanel from './SearchPanel.vue'
import errorMessages from '@/i18n/errors.json'

const toast = useToast()
const ITEMS_PER_PAGE = 100

const {
  firstRowIndex,
  totalRows,
  items: docUnits,
  fetchPaginatedData,
  isFetching,
  error,
} = usePagination<AdmDocUnitListItem, AdmDocUnitSearchParams>(
  useGetAdmPaginatedDocUnits,
  ITEMS_PER_PAGE,
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

async function fetchData(page: number = 0, search?: AdmDocUnitSearchParams) {
  await fetchPaginatedData(page, search)
}

async function handlePageUpdate(pageState: PageState) {
  await fetchData(pageState.page)
}

async function handleSearch(search: AdmDocUnitSearchParams) {
  await fetchData(0, search)
}
</script>

<template>
  <SearchPanel :loading="isFetching" @search="handleSearch" />
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
    :loading="isFetching"
  />
  <RisPaginator
    v-if="docUnits.length > 0"
    :first="firstRowIndex"
    :rows="ITEMS_PER_PAGE"
    :total-records="totalRows"
    @page="handlePageUpdate"
    :is-loading="isFetching"
  />
</template>
