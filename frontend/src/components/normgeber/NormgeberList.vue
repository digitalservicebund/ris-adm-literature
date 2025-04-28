<script lang="ts" setup>
import Button from 'primevue/button'
import IconAdd from '~icons/material-symbols/add'
import { createEmptyNormgeber, type Normgeber } from '@/domain/normgeber'
import NormgeberListItem from './NormgeberListItem.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { computed } from 'vue'

const store = useDocumentUnitStore()

const normgebers = computed({
  get: () => store.documentUnit!.normgebers ?? [],
  set: (newValue) => (store.documentUnit!.normgebers = newValue),
})

const onClickAddNormgeber = () => {
  normgebers.value = [...normgebers.value, createEmptyNormgeber()]
}

const onUpdateNormgeber = (updated: Normgeber | undefined) => {
  if (updated) {
    const index = normgebers.value.findIndex((a) => a.id === updated.id)
    normgebers.value[index] = updated
  }
}

const onRemoveNormgeber = (id: string) => {
  normgebers.value = normgebers.value.filter((a) => a.id !== id)
}

const buttonLabel = computed(() =>
  normgebers.value.length > 0 ? 'Weitere Angabe' : 'Normgeber hinzufügen',
)
</script>

<template>
  <div class="normgebers">
    <h2 class="ris-label1-bold mb-16">Normgeber</h2>
    <ol v-if="normgebers.length > 0">
      <li
        class="border-b-1 border-blue-300 py-16"
        v-for="(normgeber, index) in normgebers"
        :key="normgeber.id"
      >
        <NormgeberListItem
          :normgeber="normgebers[index]"
          @update-normgeber="onUpdateNormgeber"
          @remove-normgeber="onRemoveNormgeber"
        />
      </li>
    </ol>
    <Button
      class="mt-16"
      :aria-label="buttonLabel"
      severity="secondary"
      :label="buttonLabel"
      size="small"
      @click="onClickAddNormgeber"
    >
      <template #icon><IconAdd /></template>
    </Button>
  </div>
</template>
