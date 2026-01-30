<script lang="ts" setup>
import TitleElement from "@/components/TitleElement.vue";
import InputField from "@/components/input/InputField.vue";
import { useStoreForRoute } from "@/composables/useStoreForRoute";
import type { useSliDocumentUnitStore } from "@/stores/sliDocStore";
import { DocumentCategory } from "@/domain/documentType";
import { useScrollToHash } from "@/composables/useScroll";
import InputText from "primevue/inputtext";
import TitelSection from "@/views/literature/sli/rubriken/components/TitelSection.vue";
import DokumentTyp from "@/views/literature/DokumentTyp.vue";
import { useLiteratureRubriken } from "@/views/literature/useLiteratureRubriken";
import { computed } from "vue";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import AktivzitierungenAdm from "./components/AktivzitierungenAdm.vue";
import AktivzitierungenSli from "./components/AktivzitierungenSli.vue";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";
import AktivzitierungenRechtsprechung from "./components/AktivzitierungenRechtsprechung.vue";

const store = useStoreForRoute<ReturnType<typeof useSliDocumentUnitStore>>();
const {
  veroeffentlichungsjahr,
  dokumenttypen,
  hauptsachtitel,
  dokumentarischerTitel,
  hauptsachtitelZusatz,
} = useLiteratureRubriken(store);

const aktivzitierungAdm = computed({
  get: () => store.documentUnit!.aktivzitierungenAdm ?? [],
  set: (newValue: AktivzitierungAdm[]) => {
    store.documentUnit!.aktivzitierungenAdm = newValue;
  },
});

const aktivzitierungSli = computed({
  get: () => store.documentUnit!.aktivzitierungenSli ?? [],
  set: (newValue: AktivzitierungSli[]) => {
    store.documentUnit!.aktivzitierungenSli = newValue;
  },
});

const aktivzitierungRechtsprechung = computed({
  get: () => store.documentUnit!.aktivzitierungenRechtsprechung ?? [],
  set: (newValue: AktivzitierungRechtsprechung[]) => {
    store.documentUnit!.aktivzitierungenRechtsprechung = newValue;
  },
});

useScrollToHash();
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
        <p
          class="ris-body2-regular relative pl-12 before:content-['*'] before:absolute before:left-0"
        >
          Pflichtfelder für die Veröffentlichung
        </p>
        <p class="ris-body2-regular pl-12">
          Hinweis: Entweder Hauptsachtitel oder Dokumentarischer Titel ist erforderlich
        </p>
      </div>
    </section>

    <section
      id="inhaltlicheErschliessung"
      aria-labelledby="aktivzitierung-title"
      class="bg-white p-24"
    >
      <TitleElement
        class="mb-24"
        aria-label="Inhaltliche Erschließung"
        id="inhaltlicheErschliessung-title"
        >Inhaltliche Erschließung</TitleElement
      >
      <div class="flex flex-col gap-32">
        <AktivzitierungenRechtsprechung
          v-model="aktivzitierungRechtsprechung"
          require-citation-type
        />
        <AktivzitierungenSli v-model="aktivzitierungSli" />
        <AktivzitierungenAdm v-model="aktivzitierungAdm" require-citation-type />
      </div>
    </section>
  </div>
</template>
