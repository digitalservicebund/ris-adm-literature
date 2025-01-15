<script lang="ts" setup>
import DocumentUnitReferenceInput from '@/components/periodical/DocumentUnitReferenceInput.vue'
import Reference from '@/domain/reference.ts'
import ReferenceSummary from '@/components/periodical/ReferenceSummary.vue'
import TitleElement from '@/components/TitleElement.vue'
import EditableList from '@/components/EditableList.vue'

const model = defineModel({ default: [] })
const defaultValue = new Reference() as Reference
</script>

<template>
  <div class="flex w-full flex-1 grow flex-col p-24">
    <div aria-label="Fundstellen" class="flex flex-col gap-24 bg-white p-24">
      <TitleElement>Fundstellen</TitleElement>
      <div class="flex flex-row">
        <EditableList
          v-model="model"
          :default-value="defaultValue"
          :edit-component="DocumentUnitReferenceInput"
          :summary-component="ReferenceSummary"
        />
      </div>
    </div>
  </div>
</template>
