<script setup lang="ts">
import Aktivzitierung from "@/components/aktivzitierung/Aktivzitierung.vue";
import { useGetSliPaginatedDocUnits } from "@/services/sli/sliDocumentUnitService";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import AktivzitierungSliInput from "@/components/aktivzitierung/sli/AktivzitierungSliInput.vue";
import AktivzitierungSliItem from "@/components/aktivzitierung/sli/AktivzitierungSliItem.vue";
import AktivzitierungSliSearchResult from "@/components/aktivzitierung/sli/AktivzitierungSliSearchResult.vue";

const aktivzitierungSli = defineModel<AktivzitierungSli[]>();
</script>

<template>
  <section id="aktivzitierungSli" aria-labelledby="aktivzitierungSli-title">
    <h2 id="aktivzitierungSli-title" class="ris-body1-bold mb-16">
      Aktivzitierung (selbst. Literatur)
    </h2>
    <div class="flex flex-row gap-24 w-full">
      <div class="flex flex-col w-full">
        <Aktivzitierung v-model="aktivzitierungSli" :fetch-results-fn="useGetSliPaginatedDocUnits">
          <template #item="{ aktivzitierung }">
            <AktivzitierungSliItem :aktivzitierung="aktivzitierung" />
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
            <AktivzitierungSliInput
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
            <AktivzitierungSliSearchResult
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
