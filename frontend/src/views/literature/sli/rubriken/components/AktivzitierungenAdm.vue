<script setup lang="ts">
import Aktivzitierung from "@/components/aktivzitierung/Aktivzitierung.vue";
import AktivzitierungAdmInput from "@/components/aktivzitierung/adm/AktivzitierungAdmInput.vue";
import AktivzitierungAdmItem from "@/components/aktivzitierung/adm/AktivzitierungAdmItem.vue";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import { useGetAdmPaginatedDocUnitsForSli } from "@/services/literature/literatureDocumentUnitService";
import AktivzitierungAdmSearchResult from "@/components/aktivzitierung/adm/AktivzitierungAdmSearchResult.vue";

const aktivzitierungAdm = defineModel<AktivzitierungAdm[]>();
const props = defineProps<{
  requireCitationType?: boolean;
}>();
</script>

<template>
  <section id="aktivzitierungAdm" aria-labelledby="aktivzitierungAdm-title">
    <h2 id="aktivzitierungAdm-title" class="ris-body1-bold mb-16">
      Aktivzitierung (Verwaltungsvorschrift)
    </h2>
    <div class="flex flex-row gap-24 w-full">
      <div class="flex flex-col w-full">
        <Aktivzitierung
          v-model="aktivzitierungAdm"
          :fetch-results-fn="useGetAdmPaginatedDocUnitsForSli"
          :require-citation-type="requireCitationType"
        >
          <template #item="{ aktivzitierung }">
            <AktivzitierungAdmItem :aktivzitierung="aktivzitierung" />
          </template>

          <template
            #input="{
              aktivzitierung,
              showCancelButton,
              showDeleteButton,
              showSearchButton,
              onSave,
              onDelete,
              onCancel,
              onSearch,
            }"
          >
            <AktivzitierungAdmInput
              :aktivzitierung="aktivzitierung"
              :show-cancel-button="showCancelButton"
              :show-delete-button="showDeleteButton"
              :show-search-button="showSearchButton"
              @save="onSave"
              @delete="onDelete"
              @cancel="onCancel"
              @search="onSearch"
            />
          </template>

          <template #searchResult="{ searchResult, isAdded, onAdd }">
            <AktivzitierungAdmSearchResult
              :searchResult="searchResult"
              :is-added="isAdded"
              @add="onAdd"
            />
          </template>
        </Aktivzitierung>
      </div>
    </div>
  </section>
</template>
