<script lang="ts" setup>
import { computed, ref } from 'vue'
import ToolTip from '@/components/ToolTip.vue'
import IconArrowDown from '~icons/ic/baseline-keyboard-arrow-down'
import type { AktivzitierungLiterature } from '@/domain/AktivzitierungLiterature.ts'
import AktivzitierungLiteratureInput from './AktivzitierungLiteratureInput.vue'

const props = defineProps<{
  aktivzitierungLiterature: AktivzitierungLiterature
}>()

const emit = defineEmits<{
  updateAktivzitierungLiterature: [aktivzitierungLiterature: AktivzitierungLiterature]
  deleteAktivzitierungLiterature: [id: string]
}>()

const isEditMode = ref<boolean>(false)

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const onExpandAccordion = () => {
  toggleEditMode()
}

const onUpdateAktivzitierungLiterature = (aktivzitierungLiterature: AktivzitierungLiterature) => {
  emit('updateAktivzitierungLiterature', aktivzitierungLiterature)
  isEditMode.value = false
}

const onClickCancel = () => {
  toggleEditMode()
}

const onDeleteAktivzitierungLiterature = (id: string) => {
  emit('deleteAktivzitierungLiterature', id)
  toggleEditMode()
}

const renderSummary = computed(() => {
  const parts: string[] = []

  if (props.aktivzitierungLiterature.veroeffentlichungsjahr) {
    parts.push(props.aktivzitierungLiterature.veroeffentlichungsjahr)
  }

  if (
    props.aktivzitierungLiterature.verfasser &&
    props.aktivzitierungLiterature.verfasser.length > 0
  ) {
    parts.push(props.aktivzitierungLiterature.verfasser.join('/ '))
  }

  if (
    props.aktivzitierungLiterature.dokumenttypen &&
    props.aktivzitierungLiterature.dokumenttypen.length > 0
  ) {
    const abbreviations = props.aktivzitierungLiterature.dokumenttypen
      .map((dt) => dt.abbreviation)
      .filter(Boolean)
      .join(', ')
    if (abbreviations) {
      parts.push(`(${abbreviations})`)
    }
  }

  const title = props.aktivzitierungLiterature.hauptsachtitel
  if (title) {
    parts.push(title)
  }

  return parts.join(', ')
})
</script>

<template>
  <AktivzitierungLiteratureInput
    v-if="isEditMode"
    :aktivzitierung-literature="aktivzitierungLiterature"
    @update-aktivzitierung-literature="onUpdateAktivzitierungLiterature"
    @delete-aktivzitierung-literature="onDeleteAktivzitierungLiterature"
    @cancel="onClickCancel"
    show-cancel-button
  />
  <div v-else class="flex w-full items-center">
    <div class="ris-label1-regular">
      {{ renderSummary }}
    </div>
    <ToolTip class="ml-auto" text="Aufklappen">
      <button
        aria-label="Aktivzitierung Editieren"
        class="flex h-32 w-32 items-center justify-center text-blue-800 hover:bg-blue-100 focus:shadow-[inset_0_0_0_0.125rem] focus:shadow-blue-800 focus:outline-none cursor-pointer"
        @click="onExpandAccordion"
      >
        <IconArrowDown />
      </button>
    </ToolTip>
  </div>
</template>
