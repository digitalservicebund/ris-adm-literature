<script lang="ts" setup>
import { computed } from 'vue'
import ActiveCitationInput from '@/components/ActiveCitationInput.vue'
import ActiveCitationSummary from '@/components/ActiveCitationSummary.vue'
import EditableList from '@/components/EditableList.vue'
import ActiveCitation from '@/domain/activeCitation'

const actualActiveCitations = defineModel<ActiveCitation[]>({ default: [] })
const activeCitations = computed({
  get: () => actualActiveCitations.value,
  set: async (newValues) => {
    actualActiveCitations.value = newValues
  },
})

const defaultValue = new ActiveCitation() as ActiveCitation
</script>

<template>
  <div aria-label="Aktivzitierung">
    <h2 id="activeCitations" class="ds-label-01-bold mb-16">Aktivzitierung Rechtsprechung</h2>
    <EditableList
      v-model="activeCitations"
      :default-value="defaultValue"
      :edit-component="ActiveCitationInput"
      :summary-component="ActiveCitationSummary"
    />
  </div>
</template>
