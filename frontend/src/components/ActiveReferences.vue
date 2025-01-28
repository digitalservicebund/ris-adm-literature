<script lang="ts" setup>
import { computed } from 'vue'
import EditableList from '@/components/EditableList.vue'
import ActiveReference from '@/domain/activeReference.ts'
import ActiveReferenceInput from '@/components/ActiveReferenceInput.vue'
import ActiveReferenceSummary from '@/components/ActiveReferenceSummary.vue'
import SingleNorm from '@/domain/singleNorm.ts'
import dayjs from 'dayjs'

const activeReferencesModel = defineModel<ActiveReference[]>({ default: [] })
const activeReferences = computed({
  get: () => activeReferencesModel.value,
  set: async (newValues) => {
    activeReferencesModel.value = newValues?.filter((value) => {
      removeDuplicateSingleNorms(value as ActiveReference)
      return true // Keep the value in the norms array
    })
  },
})

// TODO: Rename norm to activeReference

function removeDuplicateSingleNorms(norm: ActiveReference): void {
  if (!norm.singleNorms || !Array.isArray(norm.singleNorms)) {
    return // Exit if singleNorms is not an array
  }

  const uniqueSingleNorms = new Set<string>()

  norm.singleNorms = norm.singleNorms.filter((singleNorm) => {
    const uniqueKey = generateUniqueSingleNormKey(singleNorm)

    // Check uniqueness and add to the set if it's new
    if (!uniqueSingleNorms.has(uniqueKey)) {
      uniqueSingleNorms.add(uniqueKey)
      return true // Keep this singleNorm
    }
    return false // Filter out duplicates
  })
}

function generateUniqueSingleNormKey(singleNorm: SingleNorm): string {
  return JSON.stringify({
    singleNorm: singleNorm.singleNorm ?? '',
    dateOfVersion: singleNorm.dateOfVersion
      ? dayjs(singleNorm.dateOfVersion).format('DD.MM.YYYY')
      : '',
    dateOfRelevance: singleNorm.dateOfRelevance ?? '',
  })
}

const defaultValue = new ActiveReference() as ActiveReference
</script>

<template>
  <div>
    <div aria-label="Verweise">
      <h2 class="ds-label-01-bold mb-16">Verweise</h2>
      <div class="flex flex-row">
        <div class="flex-1">
          <EditableList
            v-model="activeReferences"
            :default-value="defaultValue"
            :edit-component="ActiveReferenceInput"
            :summary-component="ActiveReferenceSummary"
          />
        </div>
      </div>
    </div>
  </div>
</template>
