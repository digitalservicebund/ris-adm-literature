<script setup lang="ts">
import Aktivzitierung from '@/components/aktivzitierung/Aktivzitierung.vue'
import AktivzitierungAdmInput from '@/components/aktivzitierung/adm/AktivzitierungAdmInput.vue'
import AktivzitierungAdmItem from '@/components/aktivzitierung/adm/AktivzitierungAdmItem.vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import {
  mapAdmSearchResultToAktivzitierung,
  useGetAdmPaginatedDocUnitsForSli,
} from '@/services/literature/literatureDocumentUnitService'
import IconReceiptLongFilled from '~icons/ic/baseline-receipt-long'
import IconReceiptLongOutline from '~icons/ic/outline-receipt-long'
import AktivzitierungAdmSearchResult from '@/components/aktivzitierung/adm/AktivzitierungAdmSearchResult.vue'

const aktivzitierungAdm = defineModel<AktivzitierungAdm[]>()
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
          :transform-result-fn="mapAdmSearchResultToAktivzitierung"
        >
          <template #icon="{ isFromSearch }">
            <IconReceiptLongFilled v-if="isFromSearch" class="text-neutral-800" />
            <IconReceiptLongOutline v-else class="text-neutral-800" />
          </template>
          <template #item="{ aktivzitierung }">
            <AktivzitierungAdmItem :aktivzitierung="aktivzitierung" />
          </template>

          <template #input="{ modelValue, onUpdateModelValue }">
            <AktivzitierungAdmInput
              :modelValue="modelValue"
              @update:modelValue="onUpdateModelValue"
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
