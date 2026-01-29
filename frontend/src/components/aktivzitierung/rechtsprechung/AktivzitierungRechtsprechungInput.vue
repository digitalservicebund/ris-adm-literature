<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import InputText from "primevue/inputtext";
import { computed, ref, watch } from "vue";
import type { ZitierArt } from "@/domain/zitierArt";
import ZitierArtDropDown from "@/components/dropdown/ZitierArtDropDown.vue";
import DateInput from "@/components/input/DateInput.vue";
import DokumentTypDropDown from "@/components/dropdown/DokumentTypDropDown.vue";
import { DocumentCategory } from "@/domain/documentType";
import { Button } from "primevue";
import { isAktivzitierungEmpty } from "@/utils/validators";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";
import type { Court } from "@/domain/court";
import CourtDropDown from "@/components/dropdown/CourtDropDown.vue";
import { useCitationTypeRequirement } from "@/composables/useCitationaTypeRequirement";
import { useSubmitValidation } from "@/composables/useSubmitValidation";

const props = defineProps<{
  aktivzitierung?: AktivzitierungRechtsprechung;
  showCancelButton: boolean;
  showDeleteButton: boolean;
  showSearchButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungRechtsprechung];
  delete: [id: string];
  cancel: [void];
  search: [params: Record<string, unknown>];
}>();

function createInitial(): AktivzitierungRechtsprechung {
  return { id: crypto.randomUUID() };
}

const aktivzitierungRsRef = ref<AktivzitierungRechtsprechung>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitial(),
);

const citationType = computed({
  get: () => {
    return aktivzitierungRsRef.value.citationType
      ? ({
          id: aktivzitierungRsRef.value.citationType,
          label: aktivzitierungRsRef.value.citationType,
        } as ZitierArt)
      : undefined;
  },
  set: (val: ZitierArt | undefined) => {
    aktivzitierungRsRef.value.citationType = val?.abbreviation;
  },
});

const gericht = computed({
  get: () => {
    return aktivzitierungRsRef.value.gerichttyp
      ? {
          id: aktivzitierungRsRef.value.gerichttyp,
          type: aktivzitierungRsRef.value.gerichttyp,
          location: aktivzitierungRsRef.value.gerichtort,
        }
      : undefined;
  },
  set: (val: Court | undefined) => {
    aktivzitierungRsRef.value.gerichttyp = val?.type;
    aktivzitierungRsRef.value.gerichtort = val?.location;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungRechtsprechung | undefined) => {
    if (newVal) {
      aktivzitierungRsRef.value = { ...newVal };
    }
  },
);

const isEmpty = computed(() => isAktivzitierungEmpty(aktivzitierungRsRef.value));

const isExistingEntry = computed(() => !!props.aktivzitierung?.id);

const { validationStore, clear, setCurrentCitationType } = useCitationTypeRequirement("rechtsprechung");
const { hasValidationErrors } = useSubmitValidation([
  () => validationStore.getByField("citationType")?.message,
]);

watch(
  () => validationStore.getByField("citationType"),
  (error) => {
    if (!error) return;
    document.getElementById("rs-activeCitationPredicate")?.scrollIntoView({ behavior: "smooth", block: "center" });
  },
);

function onCitationTypeUpdate(selectedCitationType: ZitierArt | undefined) {
  clear();
  setCurrentCitationType(selectedCitationType?.abbreviation);
}

function onClickSave() {
  if (!aktivzitierungRsRef.value.citationType?.trim()) {
    validationStore.add("Pflichtfeld nicht befüllt", "citationType");
    document.getElementById("rs-activeCitationPredicate")?.scrollIntoView({ behavior: "smooth", block: "center" });
    return;
  }
  if (hasValidationErrors.value) return;

  emit("save", aktivzitierungRsRef.value);
  if (!isExistingEntry.value) {
    aktivzitierungRsRef.value = createInitial();
  }
}

function onClickCancel() {
  emit("cancel");
}

function onClickDelete() {
  emit("delete", aktivzitierungRsRef.value.id);
}

function onClickSearch() {
  emit("search", aktivzitierungRsRef.value);
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <div class="flex flex-row gap-24">
        <InputField id="rs-activeCitationPredicate" label="Art der Zitierung" v-slot="slotProps" :validation-error="validationStore.getByField('citationType')">
          <ZitierArtDropDown
            :input-id="slotProps.id"
            v-model="citationType"
            :invalid="slotProps.hasError"
            :source-document-category="DocumentCategory.LITERATUR"
            :target-document-category="DocumentCategory.VERWALTUNGSVORSCHRIFTEN"
            @update:modelValue="onCitationTypeUpdate($event as ZitierArt | undefined)"
          />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField id="gericht" label="Gericht" v-slot="slotProps">
          <CourtDropDown :input-id="slotProps.id" v-model="gericht" :invalid="false" />
        </InputField>
        <InputField id="entscheidungsdatum" label="Entscheidungsdatum" v-slot="slotProps">
          <DateInput
            :id="slotProps.id"
            v-model="aktivzitierungRsRef.entscheidungsdatum"
            ariaLabel="Entscheidungsdatum"
            class="ds-input-medium"
            is-future-date
            :has-error="false"
          ></DateInput>
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField id="aktenzeichen" v-slot="slotProps" label="Aktenzeichen">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungRsRef.aktenzeichen"
            aria-label="Aktenzeichen"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
        <InputField id="docType" label="Dokumenttyp" v-slot="slotProps">
          <DokumentTypDropDown
            :input-id="slotProps.id"
            v-model="aktivzitierungRsRef.dokumenttyp"
            aria-label="Dokumenttyp"
            :isInvalid="false"
            :document-category="DocumentCategory.RECHTSPRECHUNG"
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
