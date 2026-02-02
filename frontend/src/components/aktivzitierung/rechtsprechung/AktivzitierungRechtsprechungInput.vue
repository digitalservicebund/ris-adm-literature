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
import { useScrollToElement } from "@/composables/useScroll";
import { useValidationStore } from "@/composables/useValidationStore";

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

type RsField = "gericht" | "entscheidungsdatum" | "aktenzeichen";
const rsValidationStore = useValidationStore<RsField>();

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
const citationTypeFieldRef = ref<HTMLElement | null>(null);
const gerichtFieldRef = ref<HTMLElement | null>(null);
const entscheidungsdatumFieldRef = ref<HTMLElement | null>(null);
const aktenzeichenFieldRef = ref<HTMLElement | null>(null);

const { validationStore, clear, setCurrentCitationType } =
  useCitationTypeRequirement("rechtsprechung");

const { hasValidationErrors } = useSubmitValidation([
  () => validationStore.getByField("citationType")?.message,
  () => rsValidationStore.getByField("gericht")?.message,
  () => rsValidationStore.getByField("entscheidungsdatum")?.message,
  () => rsValidationStore.getByField("aktenzeichen")?.message,
]);

const MANDATORY_MESSAGE = "Pflichtfeld nicht befüllt";

function validate(): boolean {
  const missingCitationType = !aktivzitierungRsRef.value.citationType?.trim();
  const missingGericht = !aktivzitierungRsRef.value.gerichttyp?.trim();
  const missingAktenzeichen = !aktivzitierungRsRef.value.aktenzeichen?.trim();

  const missingEntscheidungsdatum = !aktivzitierungRsRef.value.entscheidungsdatum?.trim();

  if (missingCitationType) validationStore.add(MANDATORY_MESSAGE, "citationType");
  if (missingGericht) rsValidationStore.add(MANDATORY_MESSAGE, "gericht");
  if (missingEntscheidungsdatum) rsValidationStore.add(MANDATORY_MESSAGE, "entscheidungsdatum");
  if (missingAktenzeichen) rsValidationStore.add(MANDATORY_MESSAGE, "aktenzeichen");

  if (missingCitationType || missingGericht || missingEntscheidungsdatum || missingAktenzeichen) {
    if (missingCitationType) useScrollToElement(citationTypeFieldRef);
    else if (missingGericht) useScrollToElement(gerichtFieldRef);
    else if (missingEntscheidungsdatum) useScrollToElement(entscheidungsdatumFieldRef);
    else useScrollToElement(aktenzeichenFieldRef);
    return false;
  }
  return true;
}

watch(
  () => validationStore.getByField("citationType"),
  (error) => {
    if (!error) return;
    useScrollToElement(citationTypeFieldRef);
  },
);

watch(
  () => aktivzitierungRsRef.value.gerichttyp,
  (gerichttyp) => {
    if (gerichttyp?.trim()) rsValidationStore.remove("gericht");
  },
);

watch(
  () => aktivzitierungRsRef.value.entscheidungsdatum,
  (entscheidungsdatum) => {
    if (entscheidungsdatum?.trim()) rsValidationStore.remove("entscheidungsdatum");
  },
);
watch(
  () => aktivzitierungRsRef.value.aktenzeichen,
  (aktenzeichen) => {
    if (aktenzeichen?.trim()) rsValidationStore.remove("aktenzeichen");
  },
);

function onCitationTypeUpdate(selectedCitationType: ZitierArt | undefined) {
  clear();
  setCurrentCitationType(selectedCitationType?.abbreviation);
}

function onClickSave() {
  if (!validate()) return;
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
        <InputField
          id="rs-activeCitationPredicate"
          ref="citationTypeFieldRef"
          label="Art der Zitierung *"
          v-slot="slotProps"
          :validation-error="validationStore.getByField('citationType')"
        >
          <ZitierArtDropDown
            :input-id="slotProps.id"
            v-model="citationType"
            :invalid="slotProps.hasError"
            :source-document-category="DocumentCategory.LITERATUR"
            :target-document-category="DocumentCategory.RECHTSPRECHUNG"
            @update:modelValue="onCitationTypeUpdate($event as ZitierArt | undefined)"
          />
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField
          id="gericht"
          ref="gerichtFieldRef"
          label="Gericht *"
          v-slot="slotProps"
          :validation-error="rsValidationStore.getByField('gericht')"
        >
          <CourtDropDown :input-id="slotProps.id" v-model="gericht" :invalid="slotProps.hasError" />
        </InputField>
        <InputField
          id="entscheidungsdatum"
          ref="entscheidungsdatumFieldRef"
          label="Entscheidungsdatum *"
          v-slot="slotProps"
          :validation-error="rsValidationStore.getByField('entscheidungsdatum')"
          @update:validation-error="
            (validationError) =>
              validationError
                ? rsValidationStore.add(validationError.message, 'entscheidungsdatum')
                : rsValidationStore.remove('entscheidungsdatum')
          "
        >
          <DateInput
            :id="slotProps.id"
            v-model="aktivzitierungRsRef.entscheidungsdatum"
            ariaLabel="Entscheidungsdatum"
            class="ds-input-medium"
            is-future-date
            :has-error="slotProps.hasError"
            @focus="rsValidationStore.remove('entscheidungsdatum')"
            @update:validation-error="slotProps.updateValidationError"
          ></DateInput>
        </InputField>
      </div>
      <div class="flex flex-row gap-24">
        <InputField
          id="aktenzeichen"
          ref="aktenzeichenFieldRef"
          v-slot="slotProps"
          label="Aktenzeichen *"
          :validation-error="rsValidationStore.getByField('aktenzeichen')"
        >
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
