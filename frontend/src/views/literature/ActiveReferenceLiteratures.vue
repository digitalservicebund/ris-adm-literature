<script lang="ts" setup>
import { computed } from 'vue'
import ActiveReferenceLiteratureInput from './ActiveReferenceLiteratureInput.vue'
import ActiveReferenceLiteratureSummary from './ActiveReferenceLiteratureSummary.vue'
import EditableList from '@/views/adm/documentUnit/[documentNumber]/rubriken/components/EditableList.vue'
import ActiveReferenceLiterature from '@/domain/activeReferenceLiterature'
import { useSliDocumentUnitStore } from '@/stores/sliDocStore'

const store = useSliDocumentUnitStore()

const activeReferenceLiteratures = computed({
  get: () => store.documentUnit!.activeReferenceLiteratures ?? [],
  set: (newValues: ActiveReferenceLiterature[]) => {
    store.documentUnit!.activeReferenceLiteratures = newValues
  },
})
console.log(activeReferenceLiteratures.value)
const defaultValue = new ActiveReferenceLiterature() as ActiveReferenceLiterature
</script>

<template>
  <div aria-label="Aktivzitierung">
    <EditableList
      v-model="activeReferenceLiteratures"
      :default-value="defaultValue"
      :edit-component="ActiveReferenceLiteratureInput"
      :summary-component="ActiveReferenceLiteratureSummary"
    />
  </div>
</template>
