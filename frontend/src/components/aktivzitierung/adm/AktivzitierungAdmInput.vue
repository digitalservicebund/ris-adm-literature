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
import { Button } from "primevue";
import { isAktivzitierungEmpty } from "@/utils/validators";
import { useValidationStore } from "@/composables/useValidationStore";
import { useCitationTypeRequirement } from "@/components/useCitationaTypeRequirement";

const props = defineProps<{
  aktivzitierung?: AktivzitierungAdm;
  showCancelButton: boolean;
  showDeleteButton: boolean;
  showSearchButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungAdm];
  delete: [id: string];
  cancel: [void];
  search: [params: Record<string, unknown>];
}>();

function createInitial(): AktivzitierungAdm {
  return { id: crypto.randomUUID() };
}

const aktivzitierungAdmRef = ref<AktivzitierungAdm>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitial(),
);

const citationType = computed({
  get: () => {
    return aktivzitierungAdmRef.value.citationType
      ? ({
          id: aktivzitierungAdmRef.value.citationType,
          label: aktivzitierungAdmRef.value.citationType,
        } as ZitierArt)
      : undefined;
  },
  set: (val: ZitierArt | undefined) => {
    aktivzitierungAdmRef.value.citationType = val?.abbreviation;
  },
});

const normgeber = computed({
  get: () => {
    return aktivzitierungAdmRef.value.normgeber
      ? ({
          id: aktivzitierungAdmRef.value.normgeber,
          name: aktivzitierungAdmRef.value.normgeber,
        } as Institution)
      : undefined;
  },
  set: (val: Institution | undefined) => {
    aktivzitierungAdmRef.value.normgeber = val?.name;
  },
});

const periodikum = computed({
  get: () => {
    return aktivzitierungAdmRef.value.periodikum
      ? ({
          id: aktivzitierungAdmRef.value.periodikum,
          abbreviation: aktivzitierungAdmRef.value.periodikum,
        } as Periodikum)
      : undefined;
  },
  set: (val: Periodikum | undefined) => {
    aktivzitierungAdmRef.value.periodikum = val?.abbreviation;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungAdm | undefined) => {
    if (newVal) {
      aktivzitierungAdmRef.value = { ...newVal };
    }
  },
);

const isEmpty = computed(() => isAktivzitierungEmpty(aktivzitierungAdmRef.value));

const isExistingEntry = computed(() => !!props.aktivzitierung?.id);

function onClickSave() {
  emit("save", aktivzitierungAdmRef.value);
  if (!isExistingEntry.value) {
    aktivzitierungAdmRef.value = createInitial();
  }
}

function onClickCancel() {
  emit("cancel");
}

function onClickDelete() {
  emit("delete", aktivzitierungAdmRef.value.id);
}

function onClickSearch() {
  emit("search", aktivzitierungAdmRef.value);
}

const { validationStore, clear, setCurrentCitationType } = useCitationTypeRequirement();
const dateValidationStore = useValidationStore<"inkrafttretedatum">();

function onCitationTypeUpdate(selectedCitationType: ZitierArt | undefined) {
  clear();
  setCurrentCitationType(selectedCitationType?.abbreviation);
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <div class="flex flex-row gap-24">
        <InputField
          id="activeCitationPredicate"
          label="Art der Zitierung"
          v-slot="slotProps"
          :validation-error="validationStore.getByField('citationType')"
        >
          <ZitierArtDropDown
            :input-id="slotProps.id"
            v-model="citationType"
            :invalid="slotProps.hasError"
            :source-document-category="DocumentCategory.LITERATUR"
            :target-document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
            @update:modelValue="onCitationTypeUpdate($event as ZitierArt | undefined)"
          />
        </InputField>
        <InputField id="normgeber" label="Normgeber" v-slot="slotProps">
          <InstitutionDropDown :input-id="slotProps.id" v-model="normgeber" :isInvalid="false" />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField
          id="inkrafttretedatum"
          label="Datum des Inkrafttretens"
          v-slot="slotProps"
          :validation-error="dateValidationStore.getByField('inkrafttretedatum')"
          @update:validation-error="
            (validationError) =>
              validationError
                ? dateValidationStore.add(validationError.message, 'inkrafttretedatum')
                : dateValidationStore.remove('inkrafttretedatum')
          "
        >
          <DateInput
            :id="slotProps.id"
            v-model="aktivzitierungAdmRef.inkrafttretedatum"
            ariaLabel="Inkrafttretedatum"
            class="ds-input-medium"
            is-future-date
            :has-error="slotProps.hasError"
            @focus="dateValidationStore.remove('inkrafttretedatum')"
            @update:validation-error="slotProps.updateValidationError"
          />
        </InputField>
        <InputField id="aktenzeichen" v-slot="slotProps" label="Aktenzeichen">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungAdmRef.aktenzeichen"
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
            v-model="aktivzitierungAdmRef.zitatstelle"
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
            v-model="aktivzitierungAdmRef.documentNumber"
            aria-label="Dokumentnummer"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
        <InputField id="docType" label="Dokumenttyp" v-slot="slotProps">
          <DokumentTypDropDown
            :input-id="slotProps.id"
            v-model="aktivzitierungAdmRef.dokumenttyp"
            aria-label="Dokumenttyp"
            :isInvalid="false"
            :document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
          />
        </InputField>
      </div>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        v-if="showSearchButton"
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
