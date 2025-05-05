<script lang="ts" setup>
import NormgeberListItem from './NormgeberListItem.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { computed } from 'vue'
import NormgeberInput from './NormgeberInput.vue'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import { useEditableList } from '@/composables/useEditableList'

const store = useDocumentUnitStore()

const normgebers = computed({
  get: () => store.documentUnit!.normgebers ?? [],
  set: (newValue) => {
    store.documentUnit!.normgebers = newValue
  },
})

const { onRemoveItem, onAddItem, onUpdateItem, isCreationPanelOpened } = useEditableList(normgebers)
</script>

<template>
  <div class="normgebers">
    <h2 class="ris-label1-bold mb-16">Normgeber</h2>
    <ol v-if="normgebers.length > 0" aria-label="Normgeber Liste">
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="normgeber in normgebers"
        :key="normgeber.id"
      >
        <NormgeberListItem
          :normgeber="normgeber"
          @add-normgeber="onAddItem"
          @update-normgeber="onUpdateItem"
          @delete-normgeber="onRemoveItem"
        />
      </li>
    </ol>
    <NormgeberInput
      v-if="isCreationPanelOpened || normgebers.length === 0"
      class="mt-16"
      @update-normgeber="onAddItem"
      @cancel="isCreationPanelOpened = false"
      :show-cancel-button="normgebers.length > 0"
    />
    <Button
      v-else
      class="mt-16"
      aria-label="Normgeber hinzufügen"
      severity="secondary"
      label="Normgeber hinzufügen"
      size="small"
      @click="isCreationPanelOpened = true"
    >
      <template #icon><IconAdd /></template>
    </Button>
  </div>
</template>
