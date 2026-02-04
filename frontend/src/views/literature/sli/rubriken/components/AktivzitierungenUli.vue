<script setup lang="ts">
import Aktivzitierung from "@/components/aktivzitierung/Aktivzitierung.vue";
import AktivzitierungUliInput from "@/components/aktivzitierung/uli/AktivzitierungUliInput.vue";
import AktivzitierungUliItem from "@/components/aktivzitierung/uli/AktivzitierungUliItem.vue";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";
import { useGetAdmPaginatedDocUnitsForSli } from "@/services/literature/literatureDocumentUnitService";

const aktivzitierungUli = defineModel<AktivzitierungUli[]>();
</script>

<template>
  <section id="aktivzitierungUli" aria-labelledby="aktivzitierungUli-title">
    <h2 id="aktivzitierungUli-title" class="ris-body1-bold mb-16">
      Aktivzitierung Literatur (unselbst.)
    </h2>
    <div class="flex flex-row gap-24 w-full">
      <div class="flex flex-col w-full">
        <Aktivzitierung
          v-model="aktivzitierungUli"
          :fetch-results-fn="useGetAdmPaginatedDocUnitsForSli"
        >
          <template #item="{ aktivzitierung }">
            <AktivzitierungUliItem :aktivzitierung="aktivzitierung" />
          </template>

          <template
            #input="{
              aktivzitierung,
              showCancelButton,
              showDeleteButton,
              onSave,
              onDelete,
              onCancel,
            }"
          >
            <AktivzitierungUliInput
              :aktivzitierung="aktivzitierung"
              :show-cancel-button="showCancelButton"
              :show-delete-button="showDeleteButton"
              @save="onSave"
              @delete="onDelete"
              @cancel="onCancel"
            />
          </template>
        </Aktivzitierung>
      </div>
    </div>
  </section>
</template>
