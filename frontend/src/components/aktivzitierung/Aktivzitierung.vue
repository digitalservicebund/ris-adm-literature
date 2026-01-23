<script
  lang="ts"
  setup
  generic="
    T extends { id: string; documentNumber?: string },
    R extends { id: string; documentNumber: string },
    SP extends Record<string, unknown>
  "
>
import { computed, ref, watch, type Ref, type VNodeChild } from "vue";
import { useEditableList } from "@/views/adm/documentUnit/[documentNumber]/useEditableList";
import AktivzitierungItem from "./AktivzitierungItem.vue";
import IconAdd from "~icons/material-symbols/add";
import { Button, useToast, type PageState } from "primevue";
import type { UseFetchReturn } from "@vueuse/core";
import { usePagination } from "@/composables/usePagination";
import SearchResults from "../SearchResults.vue";
import { RisPaginator } from "@digitalservicebund/ris-ui/components";
import errorMessages from "@/i18n/errors.json";
import { useCitationTypeRequirement } from "@/composables/useCitationaTypeRequirement";

const { currentCitationType, markMissingAndScroll } = useCitationTypeRequirement();
const ITEMS_PER_PAGE = 15;

/** ------------------------------------------------------------------
 * Props / v-model / slots
 * ------------------------------------------------------------------ */
const props = defineProps<{
  fetchResultsFn: (
    page: Ref<number>,
    itemsPerPage: number,
    searchParams: Ref<SP | undefined>,
  ) => UseFetchReturn<R>;
  requireCitationType?: boolean;
}>();

const aktivzitierungList = defineModel<T[]>({ default: () => [] });

defineSlots<{
  // 1. Slot for rendering the READ-ONLY list item
  item(props: { aktivzitierung: T }): VNodeChild;
  // 2. Slot for rendering the EDITABLE INPUT form (uses v-model structure)
  input(props: {
    aktivzitierung?: T;
    showCancelButton: boolean;
    showDeleteButton: boolean;
    showSearchButton: boolean;
    onSave: (value: T) => void;
    onDelete?: (id: string) => void;
    onCancel?: () => void;
    onSearch?: (value: SP) => void;
  }): VNodeChild;
  // 2. Slot for rendering the search result in the search results list
  searchResult(props: { searchResult: R; isAdded: boolean; onAdd: (value: R) => void }): VNodeChild;
}>();

/** ------------------------------------------------------------------
 * Composables
 * ------------------------------------------------------------------ */
const toast = useToast();

const {
  firstRowIndex,
  totalRows,
  items: searchResults,
  fetchPaginatedData,
  isFetching,
  error,
} = usePagination(props.fetchResultsFn, ITEMS_PER_PAGE, "documentationUnitsOverview") as {
  firstRowIndex: Ref<number>;
  totalRows: Ref<number>;
  items: Ref<R[]>;
  fetchPaginatedData: (page: number, params?: SP) => Promise<void>;
  isFetching: Ref<boolean>;
  error: Ref<unknown>;
};

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } =
  useEditableList(aktivzitierungList);

/** ------------------------------------------------------------------
 * Local state
 * ------------------------------------------------------------------ */
const editingItemId = ref<string | null>(null);
const showSearchResults = ref(false);
const searchParams = ref<SP>();
const citationTypeFromSearch = ref<string | undefined>();
const creationPanelKey = ref(0);

/** ------------------------------------------------------------------
 * Computed
 * ------------------------------------------------------------------ */
const addedEntries = computed(() => {
  return new Set(
    aktivzitierungList.value
      .map((entry) => {
        const docNum = entry.documentNumber;
        if (!docNum) return null;

        if (props.requireCitationType) {
          const citationType = (entry as Record<string, unknown>).citationType as
            | string
            | undefined;
          return citationType?.trim() ? `${docNum}|${citationType.trim()}` : docNum; // Fallback to documentNumber if citationType missing
        }

        return docNum;
      })
      .filter(Boolean),
  );
});

const isEditing = computed(() => editingItemId.value !== null);

/** ------------------------------------------------------------------
 * Handlers
 * ------------------------------------------------------------------ */
function startEditing(itemId: string) {
  isCreationPanelOpened.value = false;
  editingItemId.value = itemId;
}

function stopEditing() {
  editingItemId.value = null;
}

function removeDocumentNumber(item: T): T {
  // Remove documentNumber from manual entries (as only the search results should have it)
  const itemObj = item as Record<string, unknown>;
  return Object.fromEntries(
    Object.entries(itemObj).filter(([key]) => key !== "documentNumber"),
  ) as T;
}

function updateItem(item: T) {
  // Remove the documentNumber if this is a manual entry
  const cleanedItem = removeDocumentNumber(item);
  onUpdateItem(cleanedItem);
  stopEditing();
}

function addManualEntry(item: T) {
  // Remove documentNumber from manual entries (only search results should have it)
  const cleanedItem = removeDocumentNumber(item);
  onAddItem(cleanedItem);
  resetCreationPanel();
}

async function fetchData(page = 0) {
  await fetchPaginatedData(page, searchParams.value);
}

async function onPageUpdate(pageState: PageState) {
  await fetchData(pageState.page);
}

function onSearch(params: SP) {
  // Safe check: does the current param type support citationType?
  if (params && "citationType" in params && typeof params.citationType === "string") {
    const trimmed = params.citationType.trim();
    citationTypeFromSearch.value = trimmed || undefined;
  } else {
    citationTypeFromSearch.value = undefined;
  }

  searchParams.value = params;
  showSearchResults.value = true;
  fetchData(0);
}

function addSearchResult(result: R) {
  const baseEntry: T = result as unknown as T;

  const citationTypeValue =
    currentCitationType.value ??
    citationTypeFromSearch.value ??
    ((searchParams.value as Record<string, unknown> | undefined)?.citationType as
      | string
      | undefined);

  if (props.requireCitationType && !citationTypeValue?.trim()) {
    markMissingAndScroll();
    return;
  }

  // Check if this exact combination already exists
  // If requireCitationType is true: use documentNumber|citationType (allows same doc with different types)
  // Otherwise: use just documentNumber (blocks all duplicates)
  let entryKey: string | null = null;
  if (result.documentNumber) {
    if (props.requireCitationType && citationTypeValue?.trim()) {
      // ADM section: allow same document with different citation types
      entryKey = `${result.documentNumber}|${citationTypeValue.trim()}`;
    } else {
      // SLI section or no citation type: block all duplicates
      entryKey = result.documentNumber;
    }
  }

  if (entryKey && addedEntries.value.has(entryKey)) {
    return;
  }

  const baseEntryObj = baseEntry as Record<string, unknown>;

  const entryObj: Record<string, unknown> = {
    ...baseEntryObj,
    id: crypto.randomUUID(),
    ...(citationTypeValue && citationTypeValue.trim() !== ""
      ? { citationType: citationTypeValue.trim() }
      : {}),
  };

  const entry: T = entryObj as T;

  onAddItem(entry);
  resetCreationPanel();
}

function resetCreationPanel() {
  isCreationPanelOpened.value = false;
  showSearchResults.value = false;
  searchParams.value = undefined;
  citationTypeFromSearch.value = undefined;
  creationPanelKey.value++;
}

function isSearchResultAdded(searchResult: R): boolean {
  if (!searchResult.documentNumber) return false;

  // If requireCitationType is true, check with citation type
  if (props.requireCitationType) {
    if (currentCitationType.value?.trim()) {
      const key = `${searchResult.documentNumber}|${currentCitationType.value.trim()}`;
      return addedEntries.value.has(key);
    }
    // If requireCitationType is true but no citation type selected yet,
    // check by documentNumber only (will be blocked when trying to add)
    return addedEntries.value.has(searchResult.documentNumber);
  }

  // Otherwise, check by documentNumber only (blocks all duplicates)
  return addedEntries.value.has(searchResult.documentNumber);
}

/** ------------------------------------------------------------------
 * Side effects
 * ------------------------------------------------------------------ */
watch(error, (err) => {
  if (err) {
    toast.add({
      severity: "error",
      summary: errorMessages.DOCUMENT_UNITS_COULD_NOT_BE_LOADED.title,
    });
  }
});
</script>

<template>
  <div aria-label="Aktivzitierung" class="aktivzitierungAdmList">
    <ol
      v-if="aktivzitierungList.length"
      aria-label="Aktivzitierung Liste"
      class="border-t-1 border-blue-300 mb-16"
    >
      <li
        v-for="aktivzitierung in aktivzitierungList"
        :key="aktivzitierung.id"
        class="border-b-1 border-blue-300 py-16"
      >
        <AktivzitierungItem
          :aktivzitierung="aktivzitierung"
          :is-editing="editingItemId === aktivzitierung.id"
          @save="updateItem"
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

    <div :key="creationPanelKey" v-if="isCreationPanelOpened || !aktivzitierungList.length">
      <slot
        name="input"
        :show-cancel-button="false"
        :show-delete-button="false"
        :show-search-button="true"
        :on-save="addManualEntry"
        :on-search="onSearch"
      />

      <div v-if="showSearchResults" class="bg-blue-200 p-16 mt-16">
        <SearchResults :search-results="searchResults" :is-loading="isFetching">
          <template #default="{ searchResult }">
            <slot
              name="searchResult"
              :searchResult="searchResult"
              :isAdded="isSearchResultAdded(searchResult)"
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

    <Button
      v-else-if="!isEditing"
      aria-label="Weitere Angabe"
      severity="secondary"
      label="Weitere Angabe"
      size="small"
      @click="isCreationPanelOpened = true"
    >
      <template #icon><IconAdd /></template>
    </Button>
  </div>
</template>
