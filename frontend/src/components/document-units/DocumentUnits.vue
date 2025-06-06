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
} = usePagination<DocumentUnitListItem>(
  documentUnitService.getPaginatedDocumentUnitList,
  'documentationUnitsOverview',
)

const filteredDocUnits = ref<DocumentUnitListItem[]>([])

onMounted(async () => {
  await fetchPaginatedData()
  filteredDocUnits.value = docUnits.value
})

function handlePageUpdate(pageState: PageState) {
  fetchPaginatedData(pageState.page)
  filteredDocUnits.value = docUnits.value
}

/**
 * Temporary Frontend solution. Will be replaced with a backend search once BE is ready.
 */
function handleSearch(search: DocumentUnitSearchParams) {
  let results = docUnits.value

  const hasSearchTerm = Object.values(search).some(
    (term) => term && typeof term === 'string' && term.trim() !== '',
  )
  if (hasSearchTerm) {
    results = docUnits.value.filter((doc) => {
      return (Object.keys(search) as Array<keyof DocumentUnitSearchParams>).every((key) => {
        const searchTerm = search[key]?.toLowerCase()
        if (!searchTerm) {
          return true
        }
        switch (key) {
          case 'documentNumber':
          case 'langueberschrift':
            return (doc[key] ?? '').toLowerCase().includes(searchTerm)
          case 'fundstelle':
            return doc.fundstellen?.some((item) => item.toLowerCase().includes(searchTerm))
          default:
            return true
        }
      })
    })
  }
  filteredDocUnits.value = results
}
</script>

<template>
  <SearchPanel @search="handleSearch" />
  <DocumentUnitList
    :doc-units="filteredDocUnits"
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
