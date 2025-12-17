<script lang="ts" setup>
import { computed } from 'vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'
import { parseIsoDateToLocal } from '@/utils/dateHelpers'

const props = defineProps<{
  aktivzitierung: AktivzitierungAdm
}>()

const metaSummary = computed(() => {
  const parts: string[] = []

  if (props.aktivzitierung.normgeber) parts.push(props.aktivzitierung.normgeber)

  if (props.aktivzitierung.inkrafttretedatum) {
    const formatted = parseIsoDateToLocal(props.aktivzitierung.inkrafttretedatum)
    if (formatted) parts.push(formatted)
  }

  if (props.aktivzitierung.aktenzeichen) parts.push(props.aktivzitierung.aktenzeichen)

  // Fundstelle = Periodikum + Zitatstelle
  const periodikum = props.aktivzitierung.periodikum
  const zitatstelle = props.aktivzitierung.zitatstelle
  const fundstelle =
    periodikum && zitatstelle ? `${periodikum} ${zitatstelle}` : periodikum || zitatstelle || null

  const dokumenttyp = props.aktivzitierung.dokumenttyp

  if (fundstelle && dokumenttyp) {
    parts.push(`${fundstelle} (${dokumenttyp})`)
  } else if (fundstelle) {
    parts.push(fundstelle)
  } else if (dokumenttyp) {
    parts.push(`(${dokumenttyp})`)
  }

  // Append document number with | if it exists
  if (props.aktivzitierung.documentNumber) {
    const firstPart = parts.join(', ')
    return firstPart
      ? `${firstPart} | ${props.aktivzitierung.documentNumber}`
      : props.aktivzitierung.documentNumber
  } else {
    return parts.join(', ')
  }
})
</script>

<template>
  <div class="flex flex-col">
    <div class="ris-body1-regular">
      {{ metaSummary }}
    </div>

    <div
      v-if="aktivzitierung.documentNumber && aktivzitierung.langueberschrift"
      class="ris-body2-regular text-gray-900"
    >
      {{ aktivzitierung.langueberschrift }}
    </div>
  </div>
</template>
