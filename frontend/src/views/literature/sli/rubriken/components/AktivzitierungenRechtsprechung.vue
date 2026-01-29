<script setup lang="ts">
import Aktivzitierung from "@/components/aktivzitierung/Aktivzitierung.vue";
import { useGetAdmPaginatedDocUnitsForSli } from "@/services/literature/literatureDocumentUnitService";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";
import AktivzitierungRechtsprechungInput from "@/components/aktivzitierung/rechtsprechung/AktivzitierungRechtsprechungInput.vue";
import AktivzitierungRechtsprechungItem from "@/components/aktivzitierung/rechtsprechung/AktivzitierungRechtsprechungItem.vue";

const aktivzitierung = defineModel<AktivzitierungRechtsprechung[]>();
</script>

<template>
  <section id="AktivzitierungRechtsprechung" aria-labelledby="aktivzitierungRechtsprechung-title">
    <h2 id="aktivzitierungRechtsprechung-title" class="ris-body1-bold mb-16">
      Aktivzitierung (Rechtsprechung)
    </h2>
    <div class="flex flex-row gap-24 w-full">
      <div class="flex flex-col w-full">
        <Aktivzitierung
          v-model="aktivzitierung"
          :fetch-results-fn="useGetAdmPaginatedDocUnitsForSli"
          :citation-type-scope="'rechtsprechung'"
        >
          <template #item="{ aktivzitierung }">
            <AktivzitierungRechtsprechungItem :aktivzitierung="aktivzitierung" />
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
            <AktivzitierungRechtsprechungInput
              :aktivzitierung="aktivzitierung"
              :show-cancel-button="showCancelButton"
              :show-delete-button="showDeleteButton"
              :show-search-button="false"
              @save="onSave"
              @delete="onDelete"
              @cancel="onCancel"
              @search="onSearch"
            />
          </template>
        </Aktivzitierung>
      </div>
    </div>
  </section>
</template>
