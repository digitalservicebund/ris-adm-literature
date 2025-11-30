<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import InputField from '@/components/input/InputField.vue'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import ActiveCitationLiterature from '@/domain/activeReferenceLiterature'
import { DocumentCategory } from '@/domain/documentType'
import DokumentTyp from '@/views/literature/DokumentTyp.vue'
import { RisChipsInput } from '@digitalservicebund/ris-ui/components'

const props = defineProps<{
  modelValue?: ActiveCitationLiterature
  modelValueList?: ActiveCitationLiterature[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: ActiveCitationLiterature]
  addEntry: [void]
  cancelEdit: [void]
  removeEntry: [void]
}>()

const lastSavedModelValue = ref(new ActiveCitationLiterature({ ...props.modelValue }))
const activeCitation = ref(new ActiveCitationLiterature({ ...props.modelValue }))

const hauptsachtitel = computed({
  get: () => activeCitation.value.hauptsachtitel,
  set: (value: string | undefined) => {
    activeCitation.value.hauptsachtitel = value
  },
})

const verfasser = computed({
  get: () => activeCitation.value.verfasser ?? [],
  set: (value: string[]) => {
    activeCitation.value.verfasser = value
  },
})

async function addActiveCitation() {
  emit('update:modelValue', activeCitation.value as ActiveCitationLiterature)
  emit('addEntry')
  // Clear fields after adding
  activeCitation.value = new ActiveCitationLiterature()
}

function cancelEdit() {
  emit('cancelEdit')
}

function removeEntry() {
  emit('removeEntry')
}

watch(
  () => props.modelValue,
  () => {
    activeCitation.value = new ActiveCitationLiterature({ ...props.modelValue })
    lastSavedModelValue.value = new ActiveCitationLiterature({ ...props.modelValue })
  },
)
</script>

<template>
  <div class="flex flex-col gap-24">
    <InputField id="hauptsachtitel" v-slot="slotProps" label="Hauptsachtitel *">
      <InputText
        :id="slotProps.id"
        v-model="hauptsachtitel"
        aria-label="Hauptsachtitel"
        :invalid="slotProps.hasError"
        fluid
      />
    </InputField>
    <div class="flex flex-row gap-24">
      <InputField id="veroeffentlichungsjahr" v-slot="slotProps" label="Veröffentlichungsjahr *">
        <InputText
          :id="slotProps.id"
          v-model="activeCitation.veroeffentlichungsjahr"
          aria-label="Veröffentlichungsjahr"
          fluid
        />
      </InputField>

      <InputField id="dokumenttypen" v-slot="slotProps" label="Dokumenttyp *">
        <DokumentTyp
          :input-id="slotProps.id"
          v-model="activeCitation.dokumenttypen"
          aria-label="Dokumenttyp"
          :document-category="DocumentCategory.LITERATUR_SELBSTAENDIG"
          :invalid="slotProps.hasError"
        />
      </InputField>
    </div>

    <InputField id="verfasser" v-slot="slotProps" label="Verfasser/in *">
      <RisChipsInput
        :input-id="slotProps.id"
        v-model="verfasser"
        aria-label="Verfasser/in"
        :invalid="slotProps.hasError"
      />
    </InputField>

    <div class="flex w-full flex-row justify-between">
      <div>
        <div class="flex gap-16">
          <Button
            :disabled="activeCitation.isEmpty"
            label="Übernehmen"
            size="small"
            aria-label="Aktivzitierung speichern"
            severity="secondary"
            @click.stop="addActiveCitation"
          />
          <Button
            v-if="!lastSavedModelValue.isEmpty"
            aria-label="Abbrechen"
            label="Abbrechen"
            size="small"
            text
            @click.stop="cancelEdit"
          />
        </div>
      </div>
      <Button
        v-if="!lastSavedModelValue.isEmpty"
        aria-label="Eintrag löschen"
        label="Eintrag löschen"
        size="small"
        severity="danger"
        @click.stop="removeEntry"
      />
    </div>
  </div>
</template>
