<script setup lang="ts">
import { RisAutoCompleteMultiple } from "@digitalservicebund/ris-ui/components";
import { computed, onMounted, ref } from "vue";
import {
  useAutoComplete,
  useDokumentTypSearch,
  type AutoCompleteSuggestion,
} from "@/composables/useAutoComplete";
import { useFetchDocumentTypes } from "@/services/documentTypeService";
import type { DocumentCategory, DocumentType } from "@/domain/documentType";
import type { AutoCompleteDropdownClickEvent } from "primevue";

const props = defineProps<{
  inputId: string;
  invalid: boolean;
  ariaLabel?: string;
  placeholder?: string;
  documentCategory: DocumentCategory;
}>();

const model = defineModel<DocumentType[]>();

const selectedItems = computed<AutoCompleteSuggestion[]>({
  get: () =>
    model.value?.map((docType) => ({
      id: docType.abbreviation,
      label: docType.abbreviation,
      secondaryLabel: docType.name,
    })) ?? [],
  set: (selectedItems) => {
    model.value = selectedItems.map((item) => ({
      uuid: item.id,
      abbreviation: item.label,
      name: item.secondaryLabel,
    })) as DocumentType[];
  },
});

const autoCompleteMultipleRef = ref<typeof RisAutoCompleteMultiple | null>(null);
const documentTypeOptions = ref<DocumentType[]>([]);
const autoOptionFocus = ref(false);

const searchFn = useDokumentTypSearch(documentTypeOptions);

const { suggestions, onComplete } = useAutoComplete(searchFn);

onMounted(async () => {
  const { data } = await useFetchDocumentTypes(props.documentCategory);
  documentTypeOptions.value = data.value?.documentTypes || [];
});

function openOverlay() {
  onComplete({ query: undefined });
  if (autoCompleteMultipleRef?.value?.autoCompleteRef) {
    autoCompleteMultipleRef.value.autoCompleteRef.show();
  }
}

function handleComplete(event: AutoCompleteDropdownClickEvent) {
  onComplete(event);
  autoOptionFocus.value = true;
}

function unsetAutoOptionFocus() {
  autoOptionFocus.value = false;
}
</script>

<template>
  <RisAutoCompleteMultiple
    ref="autoCompleteMultipleRef"
    :data-testid="'document-type-autocomplete'"
    v-model="selectedItems"
    :suggestions="suggestions"
    :input-id="inputId"
    :aria-label="ariaLabel"
    append-to="self"
    :placeholder="placeholder"
    typeahead
    dropdown
    fluid
    force-selection
    disable-dropdown-tab-navigation
    :auto-option-focus="autoOptionFocus"
    @complete="handleComplete"
    @focus="openOverlay"
    @hide="unsetAutoOptionFocus"
    @dropdown-click="unsetAutoOptionFocus"
  />
</template>
