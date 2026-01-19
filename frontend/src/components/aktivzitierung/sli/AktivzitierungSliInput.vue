<script lang="ts" setup>
import InputField from "@/components/input/InputField.vue";
import InputText from "primevue/inputtext";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import { DocumentCategory } from "@/domain/documentType";
import DokumentTyp from "@/views/literature/DokumentTyp.vue";
import { RisChipsInput } from "@digitalservicebund/ris-ui/components";
import { computed, ref, watch } from "vue";
import { Button } from "primevue";
import { isAktivzitierungEmpty } from "@/utils/validators";

const props = defineProps<{
  aktivzitierung?: AktivzitierungSli;
  showCancelButton: boolean;
  showDeleteButton: boolean;
}>();

const emit = defineEmits<{
  save: [aktivzitierung: AktivzitierungSli];
  delete: [id: string];
  cancel: [void];
  search: [params: Record<string, unknown>];
}>();

function createInitialT() {
  return { id: crypto.randomUUID() } as AktivzitierungSli;
}

const aktivzitierungSliRef = ref<AktivzitierungSli>(
  props.aktivzitierung
    ? { ...props.aktivzitierung } // Use a copy of the prop data
    : createInitialT(),
);

// Ensure verfasser is always an array - use computed to provide default
const verfasser = computed({
  get: () => aktivzitierungSliRef.value.verfasser || [],
  set: (val: string[]) => {
    aktivzitierungSliRef.value.verfasser = val;
  },
});

// Ensure dokumenttypen is always an array
const dokumenttypen = computed({
  get: () => aktivzitierungSliRef.value.dokumenttypen || [],
  set: (val) => {
    aktivzitierungSliRef.value.dokumenttypen = val;
  },
});

watch(
  () => props.aktivzitierung,
  (newVal: AktivzitierungSli | undefined) => {
    if (newVal) {
      aktivzitierungSliRef.value = { ...newVal };
    }
  },
);

const isEmpty = computed(() => isAktivzitierungEmpty(aktivzitierungSliRef.value));

const isExistingEntry = computed(() => !!props.aktivzitierung?.id);

function onClickSave() {
  emit("save", aktivzitierungSliRef.value);
  if (!isExistingEntry.value) {
    aktivzitierungSliRef.value = createInitialT();
  }
}

function onClickCancel() {
  emit("cancel");
}

function onClickDelete() {
  emit("delete", aktivzitierungSliRef.value.id);
}

function onClickSearch() {
  emit("search", aktivzitierungSliRef.value);
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
          v-model="aktivzitierungSliRef.titel"
          aria-label="Hauptsachtitel / Dokumentarischer Titel"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>

      <div class="flex flex-row gap-24">
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungSliRef.veroeffentlichungsJahr"
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
