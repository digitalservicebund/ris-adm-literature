<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import InputField from '@/components/input/InputField.vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature.ts'
import { DocumentCategory } from '@/domain/documentType'
import DokumentTyp from '@/views/literature/DokumentTyp.vue'
import { RisChipsInput } from '@digitalservicebund/ris-ui/components'
import type { SliDocUnitSearchParams } from '@/domain/sli/sliDocumentUnit'

const props = defineProps<{
  aktivzitierungLiterature?: AktivzitierungLiterature
  showCancelButton: boolean
}>()

const emit = defineEmits<{
  updateAktivzitierungLiterature: [aktivzitierungLiterature: AktivzitierungLiterature]
  deleteAktivzitierungLiterature: [id: string]
  cancel: [void]
  search: [searchParams: SliDocUnitSearchParams]
}>()

const initialUuid = props.aktivzitierungLiterature?.uuid ?? crypto.randomUUID()

const aktivzitierungLiterature = ref<AktivzitierungLiterature>({
  id: props.aktivzitierungLiterature?.id ?? initialUuid,
  uuid: initialUuid,
  titel: props.aktivzitierungLiterature?.titel || '',
  veroeffentlichungsjahr: props.aktivzitierungLiterature?.veroeffentlichungsjahr || '',
  dokumenttypen: props.aktivzitierungLiterature?.dokumenttypen || [],
  verfasser: props.aktivzitierungLiterature?.verfasser || [],
})

const isEmpty = computed(() => {
  const fields = ['titel', 'veroeffentlichungsjahr', 'dokumenttypen', 'verfasser']
  return fields.every((field) => {
    const value = aktivzitierungLiterature.value[field as keyof AktivzitierungLiterature]
    if (value === undefined || value === null) return true
    if (typeof value === 'string') return value.trim() === ''
    if (Array.isArray(value)) return value.length === 0
    return false
  })
})

const isExistingEntry = computed(() => !!props.aktivzitierungLiterature?.uuid)

function onClickSave() {
  emit('updateAktivzitierungLiterature', { ...aktivzitierungLiterature.value })
}

function onClickCancel() {
  emit('cancel')
}

function onClickDelete() {
  if (props.aktivzitierungLiterature?.uuid) {
    emit('deleteAktivzitierungLiterature', props.aktivzitierungLiterature.id)
  }
}

function onClickSearch() {
  emit('search', {
    veroeffentlichungsjahr: aktivzitierungLiterature.value.veroeffentlichungsjahr,
    titel: aktivzitierungLiterature.value.titel,
    dokumenttypen: aktivzitierungLiterature.value.dokumenttypen?.map(
      (docType) => docType.abbreviation,
    ),
    verfasser: aktivzitierungLiterature.value.verfasser,
  })
}

watch(
  () => props.aktivzitierungLiterature,
  (newVal) => {
    if (newVal) {
      aktivzitierungLiterature.value = {
        id: newVal.id || crypto.randomUUID(),
        uuid: newVal.uuid || crypto.randomUUID(),
        titel: newVal.titel || '',
        veroeffentlichungsjahr: newVal.veroeffentlichungsjahr || '',
        dokumenttypen: newVal.dokumenttypen || [],
        verfasser: newVal.verfasser || [],
      }
    }
  },
  { immediate: true },
)
</script>

<template>
  <div>
    <div class="flex flex-col gap-24">
      <InputField id="titel" v-slot="slotProps" label="Hauptsachtitel / Dokumentarischer Titel *">
        <InputText
          :id="slotProps.id"
          v-model="aktivzitierungLiterature.titel"
          aria-label="Hauptsachtitel / Dokumentarischer Titel"
          :invalid="slotProps.hasError"
          fluid
        />
      </InputField>
      <div class="flex flex-row gap-24">
        <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr *">
          <InputText
            :id="slotProps.id"
            v-model="aktivzitierungLiterature.veroeffentlichungsjahr"
            aria-label="Veröffentlichungsjahr"
            fluid
          />
        </InputField>

        <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp *">
          <DokumentTyp
            :input-id="slotProps.id"
            v-model="aktivzitierungLiterature.dokumenttypen"
            aria-label="Dokumenttyp"
            :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
            :invalid="slotProps.hasError"
          />
        </InputField>
      </div>

      <InputField id="verfasser" v-slot="slotProps" label="Verfasser/in *">
        <RisChipsInput
          :input-id="slotProps.id"
          v-model="aktivzitierungLiterature.verfasser!"
          aria-label="Verfasser/in"
          :invalid="slotProps.hasError"
        />
      </InputField>
    </div>
    <div class="flex w-full gap-16 mt-16">
      <Button
        aria-label="Selbständige Literatur suchen"
        label="Suchen"
        size="small"
        @click.stop="onClickSearch"
      />
      <Button
        :disabled="isEmpty"
        aria-label="Aktivzitierung übernehmen"
        label="Übernehmen"
        size="small"
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
        v-if="isExistingEntry"
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
