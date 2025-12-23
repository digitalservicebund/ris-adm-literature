<script lang="ts" setup>
import TitleElement from '@/components/TitleElement.vue'
import InputField from '@/components/input/InputField.vue'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { useSliDocumentUnitStore } from '@/stores/sliDocStore'
import { DocumentCategory } from '@/domain/documentType'
import { useScrollToHash } from '@/composables/useScroll'
import InputText from 'primevue/inputtext'
import TitelSection from '@/views/literature/sli/rubriken/components/TitelSection.vue'
import DokumentTyp from '@/views/literature/DokumentTyp.vue'
import { useLiteratureRubriken } from '@/views/literature/useLiteratureRubriken'
import { computed } from 'vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import Aktivzitierung from '@/components/aktivzitierung/Aktivzitierung.vue'
import AktivzitierungAdmInput from '@/components/aktivzitierung/adm/AktivzitierungAdmInput.vue'
import AktivzitierungAdmItem from '@/components/aktivzitierung/adm/AktivzitierungAdmItem.vue'
import type { AktivzitierungSli as AktivzitierungSliType } from '@/domain/AktivzitierungSli'
import AktivzitierungSliItem from '@/components/aktivzitierung/sli/AktivzitierungSliItem.vue'
import AktivzitierungSliInput from '@/components/aktivzitierung/sli/AktivzitierungSliInput.vue'
import {
  useGetAdmPaginatedDocUnitsForSli,
  useGetSliPaginatedDocUnits,
} from '@/services/literature/literatureDocumentUnitService'
import AktivzitierungSliSearchResult from '@/components/aktivzitierung/sli/AktivzitierungSliSearchResult.vue'
import AktivzitierungAdmSearchResult from '@/components/aktivzitierung/adm/AktivzitierungAdmSearchResult.vue'
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit'
import type { AdmAktivzitierungListItem } from '@/domain/adm/admDocumentUnit'
import IconReceiptLongFilled from '~icons/ic/baseline-receipt-long'
import IconReceiptLongOutline from '~icons/ic/outline-receipt-long'

const store = useStoreForRoute<ReturnType<typeof useSliDocumentUnitStore>>()
const {
  veroeffentlichungsjahr,
  dokumenttypen,
  hauptsachtitel,
  dokumentarischerTitel,
  hauptsachtitelZusatz,
} = useLiteratureRubriken(store)

const aktivzitierungAdm = computed({
  get: () => store.documentUnit!.aktivzitierungenAdm ?? [],
  set: (newValue: AktivzitierungAdm[]) => {
    store.documentUnit!.aktivzitierungenAdm = newValue
  },
})

const aktivzitierungSli = computed({
  get: () => store.documentUnit!.aktivzitierungenSli ?? [],
  set: (newValue: AktivzitierungSliType[]) => {
    store.documentUnit!.aktivzitierungenSli = newValue
  },
})

function mapSliSearchResult(result: SliDocUnitListItem): AktivzitierungSliType {
  const dokumenttypen = result.dokumenttypen?.map((abbr) => ({
    abbreviation: abbr,
    name: abbr,
  }))

  return {
    id: crypto.randomUUID(),
    uuid: result.id,
    titel: result.titel,
    documentNumber: result.documentNumber,
    veroeffentlichungsJahr: result.veroeffentlichungsjahr,
    verfasser: result.verfasser || [],
    dokumenttypen: dokumenttypen || [],
  }
}

function mapAdmSearchResult(result: AdmAktivzitierungListItem): AktivzitierungAdm {
  const { id, ...rest } = result
  return {
    id: crypto.randomUUID(),
    uuid: id,
    ...rest,
  }
}

useScrollToHash()
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col gap-24 p-24">
    <section
      id="formaldaten"
      aria-labelledby="formaldaten-title"
      class="flex flex-col gap-24 bg-white p-24"
    >
      <TitleElement id="formaldaten-title">Formaldaten</TitleElement>
      <div class="flex flex-row gap-24">
        <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp *">
          <DokumentTyp
            :input-id="slotProps.id"
            v-model="dokumenttypen"
            aria-label="Dokumenttyp"
            :invalid="slotProps.hasError"
            :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
          />
        </InputField>
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr *">
          <InputText
            :id="slotProps.id"
            v-model="veroeffentlichungsjahr"
            aria-label="Veröffentlichungsjahr"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
      </div>
      <TitelSection
        v-model:hauptsachtitel="hauptsachtitel"
        v-model:dokumentarischer-titel="dokumentarischerTitel"
        v-model:hauptsachtitel-zusatz="hauptsachtitelZusatz"
      />
      <div>
        <p class="relative pl-12 before:content-['*'] before:absolute before:left-0">
          Pflichtfelder für die Veröffentlichung
        </p>
        <p class="pl-12">
          Hinweis: Hauptsachtitel oder Dokumentarischer Titel muss für die Veröffentlichung erfasst
          werden
        </p>
      </div>
    </section>

    <section
      id="inhaltlicheErschliessung"
      aria-labelledby="aktivzitierung-title"
      class="flex flex-col gap-24 bg-white p-24"
    >
      <TitleElement
        aria-label="Inhaltliche Erschließung"
        class="mb-24"
        id="inhaltlicheErschliessung-title"
        >Inhaltliche Erschließung</TitleElement
      >
      <section id="aktivzitierungSli" aria-labelledby="aktivzitierungSli-title">
        <h2 id="aktivzitierungSli-title" class="ris-body1-bold mb-16">
          Aktivzitierung (selbst. Literatur)
        </h2>
        <div class="flex flex-row gap-24 w-full">
          <div class="flex flex-col w-full">
            <Aktivzitierung
              v-model="aktivzitierungSli"
              :fetch-results-fn="useGetSliPaginatedDocUnits"
              :transform-result-fn="mapSliSearchResult"
            >
              <template #item="{ aktivzitierung }">
                <AktivzitierungSliItem :aktivzitierung="aktivzitierung" />
              </template>

              <template #input="{ modelValue, onUpdateModelValue }">
                <AktivzitierungSliInput
                  :modelValue="modelValue"
                  @update:modelValue="onUpdateModelValue"
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
      <section id="aktivzitierungAdm" aria-labelledby="aktivzitierungAdm-title">
        <h2 id="aktivzitierungAdm-title" class="ris-body1-bold mb-16">
          Aktivzitierung (Verwaltungsvorschrift)
        </h2>
        <div class="flex flex-row gap-24 w-full">
          <div class="flex flex-col w-full">
            <Aktivzitierung
              v-model="aktivzitierungAdm"
              :fetch-results-fn="useGetAdmPaginatedDocUnitsForSli"
              :transform-result-fn="mapAdmSearchResult"
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
    </section>
  </div>
</template>
