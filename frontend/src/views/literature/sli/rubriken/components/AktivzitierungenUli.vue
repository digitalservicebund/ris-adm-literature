<script setup lang="ts">
import Aktivzitierung from "@/components/aktivzitierung/Aktivzitierung.vue";
import AktivzitierungUliInput from "@/components/aktivzitierung/uli/AktivzitierungUliInput.vue";
import AktivzitierungUliItem from "@/components/aktivzitierung/uli/AktivzitierungUliItem.vue";
import AktivzitierungUliSearchResult from "@/components/aktivzitierung/uli/AktivzitierungUliSearchResult.vue";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";
import { useGetUliPaginatedDocUnits } from "@/services/uli/uliDocumentUnitService";

const aktivzitierungUli = defineModel<AktivzitierungUli[]>();
</script>

<template>
  <section id="aktivzitierungUli" aria-labelledby="aktivzitierungUli-title">
    <h2 id="aktivzitierungUli-title" class="ris-body1-bold mb-16">
      Aktivzitierung (unselbst. Literatur)
    </h2>
    <div class="flex flex-row gap-24 w-full">
      <div class="flex flex-col w-full">
        <Aktivzitierung v-model="aktivzitierungUli" :fetch-results-fn="useGetUliPaginatedDocUnits">
          <template #item="{ aktivzitierung }">
            <AktivzitierungUliItem :aktivzitierung="aktivzitierung" />
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
            <AktivzitierungUliInput
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
            <AktivzitierungUliSearchResult
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
