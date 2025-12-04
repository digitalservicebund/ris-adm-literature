<script lang="ts" setup>
import { computed } from 'vue'
import IconEdit from '~icons/ic/outline-edit'
import IconBaselineDescription from '~icons/ic/outline-class'
import IconFilledDescription from '~icons/ic/class'
import IconClose from '~icons/ic/close'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature.ts'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'

const props = defineProps<{
  aktivzitierungLiterature: AktivzitierungLiterature
  isEditing?: boolean
}>()

const emit = defineEmits<{
  updateAktivzitierungLiterature: [aktivzitierungLiterature: AktivzitierungLiterature]
  deleteAktivzitierungLiterature: [id: string]
  editStart: [id: string]
  cancelEdit: [void]
}>()

const isFromSearch = computed(
  () => !!props.aktivzitierungLiterature.documentNumber, // search-based if documentNumber set
)

const onExpandAccordion = () => {
  emit('editStart', props.aktivzitierungLiterature.id)
}

const onUpdateAktivzitierungLiterature = (aktivzitierungLiterature: AktivzitierungLiterature) => {
  emit('updateAktivzitierungLiterature', aktivzitierungLiterature)
}

const onClickCancel = () => {
  emit('cancelEdit')
}

const onDeleteAktivzitierungLiterature = (id: string) => {
  emit('deleteAktivzitierungLiterature', id)
  emit('cancelEdit')
}

const onDeleteFromSummary = () => {
  emit('deleteAktivzitierungLiterature', props.aktivzitierungLiterature.id)
}

const metaSummary = computed(() => {
  const parts: string[] = []

  if (props.aktivzitierungLiterature.veroeffentlichungsjahr) {
    parts.push(props.aktivzitierungLiterature.veroeffentlichungsjahr)
  }

  if (
    props.aktivzitierungLiterature.verfasser &&
    props.aktivzitierungLiterature.verfasser.length > 0
  ) {
    const authors = props.aktivzitierungLiterature.verfasser
      .map((author) => author.trim().replace(/,$/, ''))
      .filter((author) => author.length > 0)
      .join(', ')
    if (authors) {
      parts.push(authors)
    }
  }

  const mainParts = parts.join(', ')

  if (
    props.aktivzitierungLiterature.dokumenttypen &&
    props.aktivzitierungLiterature.dokumenttypen.length > 0
  ) {
    const abbreviations = props.aktivzitierungLiterature.dokumenttypen
      .map((dt) => dt.abbreviation)
      .filter(Boolean)
      .join(', ')
    if (abbreviations) {
      return `${mainParts} (${abbreviations})`
    }
  }

  return mainParts
})

const titleSummary = computed(() => {
  return props.aktivzitierungLiterature.titel || ''
})
</script>

<template>
  <AktivzitierungLiteratureInput
    v-if="isEditing && !isFromSearch"
    :aktivzitierung-literature="aktivzitierungLiterature"
    @update-aktivzitierung-literature="onUpdateAktivzitierungLiterature"
    @delete-aktivzitierung-literature="onDeleteAktivzitierungLiterature"
    @cancel="onClickCancel"
    show-cancel-button
  />
  <div v-else class="flex w-full items-center gap-10">
    <IconBaselineDescription v-if="!isFromSearch" class="text-neutral-800" />
    <IconFilledDescription v-if="isFromSearch" class="text-neutral-800" />

    <div class="flex flex-col gap-2">
      <div class="ris-body1-regular">
        {{ metaSummary }}
      </div>
      <div class="ris-body2-regular text-gray-900">
        {{ titleSummary }}
      </div>
    </div>
    <div class="ml-auto flex items-center gap-8">
      <button
        v-if="!isFromSearch"
        v-tooltip.bottom="'Eintrag bearbeiten'"
        aria-label="Eintrag bearbeiten"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer ml-auto"
        @click="onExpandAccordion"
      >
        <IconEdit />
      </button>
      <button
        v-else
        v-tooltip.bottom="'Eintrag löschen'"
        aria-label="Eintrag löschen"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer"
        @click="onDeleteFromSummary"
      >
        <IconClose />
      </button>
    </div>
  </div>
</template>
