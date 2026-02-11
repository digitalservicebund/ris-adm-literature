<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import InputText from "primevue/inputtext";
import { RisChipsInput } from "@digitalservicebund/ris-ui/components";
import PeriodikumDropDown from "@/components/dropdown/PeriodikumDropDown.vue";
import type { Periodikum } from "@/domain/fundstelle";
import { DocumentCategory } from "@/domain/documentType";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";
import { Button } from "primevue";
import { computed, ref, watch } from "vue";
import { isAktivzitierungEmpty } from "@/utils/validators";
import { useValidationStore } from "@/composables/useValidationStore";
import { useSubmitValidation } from "@/composables/useSubmitValidation";
import { useScrollToElement } from "@/composables/useScroll";
import DokumentTyp from "@/views/literature/DokumentTyp.vue";
import type { DocumentType } from "@/domain/documentType";

const props = defineProps<{
  aktivzitierung?: AktivzitierungUli;
  showCancelButton: boolean;
  showDeleteButton: boolean;
  showSearchButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungUli];
  delete: [id: string];
  cancel: [void];
  search: [params: Record<string, unknown>];
}>();

function createInitial(): AktivzitierungUli {
  return {
    id: crypto.randomUUID(),
    periodikum: undefined,
    zitatstelle: undefined,
    verfasser: [],
    dokumenttypen: [],
    documentNumber: undefined,
  };
}

const aktivzitierungUliRef = ref<AktivzitierungUli>(
  props.aktivzitierung ? { ...props.aktivzitierung } : createInitial(),
);

const verfasser = computed<string[]>({
  get: () => aktivzitierungUliRef.value.verfasser ?? [],
  set: (val) => {
    aktivzitierungUliRef.value.verfasser = val;
  },
});

const periodikum = computed<Periodikum | undefined>({
  get: () => {
    const value = aktivzitierungUliRef.value.periodikum;
    return value
      ? ({
          id: value,
          abbreviation: value,
        } as Periodikum)
      : undefined;
  },
  set: (val) => {
    aktivzitierungUliRef.value.periodikum = val?.abbreviation;
  },
});

const dokumenttypen = computed<DocumentType[] | undefined>({
  get: () => aktivzitierungUliRef.value.dokumenttypen || [],
  set: (val) => {
    aktivzitierungUliRef.value.dokumenttypen = val;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungUli | undefined) => {
    if (newVal) {
      aktivzitierungUliRef.value = { ...newVal };
    }
  },
);

const isEmpty = computed(() => isAktivzitierungEmpty(aktivzitierungUliRef.value));
const isExistingEntry = computed(() => !!props.aktivzitierung?.id);

// Validation ---------------------------------------------------------------

type UliField = "periodikum" | "zitatstelle" | "verfasser";
const validationStore = useValidationStore<UliField>();

const periodikumFieldRef = ref<HTMLElement | null>(null);
const zitatstelleFieldRef = ref<HTMLElement | null>(null);
const verfasserFieldRef = ref<HTMLElement | null>(null);

const { hasValidationErrors } = useSubmitValidation([
  () => validationStore.getByField("periodikum")?.message,
  () => validationStore.getByField("zitatstelle")?.message,
  () => validationStore.getByField("verfasser")?.message,
]);

const MANDATORY_MESSAGE = "Pflichtfeld nicht befüllt";

function validate(): boolean {
  const missingPeriodikum = !aktivzitierungUliRef.value.periodikum?.trim();
  const missingZitatstelle = !aktivzitierungUliRef.value.zitatstelle?.trim();
  const missingVerfasser = !aktivzitierungUliRef.value.verfasser?.length;

  if (missingPeriodikum) validationStore.add(MANDATORY_MESSAGE, "periodikum");
  if (missingZitatstelle) validationStore.add(MANDATORY_MESSAGE, "zitatstelle");
  if (missingVerfasser) validationStore.add(MANDATORY_MESSAGE, "verfasser");

  if (missingPeriodikum || missingZitatstelle || missingVerfasser) {
    if (missingPeriodikum) useScrollToElement(periodikumFieldRef);
    else if (missingZitatstelle) useScrollToElement(zitatstelleFieldRef);
    else useScrollToElement(verfasserFieldRef);
    return false;
  }

  return true;
}

watch(
  () => aktivzitierungUliRef.value.periodikum,
  (val) => {
    if (val?.trim()) validationStore.remove("periodikum");
  },
);

watch(
  () => aktivzitierungUliRef.value.zitatstelle,
  (val) => {
    if (val?.trim()) validationStore.remove("zitatstelle");
  },
);

watch(
  () => aktivzitierungUliRef.value.verfasser,
  (val) => {
    if (val && val.length > 0) validationStore.remove("verfasser");
  },
);

// Handlers -----------------------------------------------------------------

function onClickSave() {
  if (!validate()) return;
  if (hasValidationErrors.value) return;

  emit("save", aktivzitierungUliRef.value);
  if (!isExistingEntry.value) {
    aktivzitierungUliRef.value = createInitial();
  }
}

function onClickCancel() {
  emit("cancel");
}

function onClickDelete() {
  emit("delete", aktivzitierungUliRef.value.id);
}

function onClickSearch() {
  emit("search", aktivzitierungUliRef.value);
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <div class="flex flex-row gap-24">
        <InputField
          id="periodikum"
          ref="periodikumFieldRef"
          label="Periodikum *"
          v-slot="slotProps"
          :validation-error="validationStore.getByField('periodikum')"
        >
          <PeriodikumDropDown
            :input-id="slotProps.id"
            v-model="periodikum"
            :invalid="slotProps.hasError"
          />
        </InputField>

        <InputField
          id="zitatstelle"
          ref="zitatstelleFieldRef"
          label="Zitatstelle *"
          v-slot="slotProps"
          :validation-error="validationStore.getByField('zitatstelle')"
        >
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungUliRef.zitatstelle"
            aria-label="Zitatstelle"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>
      </div>

      <InputField
        id="verfasser"
        ref="verfasserFieldRef"
        label="Verfasser/in *"
        v-slot="slotProps"
        :validation-error="validationStore.getByField('verfasser')"
      >
        <RisChipsInput
          :input-id="slotProps.id"
          v-model="verfasser"
          aria-label="Verfasser/in"
          :has-error="slotProps.hasError"
        />
      </InputField>

      <div class="flex flex-row gap-24">
        <InputField id="documentNumber" v-slot="slotProps" label="Dokumentnummer">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungUliRef.documentNumber"
            aria-label="Dokumentnummer"
            :invalid="slotProps.hasError"
            fluid
          />
        </InputField>

        <InputField id="docType" label="Dokumenttyp" v-slot="slotProps">
          <DokumentTyp
            :input-id="slotProps.id"
            v-model="dokumenttypen"
            aria-label="Dokumenttyp"
            :document-category="DocumentCategory.LITERATUR_UNSELBSTAENDIG"
            :invalid="slotProps.hasError"
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
