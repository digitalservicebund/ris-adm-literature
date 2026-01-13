<script setup lang="ts">
import { computed } from "vue";
import Button from "primevue/button";
import IconAdd from "~icons/material-symbols/add";
import { parseIsoDateToLocal } from "@/utils/dateHelpers";
import type { AdmAktivzitierungListItem } from "@/domain/adm/admDocumentUnit";

const props = defineProps<{
  searchResult: AdmAktivzitierungListItem;
  isAdded: boolean;
}>();

const emit = defineEmits<{
  add: [searchResult: AdmAktivzitierungListItem];
}>();

function handleAdd() {
  if (!props.isAdded) emit("add", props.searchResult);
}

const line1 = computed(() => {
  const { normgeberList, inkrafttretedatum, aktenzeichenList, fundstellen, dokumenttyp } =
    props.searchResult;

  // Helper to safely get the first non-empty item from a potential array
  const firstOf = (list?: string[]) => (list?.length ? list[0] : null);

  const parts: string[] = [];

  const normgeber = firstOf(normgeberList);
  if (normgeber) parts.push(normgeber);

  const date = inkrafttretedatum ? parseIsoDateToLocal(inkrafttretedatum) : null;
  if (date) parts.push(date);

  const az = firstOf(aktenzeichenList);
  if (az) parts.push(az);

  const fundstelle = firstOf(fundstellen);

  if (fundstelle && dokumenttyp) {
    parts.push(`${fundstelle} (${dokumenttyp})`);
  } else if (fundstelle || dokumenttyp) {
    parts.push(fundstelle || `(${dokumenttyp})`);
  }

  return parts.join(", ");
});

const line2 = computed(() => props.searchResult.langueberschrift || "unbekannt");
</script>

<template>
  <div class="search-result flex items-center w-full">
    <Button
      aria-label="Aktivzitierung hinzufügen"
      size="small"
      class="mr-16 shrink-0"
      :disabled="isAdded"
      @click="handleAdd"
    >
      <template #icon><IconAdd /></template>
    </Button>

    <div class="flex flex-col w-full">
      <div class="relative">
        <p class="ris-body1-regular inline-block relative pr-8">
          {{ line1 || "—" }} | {{ searchResult.documentNumber }}

          <span
            v-if="isAdded"
            class="absolute top-1/2 -translate-y-1/2 ris-label2-regular rounded-full ml-10 px-6 py-2 bg-yellow-300 whitespace-nowrap"
            aria-label="Bereits hinzugefügt"
          >
            <span class="text-yellow-900">Bereits hinzugefügt</span>
          </span>
        </p>
      </div>

      <p class="ris-body2-regular text-gray-900">
        {{ line2 }}
      </p>
    </div>
  </div>
</template>
