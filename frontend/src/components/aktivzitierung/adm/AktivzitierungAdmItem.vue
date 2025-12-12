<script lang="ts" setup>
import { computed } from 'vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'

const props = defineProps<{
  aktivzitierung: AktivzitierungAdm
}>()

const metaSummary = computed(() => {
  const parts: string[] = []

  if (props.aktivzitierung.documentNumber) {
    parts.push(props.aktivzitierung.documentNumber)
  }

  if (props.aktivzitierung.citationType) {
    parts.push(props.aktivzitierung.citationType)
  }

  const mainParts = parts.join(', ')

  return mainParts
})
</script>

<template>
  <div class="ris-body1-regular">
    {{ metaSummary }}
  </div>
</template>
