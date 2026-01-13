<script setup lang="ts">
import { RisAutoComplete } from "@digitalservicebund/ris-ui/components";
import { onMounted, ref } from "vue";
import { useAutoComplete, useDokumentTypSearch } from "@/composables/useAutoComplete";
import { useFetchDocumentTypes } from "@/services/documentTypeService";
import type { DocumentCategory, DocumentType } from "@/domain/documentType";

const props = defineProps<{
  inputId: string;
  isInvalid: boolean;
  ariaLabel?: string;
  placeholder?: string;
  documentCategory: DocumentCategory;
}>();

const model = defineModel<string | undefined>();

const documentTypeOptions = ref<DocumentType[]>([]);

const searchFn = useDokumentTypSearch(documentTypeOptions);

const { suggestions, onComplete, onDropdownClick, onItemSelect } = useAutoComplete(searchFn);

onMounted(async () => {
  const { data } = await useFetchDocumentTypes(props.documentCategory);
  documentTypeOptions.value = data.value?.documentTypes || [];
});
</script>

<template>
  <RisAutoComplete
    v-model="model"
    :suggestions="suggestions"
    :invalid="isInvalid"
    :initial-label="model"
    :input-id="inputId"
    :aria-label="ariaLabel"
    append-to="self"
    typeahead
    dropdown
    dropdown-mode="blank"
    force-selection
    complete-on-focus
    @complete="onComplete"
    @dropdown-click="onDropdownClick"
    @item-select="onItemSelect"
  />
</template>
