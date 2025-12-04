<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { useEditableList } from '@/views/adm/documentUnit/[documentNumber]/useEditableList'
import AktivzitierungLiteratureItem from './AktivzitierungLiteratureItem.vue'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature.ts'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import SearchResults from '@/components/SearchResults.vue'
import AktivzitierungSearchResult from '@/views/literature/AktivzitierungSearchResult.vue'
import type { SliDocUnitListItem, SliDocUnitSearchParams } from '@/domain/sli/sliDocumentUnit'
import { useToast, type PageState } from 'primevue'
import { usePagination } from '@/composables/usePagination'
import { useGetSliPaginatedDocUnits } from '@/services/literature/literatureDocumentUnitService'
import errorMessages from '@/i18n/errors.json'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'

const ITEMS_PER_PAGE = 15

const toast = useToast()
const store = useSliDocumentUnitStore()

const aktivzitierungLiteratures = computed({
  get: () => store.documentUnit!.aktivzitierungenSli ?? [],
  set: (newValue: AktivzitierungLiterature[]) => {
    store.documentUnit!.aktivzitierungenSli = newValue
  },
})

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungLiteratures)

const {
  firstRowIndex,
  totalRows,
  items: searchResults,
  fetchPaginatedData,
  isFetching,
  error,
} = usePagination<SliDocUnitListItem, SliDocUnitSearchParams>(
  useGetSliPaginatedDocUnits,
  ITEMS_PER_PAGE,
  'documentationUnitsOverview',
)

const searchParams = ref<SliDocUnitSearchParams>()
const showSearchResults = ref(false)

async function fetchData(page: number = 0) {
  await fetchPaginatedData(page, searchParams.value)
}

async function handlePageUpdate(pageState: PageState) {
  await fetchData(pageState.page)
}

async function onSearch(params: SliDocUnitSearchParams) {
  searchParams.value = params
  await fetchData(0)
  showSearchResults.value = true
}

watch(error, (err) => {
  if (err) {
    toast.add({
      severity: 'error',
      summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
    })
  }
})

const editingItemId = ref<string | undefined>(undefined)
function handleEditStart(itemId: string) {
  if (isCreationPanelOpened.value) {
    isCreationPanelOpened.value = false
  }
  editingItemId.value = itemId
}

function handleEditEnd() {
  editingItemId.value = undefined
}

function handleUpdateItem(item: AktivzitierungLiterature) {
  onUpdateItem(item)
  handleEditEnd()
}

function handleAddItem(item: AktivzitierungLiterature) {
  onAddItem(item)
  isCreationPanelOpened.value = true
}

function handleCancelEdit() {
  handleEditEnd()
}

const inputRef = ref<InstanceType<typeof AktivzitierungLiteratureInput>>()

function handleAddSearchResult(result: SliDocUnitListItem) {
  if (
    aktivzitierungLiteratures.value.some((entry) => entry.documentNumber === result.documentNumber)
  ) {
    return
  }

  const entry: AktivzitierungLiterature = {
    id: crypto.randomUUID(),
    uuid: result.id,
    titel: result.titel,
    documentNumber: result.documentNumber,
    veroeffentlichungsjahr: result.veroeffentlichungsjahr,
    verfasser: result.verfasser || [],
  }

  onAddItem(entry)
  isCreationPanelOpened.value = true
  showSearchResults.value = false
  searchParams.value = undefined
  inputRef.value?.clearSearchFields()
}
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungLiteraturesList">
    <ol
      v-if="aktivzitierungLiteratures.length > 0"
      aria-label="Aktivzitierung Liste"
      class="border-t-1 border-blue-300"
    >
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="aktivzitierungLiterature in aktivzitierungLiteratures"
        :key="aktivzitierungLiterature.id"
      >
        <AktivzitierungLiteratureItem
          :aktivzitierung-literature="aktivzitierungLiterature"
          :is-editing="editingItemId === aktivzitierungLiterature.id"
          @update-aktivzitierung-literature="handleUpdateItem"
          @edit-start="handleEditStart(aktivzitierungLiterature.id)"
          @cancel-edit="handleCancelEdit"
          @delete-aktivzitierung-literature="onRemoveItem"
        />
      </li>
    </ol>
    <AktivzitierungLiteratureInput
      ref="inputRef"
      v-if="isCreationPanelOpened || aktivzitierungLiteratures.length === 0"
      class="mt-16"
      @update-aktivzitierung-literature="handleAddItem"
      @cancel="isCreationPanelOpened = false"
      @search="onSearch"
      :show-cancel-button="false"
    />
    <Button
      v-else-if="!editingItemId"
      class="mt-16"
      aria-label="Weitere Angabe"
      severity="secondary"
      label="Weitere Angabe"
      size="small"
      @click="isCreationPanelOpened = true"
    >
      <template #icon><IconAdd /></template>
    </Button>
    <div v-if="showSearchResults" class="bg-blue-200 p-16 mt-16">
      <SearchResults :search-results="searchResults" :is-loading="isFetching">
        <template #default="{ searchResult }">
          <AktivzitierungSearchResult :search-result="searchResult" @add="handleAddSearchResult" />
        </template>
      </SearchResults>
      <RisPaginator
        v-if="searchResults.length > 0"
        :first="firstRowIndex"
        :rows="ITEMS_PER_PAGE"
        :total-records="totalRows"
        @page="handlePageUpdate"
        :is-loading="isFetching"
      />
    </div>
  </div>
</template>
