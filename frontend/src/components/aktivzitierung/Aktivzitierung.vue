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

/** ------------------------------------------------------------------
 * Props / v-model / slots
 * ------------------------------------------------------------------ */
const props = defineProps<{
  fetchResultsFn: (
    page: Ref<number>,
    itemsPerPage: number,
    searchParams: Ref<unknown>,
  ) => UseFetchReturn<R>
  transformResultFn?: (result: R) => T
}>()

const aktivzitierungList = defineModel<T[]>({ default: () => [] })

defineSlots<{
  item(props: { aktivzitierung: T }): VNodeChild
  input(props: { modelValue: T; onUpdateModelValue: (value: T) => void }): VNodeChild
  searchResult(props: { searchResult: R; isAdded: boolean; onAdd: (value: R) => void }): VNodeChild
}>()

/** ------------------------------------------------------------------
 * Composables
 * ------------------------------------------------------------------ */
const toast = useToast()

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
  fetchPaginatedData: (page: number, params?: unknown) => Promise<void>
  isFetching: Ref<boolean>
  error: Ref<unknown>
}

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungList)

/** ------------------------------------------------------------------
 * Local state
 * ------------------------------------------------------------------ */
const editingItemId = ref<string | null>(null)
const showSearchResults = ref(false)
const searchParams = ref<unknown>()
const citationTypeFromSearch = ref<string | undefined>()
const inputRef = ref<{ clearSearchFields: () => void } | null>(null)

/** ------------------------------------------------------------------
 * Computed
 * ------------------------------------------------------------------ */
const addedDocumentNumbers = computed(() => {
  return new Set(aktivzitierungList.value.map((entry) => entry.documentNumber).filter(Boolean))
})

const isEditing = computed(() => editingItemId.value !== null)

/** ------------------------------------------------------------------
 * Handlers
 * ------------------------------------------------------------------ */
function startEditing(itemId: string) {
  isCreationPanelOpened.value = false
  editingItemId.value = itemId
}

function stopEditing() {
  editingItemId.value = null
}

function removeDocumentNumber(item: T): T {
  // Remove documentNumber from manual entries (as only the search results should have it)
  const itemObj = item as Record<string, unknown>
  return Object.fromEntries(
    Object.entries(itemObj).filter(([key]) => key !== 'documentNumber'),
  ) as T
}

function updateItem(item: T) {
  // Remove the documentNumber if this is a manual entry
  const cleanedItem = removeDocumentNumber(item)
  onUpdateItem(cleanedItem)
  stopEditing()
}

function addItem(item: T) {
  // Remove documentNumber from manual entries (only search results should have it)
  const cleanedItem = removeDocumentNumber(item)
  onAddItem(cleanedItem)
  isCreationPanelOpened.value = true
}

async function fetchData(page = 0) {
  await fetchPaginatedData(page, searchParams.value)
}

async function onPageUpdate(pageState: PageState) {
  await fetchData(pageState.page)
}

function onSearch(params: unknown) {
  const paramsObj = params as Record<string, unknown> | undefined
  const citationTypeValue = paramsObj?.citationType as string | undefined

  citationTypeFromSearch.value =
    citationTypeValue && citationTypeValue.trim() !== '' ? citationTypeValue.trim() : undefined

  searchParams.value = params
  showSearchResults.value = true
  fetchData(0)
}

function addSearchResult(result: R) {
  if (addedDocumentNumbers.value.has(result.documentNumber)) return

  const baseEntry: T = props.transformResultFn
    ? props.transformResultFn(result)
    : (result as unknown as T)

  const citationTypeValue =
    citationTypeFromSearch.value ||
    ((searchParams.value as Record<string, unknown> | undefined)?.citationType as
      | string
      | undefined)

  const baseEntryObj = baseEntry as Record<string, unknown>

  const entryObj: Record<string, unknown> = {
    ...baseEntryObj,
    id: crypto.randomUUID(),
    ...(citationTypeValue && citationTypeValue.trim() !== ''
      ? { citationType: citationTypeValue.trim() }
      : {}),
  }

  const entry: T = entryObj as T

  onAddItem(entry)
  isCreationPanelOpened.value = true
  showSearchResults.value = false
  searchParams.value = undefined
  citationTypeFromSearch.value = undefined
  inputRef.value?.clearSearchFields()
}

/** ------------------------------------------------------------------
 * Side effects
 * ------------------------------------------------------------------ */
watch(error, (err) => {
  if (!err) return

  toast.add({
    severity: 'error',
    summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
  })
})
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungAdmList">
    <ol
      v-if="aktivzitierungList.length"
      aria-label="Aktivzitierung Liste"
      class="border-t-1 border-blue-300"
    >
      <li
        v-for="aktivzitierung in aktivzitierungList"
        :key="aktivzitierung.id"
        class="border-b-1 border-blue-300 py-16"
      >
        <AktivzitierungItem
          :aktivzitierung="aktivzitierung"
          :is-editing="editingItemId === aktivzitierung.id"
          @update="updateItem"
          @edit-start="startEditing(aktivzitierung.id)"
          @cancel-edit="stopEditing"
          @delete="onRemoveItem"
          @search="onSearch"
        >
          <template #item="slotProps">
            <slot name="item" v-bind="slotProps" />
          </template>

          <template #input="slotProps">
            <slot name="input" v-bind="slotProps" />
          </template>
        </AktivzitierungItem>
      </li>
    </ol>
    <AktivzitierungInput
      v-if="isCreationPanelOpened || !aktivzitierungList.length"
      ref="inputRef"
      class="mt-16"
      :show-cancel-button="false"
      @update="addItem"
      @cancel="isCreationPanelOpened = false"
      @search="onSearch"
    >
      <template #default="{ modelValue, onUpdateModelValue }">
        <slot
          name="input"
          :modelValue="modelValue as T"
          :onUpdateModelValue="onUpdateModelValue"
        ></slot>
      </template>
    </AktivzitierungInput>

    <!-- Add button -->
    <Button
      v-else-if="!isEditing"
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
            :onAdd="addSearchResult"
          />
        </template>
      </SearchResults>

      <RisPaginator
        v-if="searchResults.length"
        class="mt-20"
        :first="firstRowIndex"
        :rows="ITEMS_PER_PAGE"
        :total-records="totalRows"
        :is-loading="isFetching"
        @page="onPageUpdate"
      />
    </div>
  </div>
</template>
