<!-- eslint-disable vue/multi-word-component-names -->
<script
  lang="ts"
  setup
  generic="
    T extends { id: string; documentNumber?: string },
    R extends { id: string; documentNumber: string }
  "
>
import { computed, ref, watch, type Ref, type VNodeChild } from 'vue'
import { useEditableList } from '@/views/adm/documentUnit/[documentNumber]/useEditableList'
import AktivzitierungInput from './AktivzitierungInput.vue'
import AktivzitierungItem from './AktivzitierungItem.vue'
import IconAdd from '~icons/material-symbols/add'
import { Button, useToast, type PageState } from 'primevue'
import type { UseFetchReturn } from '@vueuse/core'
import { usePagination } from '@/composables/usePagination'
import SearchResults from '../SearchResults.vue'
import { RisPaginator } from '@digitalservicebund/ris-ui/components'
import errorMessages from '@/i18n/errors.json'

const ITEMS_PER_PAGE = 15

const toast = useToast()

const props = defineProps<{
  fetchResultsFn: (page: Ref<number>, itemsPerPage: number, searchParams: Ref) => UseFetchReturn<R>
}>()

const aktivzitierungList = defineModel<T[]>({ default: () => [] })
defineSlots<{
  // 1. Slot for rendering the READ-ONLY list item
  item(props: { aktivzitierung: T }): VNodeChild
  // 2. Slot for rendering the EDITABLE INPUT form (uses v-model structure)
  input(props: { modelValue: T; onUpdateModelValue: (value: T) => void }): VNodeChild
  // 3. Slot for rendering the search result list item
  searchResult(props: { searchResult: R; isAdded: boolean; onAdd: (value: R) => void }): VNodeChild
}>()

const {
  firstRowIndex,
  totalRows,
  items: searchResults,
  fetchPaginatedData,
  isFetching,
  error,
} = usePagination(props.fetchResultsFn, ITEMS_PER_PAGE, 'documentationUnitsOverview') as {
  firstRowIndex: Ref<number>
  totalRows: Ref<number>
  items: Ref<R[]>
  fetchPaginatedData: (page: number, params: Ref) => Promise<void>
  isFetching: Ref<boolean>
  error: Ref
}

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungList)

const editingItemId = ref<string | undefined>(undefined)
const showSearchResults = ref(false)
const searchParams = ref()
const inputRef = ref<{ clearSearchFields: () => void }>()

function handleEditStart(itemId: string) {
  if (isCreationPanelOpened.value) {
    isCreationPanelOpened.value = false
  }
  editingItemId.value = itemId
}

function handleEditEnd() {
  editingItemId.value = undefined
}

function handleUpdateItem(item: T) {
  onUpdateItem(item)
  handleEditEnd()
}

function handleAddItem(item: T) {
  onAddItem(item)
  isCreationPanelOpened.value = true
}

function handleCancelEdit() {
  handleEditEnd()
}

async function fetchData(page: number = 0) {
  await fetchPaginatedData(page, searchParams.value)
}

async function handlePageUpdate(pageState: PageState) {
  await fetchData(pageState.page)
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function handleSearch(params: any) {
  searchParams.value = params
  fetchData(0)
  showSearchResults.value = true
}

const addedDocumentNumbers = computed(() => {
  return new Set(
    aktivzitierungList.value
      .map((entry) => entry.documentNumber)
      .filter((num): num is string => !!num),
  )
})

function handleAddSearchResult(result: R) {
  if (aktivzitierungList.value.some((entry) => entry.documentNumber === result.documentNumber)) {
    return
  }

  // Convert dokumenttypen from (names) to abbreviations
  // const dokumenttypen = result.dokumenttypen?.map((name) => {
  //   const abbreviation = getNameToAbbreviation(name)
  //   return {
  //     abbreviation,
  //     name,
  //   }
  // })

  const entry = {
    ...result,
    id: crypto.randomUUID(),
  }

  onAddItem(entry as unknown as T)
  isCreationPanelOpened.value = true
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
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungAdmList">
    <ol
      v-if="aktivzitierungList.length > 0"
      aria-label="Aktivzitierung Liste"
      class="border-t-1 border-blue-300"
    >
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="aktivzitierung in aktivzitierungList"
        :key="aktivzitierung.id"
      >
        <AktivzitierungItem
          :aktivzitierung="aktivzitierung"
          :is-editing="editingItemId === aktivzitierung.id"
          @update="handleUpdateItem"
          @edit-start="handleEditStart(aktivzitierung.id)"
          @cancel-edit="handleCancelEdit"
          @delete="onRemoveItem"
          @search="handleSearch"
        >
          <template #item="{ aktivzitierung }">
            <slot name="item" :aktivzitierung="aktivzitierung"></slot>
          </template>

          <template #input="{ modelValue, onUpdateModelValue }">
            <slot
              name="input"
              :modelValue="modelValue"
              :onUpdateModelValue="onUpdateModelValue"
            ></slot>
          </template>
        </AktivzitierungItem>
      </li>
    </ol>
    <AktivzitierungInput
      ref="inputRef"
      v-if="isCreationPanelOpened || aktivzitierungList.length === 0"
      class="mt-16"
      @update="handleAddItem"
      @cancel="isCreationPanelOpened = false"
      @search="handleSearch"
      :show-cancel-button="false"
    >
      <template #default="{ modelValue, onUpdateModelValue }">
        <slot
          name="input"
          :modelValue="modelValue as T"
          :onUpdateModelValue="onUpdateModelValue"
        ></slot>
      </template>
    </AktivzitierungInput>
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
          <slot
            name="searchResult"
            :searchResult="searchResult"
            :isAdded="addedDocumentNumbers.has(searchResult.documentNumber)"
            @add="handleAddSearchResult"
          ></slot>
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
