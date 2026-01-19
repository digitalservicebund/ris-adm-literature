<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import InputText from "primevue/inputtext";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import { DocumentCategory } from "@/domain/documentType";
import DokumentTyp from "@/views/literature/DokumentTyp.vue";
import { RisChipsInput } from "@digitalservicebund/ris-ui/components";
import type { SliDocUnitSearchParams } from "@/domain/sli/sliDocumentUnit";
import { computed, ref, watch } from "vue";
import { Button } from "primevue";

const props = defineProps<{
  aktivzitierung?: AktivzitierungSli;
  showCancelButton: boolean;
  showDeleteButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungSli];
  delete: [id: string];
  cancel: [void];
  search: [params: SliDocUnitSearchParams];
}>();

function createInitialT() {
  return { id: crypto.randomUUID() } as AktivzitierungSli;
}

const aktivzitierungRef = ref<AktivzitierungSli>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitialT(),
);

// Ensure verfasser is always an array - use computed to provide default
const verfasser = computed({
  get: () => aktivzitierungRef.value.verfasser || [],
  set: (val: string[]) => {
    aktivzitierungRef.value.verfasser = val;
  },
});

// Ensure dokumenttypen is always an array
const dokumenttypen = computed({
  get: () => aktivzitierungRef.value.dokumenttypen || [],
  set: (val) => {
    aktivzitierungRef.value.dokumenttypen = val;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungSli | undefined) => {
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
      <InputField id="verfasser" v-slot="slotProps" label="Verfasser/in">
        <RisChipsInput
          :input-id="slotProps.id"
          v-model="verfasser"
          aria-label="Verfasser/in"
          :invalid="slotProps.hasError"
        />
      </InputField>

      <InputField id="titel" v-slot="slotProps" label="Hauptsachtitel / Dokumentarischer Titel">
        <InputText
          :id="slotProps.id"
          v-model="aktivzitierungRef.titel"
          aria-label="Hauptsachtitel / Dokumentarischer Titel"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>

      <div class="flex flex-row gap-24">
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungRef.veroeffentlichungsJahr"
            aria-label="Veröffentlichungsjahr"
            fluid
          />
        </InputField>

        <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp">
          <DokumentTyp
            :input-id="slotProps.id"
            v-model="dokumenttypen"
            aria-label="Dokumenttyp"
            :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
            :invalid="slotProps.hasError"
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
