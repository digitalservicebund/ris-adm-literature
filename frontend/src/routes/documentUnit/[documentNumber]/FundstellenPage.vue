<script lang="ts" setup>
import { computed } from 'vue'
import DocumentUnitReferenceInput from '@/components/periodical/DocumentUnitReferenceInput.vue'
import Reference from '@/domain/reference.ts'
import ReferenceSummary from '@/components/periodical/ReferenceSummary.vue'
import TitleElement from '@/components/TitleElement.vue'
import EditableList from '@/components/EditableList.vue'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'

const store = useDocumentUnitStore()

const references = computed({
  get: () => store.documentUnit!.references as Reference[],
  set: (newValues) => {
    store.documentUnit!.references = newValues
  },
})

const defaultValue = new Reference() as Reference
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Fundstellen" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Fundstellen</TitleElement>
      <div class="flex flex-row">
        <EditableList
          v-model="references"
          :default-value="defaultValue"
          :edit-component="DocumentUnitReferenceInput"
          :summary-component="ReferenceSummary"
        />
      </div>
    </div>
  </div>
</template>
