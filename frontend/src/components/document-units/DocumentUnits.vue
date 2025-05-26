<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import { onMounted, ref } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'
import documentUnitService from '@/services/documentUnitService'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'

const ITEMS_PER_PAGE = 100

const isLoading = ref(true)
const firstRowIndex = ref<number>(0)
const totalRows = ref<number>(0)
const docUnits = ref<DocumentUnitListItem[]>([])

onMounted(async () => {
  const response = await documentUnitService.getPaginatedDocumentUnitList()
  docUnits.value = response.data?.documentationUnitsOverview || []
  totalRows.value = response.data?.page.numberOfElements || 0
  firstRowIndex.value = response.data?.page.number || 0
  isLoading.value = false
})
</script>

<template>
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
    :loading="isLoading"
  />
  <RisPaginator
    :first="firstRowIndex"
    :rows="ITEMS_PER_PAGE"
    :total-records="totalRows"
  />
</template>
