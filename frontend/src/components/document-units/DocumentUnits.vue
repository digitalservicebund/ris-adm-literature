<script lang="ts" setup>
import type { DocumentUnitListItem } from '@/domain/documentUnit'
import { ref } from 'vue'
import DocumentUnitList from './DocumentUnitList.vue'

const ITEMS_PER_PAGE = 100

const isLoading = ref(false)
const firstRowIndex = ref<number>(0)
const totalRows = ref<number>(0)
const docUnits = ref<DocumentUnitListItem[]>([])
</script>

<template>
  <DocumentUnitList
    :doc-units="docUnits"
    :first-row-index="firstRowIndex"
    :rows-per-page="ITEMS_PER_PAGE"
    :total-rows="totalRows"
    :loading="isLoading"
  />
</template>
