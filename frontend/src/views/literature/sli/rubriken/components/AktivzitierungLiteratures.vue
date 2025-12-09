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
import { DocumentCategory } from '@/domain/documentType'
import { useFetchDocumentTypes } from '@/services/documentTypeService'

const ITEMS_PER_PAGE = 15

const toast = useToast()
const store = useSliDocumentUnitStore()

const ownDocumentNumber = computed(() => store.documentUnit?.documentNumber)

const aktivzitierungLiteratures = computed({
  get: () => store.documentUnit!.aktivzitierungenSli ?? [],
  set: (newValue: AktivzitierungLiterature[]) => {
    store.documentUnit!.aktivzitierungenSli = newValue
  },
})

const visibleSearchResults = computed(() => {
  if (!searchResults.value) return []
  return searchResults.value.filter((result) => result.documentNumber !== ownDocumentNumber.value)
})

const addedDocumentNumbers = computed(() => {
  return new Set(
    aktivzitierungLiteratures.value
      .map((entry) => entry.documentNumber)
      .filter((num): num is string => !!num),
  )
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

const { data: docTypes } = useFetchDocumentTypes(DocumentCategory.LITERATUR_SELBSTAENDIG)

const nameToAbbreviation = computed(() => {
  const mapping = new Map<string, string>()
  docTypes.value?.documentTypes?.forEach((docType) => {
    mapping.set(docType.name, docType.abbreviation)
  })
  return mapping
})

function getNameToAbbreviation(name: string): string {
  return nameToAbbreviation.value.get(name) || name
}

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

// make sure all entries have unique IDs when loaded and reset editing state
watch(
  () => store.documentUnit?.aktivzitierungenSli,
  (entries) => {
    if (entries) {
      editingItemId.value = undefined

      // Only process if there are entries missing IDs
      const needsIds = entries.some((entry) => !entry.id || entry.id === '')
      if (needsIds) {
        entries.forEach((entry) => {
          if (!entry.id || entry.id === '') {
            entry.id = crypto.randomUUID()
          }
        })
      }
    }
  },
  { immediate: true, deep: true },
)

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
  // Close search results when manually adding an entry
  showSearchResults.value = false
  searchParams.value = undefined
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

  // Convert dokumenttypen from (names) to abbreviations
  const dokumenttypen = result.dokumenttypen?.map((name) => {
    const abbreviation = getNameToAbbreviation(name)
    return {
      abbreviation,
      name,
    }
  })

  const entry: AktivzitierungLiterature = {
    id: crypto.randomUUID(),
    uuid: result.id,
    titel: result.titel,
    documentNumber: result.documentNumber,
    veroeffentlichungsJahr: result.veroeffentlichungsjahr,
    verfasser: result.verfasser || [],
    dokumenttypen: dokumenttypen || [],
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
      <SearchResults :search-results="visibleSearchResults" :is-loading="isFetching">
        <template #default="{ searchResult }">
          <AktivzitierungSearchResult
            :search-result="searchResult"
            :is-added="addedDocumentNumbers.has(searchResult.documentNumber)"
            :document-type-name-to-abbreviation="nameToAbbreviation"
            @add="handleAddSearchResult"
          />
        </template>
      </SearchResults>
      <RisPaginator
        v-if="searchResults.length > 0"
        class="mt-20"
        :first="firstRowIndex"
        :rows="ITEMS_PER_PAGE"
        :total-records="totalRows"
        @page="handlePageUpdate"
        :is-loading="isFetching"
      />
    </div>
  </div>
</template>
