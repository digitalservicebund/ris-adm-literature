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
  set: (newValue) => {
    console.log(newValue)
    store.documentUnit!.normgebers = newValue
  },
})

const onClickAddNormgeber = () => {
  normgebers.value = [...normgebers.value, createEmptyNormgeber()]
}

const onAddNormgeber = (normgeber: Normgeber | undefined) => {
  if (
    normgeber &&
    normgeber.institution &&
    // only add if not yet existing
    !normgebers.value.find(
      (n) => n.institution && n.institution.label === normgeber.institution!.label,
    )
  ) {
    const index = normgebers.value.findIndex((normgeber) => !normgeber.institution)
    normgebers.value[index] = normgeber
  }
}

const onUpdateNormgeber = (normgeber: Normgeber | undefined) => {
  console.log(normgeber)
  if (normgeber) {
    const index = normgebers.value.findIndex(
      (n) => n.institution?.label === normgeber.institution?.label,
    )
    normgebers.value[index] = normgeber
  }
}

const onRemoveNormgeber = (label: string) => {
  normgebers.value = normgebers.value.filter((a) => a.institution?.label !== label)
}

const onRemoveEmptyNormgeber = () => {
  normgebers.value = normgebers.value.filter((a) => a.institution && a.institution.label)
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
        v-for="normgeber in normgebers"
        :key="normgeber.institution?.label"
      >
        <NormgeberListItem
          :normgeber="normgeber"
          @add-normgeber="onAddNormgeber"
          @update-normgeber="onUpdateNormgeber"
          @remove-normgeber="onRemoveNormgeber"
          @remove-empty-normgeber="onRemoveEmptyNormgeber"
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
