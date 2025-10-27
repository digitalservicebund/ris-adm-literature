<script lang="ts" setup>
import { ref, toRaw } from 'vue'
import IconBadge from '@/components/IconBadge.vue'
import { useSaveToRemote } from '@/composables/useSaveToRemote'
import Button from 'primevue/button'
import { useStatusBadge } from '@/composables/useStatusBadge'
import { PublicationState } from '@/domain/publicationStatus'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import type { DocumentUnitStore } from '@/stores/types'

interface Props {
  heading?: string
  hideSaveButton?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  heading: '',
  hideSaveButton: false,
})

const store = useStoreForRoute<DocumentUnitStore>()

const statusBadge = ref(
  useStatusBadge({
    publicationStatus: PublicationState.UNPUBLISHED,
  }).value,
)

const formattedInfo = ''

const { saveIsInProgress, triggerSave, lastSaveError, formattedLastSavedOn } =
  useSaveToRemote(store)

const getErrorDetails = () => (lastSaveError.value?.title ? ': ' + lastSaveError.value.title : '')
</script>

<template>
  <div
    class="sticky top-0 z-30 flex h-[64px] flex-row items-center border-b border-solid border-gray-400 bg-blue-100 px-24 py-12"
  >
    <h1 class="text font-bold">{{ props.heading }}</h1>
    <span v-if="formattedInfo.length > 0" class="m-4"> | </span>
    <span
      class="overflow-hidden text-ellipsis whitespace-nowrap"
      data-testid="document-unit-info-panel-items"
    >
      {{ formattedInfo }}</span
    >
    <IconBadge
      :background-color="statusBadge.backgroundColor"
      class="ml-12"
      :color="statusBadge.color"
      :icon="toRaw(statusBadge.icon)"
      :label="statusBadge.label"
    />

    <span class="flex-grow"></span>
    <div class="ml-12 flex items-center space-x-[12px] whitespace-nowrap">
      <p v-if="lastSaveError !== undefined" class="ris-label1-regular text-red-800">
        Fehler beim Speichern{{ getErrorDetails() }}
      </p>
      <p v-else-if="saveIsInProgress === true" class="ris-label1-regular">speichern...</p>
      <p v-else-if="formattedLastSavedOn != undefined" class="ris-label1-regular">
        Gespeichert:
        <span>{{ formattedLastSavedOn }}</span>
        Uhr
      </p>
      <Button
        v-if="!hideSaveButton"
        data-testid="save-button"
        label="Speichern"
        size="small"
        @click="triggerSave"
      />
    </div>
  </div>
</template>
