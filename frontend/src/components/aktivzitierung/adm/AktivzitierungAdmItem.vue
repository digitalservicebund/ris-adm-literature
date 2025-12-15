<script lang="ts" setup>
import { computed } from 'vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'
import { parseIsoDateToLocal } from '@/utils/dateHelpers'

const props = defineProps<{
  aktivzitierung: AktivzitierungAdm
}>()

const metaSummary = computed(() => {
  const parts = [
    props.aktivzitierung.citationType,
    props.aktivzitierung.normgeber,
    props.aktivzitierung.inkrafttretedatum
      ? parseIsoDateToLocal(props.aktivzitierung.inkrafttretedatum)
      : undefined,
    props.aktivzitierung.periodikum,
    props.aktivzitierung.dokumenttyp,
    props.aktivzitierung.documentNumber,
  ].filter(Boolean)

  return parts.join(', ')
})
</script>

<template>
  <div class="ris-body1-regular">
    {{ metaSummary }}
  </div>
</template>
