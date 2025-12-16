<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import type { AktivzitierungSli } from '@/domain/AktivzitierungSli'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'
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
import Aktivzitierung from '@/components/aktivzitierung/Aktivzitierung.vue'
import AktivzitierungSliItem from './AktivzitierungSliItem.vue'
import AktivzitierungSliInput from './AktivzitierungSliInput.vue'

const ITEMS_PER_PAGE = 15

const aktivzitierungSli = defineModel<AktivzitierungSli[]>({ default: () => [] })

const toast = useToast()
const store = useSliDocumentUnitStore()

const ownDocumentNumber = computed(() => store.documentUnit?.documentNumber)

const visibleSearchResults = computed(() => {
  if (!searchResults.value) return []
  return searchResults.value.filter((result) => result.documentNumber !== ownDocumentNumber.value)
})

const addedDocumentNumbers = computed(() => {
  return new Set(
    aktivzitierungSli.value
      .map((entry) => entry.documentNumber)
      .filter((num): num is string => !!num),
  )
})

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

const inputRef = ref<InstanceType<typeof AktivzitierungSliInput>>()

function handleSearch(params: SliDocUnitSearchParams) {
  searchParams.value = params
  fetchData(0)
  showSearchResults.value = true
}

// function handleAddItem(item: AktivzitierungSli) {
//   // Ensure ID exists
//   if (!item.id || item.id === '') {
//     item.id = crypto.randomUUID()
//   }
//   aktivzitierungSli.value = [...aktivzitierungSli.value, item]
//   // Close search results when manually adding an entry
//   showSearchResults.value = false
//   searchParams.value = undefined
// }

function handleAddSearchResult(result: SliDocUnitListItem) {
  if (aktivzitierungSli.value.some((entry) => entry.documentNumber === result.documentNumber)) {
    return
  }

  // Convert dokumenttypen from names to DocumentType objects with abbreviations
  const dokumenttypen = result.dokumenttypen?.map((name) => {
    const abbreviation = getNameToAbbreviation(name)
    return {
      abbreviation,
      name,
    }
  })

  const entry: AktivzitierungSli = {
    id: crypto.randomUUID(),
    uuid: result.id,
    titel: result.titel,
    documentNumber: result.documentNumber,
    veroeffentlichungsJahr: result.veroeffentlichungsjahr,
    verfasser: result.verfasser || [],
    dokumenttypen: dokumenttypen || [],
  }

  aktivzitierungSli.value = [...aktivzitierungSli.value, entry]
  showSearchResults.value = false
  searchParams.value = undefined
  inputRef.value?.clearSearchFields()
}

watch(error, (err) => {
  if (err) {
    toast.add({
      severity: 'error',
      summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
    })
  }
})

// Ensure all entries have unique IDs when loaded
watch(
  () => aktivzitierungSli.value,
  (entries) => {
    if (entries) {
      entries.forEach((entry) => {
        if (!entry.id || entry.id === '') {
          entry.id = crypto.randomUUID()
        }
      })
    }
  },
  { immediate: true, deep: true },
)
</script>

<template>
  <div>
    <Aktivzitierung v-model="aktivzitierungSli">
      <template #item="{ aktivzitierung }">
        <AktivzitierungSliItem :aktivzitierung="aktivzitierung" />
      </template>

      <template #input="{ modelValue, onUpdateModelValue }">
        <AktivzitierungSliInput
          ref="inputRef"
          :modelValue="modelValue"
          @update:modelValue="onUpdateModelValue"
          @search="handleSearch"
        />
      </template>
    </Aktivzitierung>

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
