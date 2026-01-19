<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import InputText from "primevue/inputtext";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import { computed, ref, watch } from "vue";
import type { ZitierArt } from "@/domain/zitierArt";
import ZitierArtDropDown from "@/components/dropdown/ZitierArtDropDown.vue";
import InstitutionDropDown from "@/components/dropdown/InstitutionDropDown.vue";
import type { Institution } from "@/domain/normgeber";
import DateInput from "@/components/input/DateInput.vue";
import PeriodikumDropDown from "@/components/dropdown/PeriodikumDropDown.vue";
import type { Periodikum } from "@/domain/fundstelle";
import DokumentTypDropDown from "@/components/dropdown/DokumentTypDropDown.vue";
import { DocumentCategory } from "@/domain/documentType";
import type { AdmDocUnitSearchParams } from "@/domain/adm/admDocumentUnit";
import { Button } from "primevue";

const props = defineProps<{
  aktivzitierung?: AktivzitierungAdm;
  showCancelButton: boolean;
  showDeleteButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungAdm];
  delete: [id: string];
  cancel: [void];
  search: [params: Record<string, unknown>];
}>();

function createInitialT() {
  return { id: crypto.randomUUID() } as AktivzitierungAdm;
}

const aktivzitierungRef = ref<AktivzitierungAdm>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitialT(),
);

const citationType = computed({
  get: () => {
    return aktivzitierungRef.value.citationType
      ? ({
          id: aktivzitierungRef.value.citationType,
          label: aktivzitierungRef.value.citationType,
        } as ZitierArt)
      : undefined;
  },
  set: (val: ZitierArt | undefined) => {
    aktivzitierungRef.value.citationType = val?.abbreviation;
  },
});

const normgeber = computed({
  get: () => {
    return aktivzitierungRef.value.normgeber
      ? ({
          id: aktivzitierungRef.value.normgeber,
          name: aktivzitierungRef.value.normgeber,
        } as Institution)
      : undefined;
  },
  set: (val: Institution | undefined) => {
    aktivzitierungRef.value.normgeber = val?.name;
  },
});

const periodikum = computed({
  get: () => {
    return aktivzitierungRef.value.periodikum
      ? ({
          id: aktivzitierungRef.value.periodikum,
          abbreviation: aktivzitierungRef.value.periodikum,
        } as Periodikum)
      : undefined;
  },
  set: (val: Periodikum | undefined) => {
    aktivzitierungRef.value.periodikum = val?.abbreviation;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungAdm | undefined) => {
    if (newVal) {
      aktivzitierungRef.value = { ...newVal };
    }
  },
);

const isEmpty = computed(() => {
  const value = aktivzitierungRef.value as Record<string, unknown>;
  const entries = Object.entries(value).filter(([key]) => key !== "id");

  if (entries.length === 0) return true;

  return entries.every(([, v]) => {
    if (v === undefined || v === null) return true;
    if (typeof v === "string") return v.trim() === "";
    if (Array.isArray(v)) return v.length === 0;
    return false;
  });
});

const isExistingEntry = computed(() => !!props.aktivzitierung?.id);

function onClickSave() {
  emit("save", aktivzitierungRef.value);
  if (!isExistingEntry.value) {
    aktivzitierungRef.value = createInitialT();
  }
}

function onClickCancel() {
  emit("cancel");
}

function onClickDelete() {
  emit("delete", aktivzitierungRef.value.id);
}

function onClickSearch() {
  emit("search", aktivzitierungRef.value);
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <div class="flex flex-row gap-24">
        <InputField id="activeCitationPredicate" label="Art der Zitierung" v-slot="slotProps">
          <ZitierArtDropDown
            :input-id="slotProps.id"
            v-model="citationType"
            :invalid="false"
            :source-document-category="DocumentCategory.LITERATUR"
            :target-document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
          />
        </InputField>
        <InputField id="normgeber" label="Normgeber" v-slot="slotProps">
          <InstitutionDropDown :input-id="slotProps.id" v-model="normgeber" :isInvalid="false" />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField id="inkrafttretedatum" label="Datum des Inkrafttretens" v-slot="slotProps">
          <DateInput
            :id="slotProps.id"
            v-model="aktivzitierungRef.inkrafttretedatum"
            ariaLabel="Inkrafttretedatum"
            class="ds-input-medium"
            is-future-date
            :has-error="false"
          ></DateInput>
        </InputField>
        <InputField id="aktenzeichen" v-slot="slotProps" label="Aktenzeichen">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungRef.aktenzeichen"
            aria-label="Aktenzeichen"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField id="periodikum" label="Periodikum" v-slot="slotProps">
          <PeriodikumDropDown :input-id="slotProps.id" v-model="periodikum" :invalid="false" />
        </InputField>
        <InputField id="zitatstelle" v-slot="slotProps" label="Zitatstelle">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungRef.zitatstelle"
            aria-label="Zitatstelle"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField id="documentNumber" v-slot="slotProps" label="Dokumentnummer">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungRef.documentNumber"
            aria-label="Dokumentnummer"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
        <InputField id="docType" label="Dokumenttyp" v-slot="slotProps">
          <DokumentTypDropDown
            :input-id="slotProps.id"
            v-model="aktivzitierungRef.dokumenttyp"
            aria-label="Dokumenttyp"
            :isInvalid="false"
            :document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
          />
        </InputField>
      </div>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        aria-label="Dokumente Suchen"
        label="Suchen"
        size="small"
        @click.stop="onClickSearch"
      />
      <Button
        aria-label="Aktivzitierung übernehmen"
        label="Übernehmen"
        severity="secondary"
        size="small"
        :disabled="isEmpty"
        @click.stop="onClickSave"
      />
      <Button
        v-if="showCancelButton"
        aria-label="Abbrechen"
        label="Abbrechen"
        size="small"
        text
        @click.stop="onClickCancel"
      />
      <Button
        v-if="showDeleteButton"
        class="ml-auto"
        aria-label="Eintrag löschen"
        severity="danger"
        label="Eintrag löschen"
        size="small"
        @click.stop="onClickDelete"
      />
    </div>
  </div>
</template>
