<script lang="ts" setup>
import { type Normgeber } from '@/domain/normgeber'
import NormgeberListItem from './NormgeberListItem.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { computed, ref } from 'vue'
import NormgeberInput from './NormgeberInput.vue'
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'

const store = useDocumentUnitStore()

const normgebers = computed({
  get: () => store.documentUnit!.normgebers ?? [],
  set: (newValue) => {
    store.documentUnit!.normgebers = newValue
  },
})

const onAddNormgeber = (newNormgeber: Normgeber) => {
  normgebers.value = [...normgebers.value, newNormgeber]
  isCreationPanelOpened.value = false
}

const onUpdateNormgeber = (normgeber: Normgeber) => {
  const index = normgebers.value.findIndex((n) => n.id === normgeber.id)
  normgebers.value[index] = normgeber
}

const onRemoveNormgeber = (id: string) => {
  normgebers.value = normgebers.value.filter((n) => n.id !== id)
}

const isCreationPanelOpened = ref(false)
</script>

<template>
  <div class="normgebers">
    <h2 class="ris-label1-bold mb-16">Normgeber</h2>
    <ol v-if="normgebers.length > 0">
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="normgeber in normgebers"
        :key="normgeber.id"
      >
        <NormgeberListItem
          :normgeber="normgeber"
          @add-normgeber="onAddNormgeber"
          @update-normgeber="onUpdateNormgeber"
          @delete-normgeber="onRemoveNormgeber"
        />
      </li>
    </ol>
    <NormgeberInput
      v-if="isCreationPanelOpened || normgebers.length === 0"
      class="mt-16"
      @update-normgeber="onAddNormgeber"
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
