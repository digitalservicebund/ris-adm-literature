<script lang="ts" setup>
import { computed } from 'vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'
import { parseIsoDateToLocal } from '@/utils/dateHelpers'

const props = defineProps<{
  aktivzitierung: AktivzitierungAdm
}>()

function buildBasicParts(aktivzitierung: AktivzitierungAdm): string[] {
  const parts: string[] = []

  if (aktivzitierung.citationType) {
    parts.push(aktivzitierung.citationType)
  }

  if (aktivzitierung.normgeber) {
    parts.push(aktivzitierung.normgeber)
  }

  if (aktivzitierung.inkrafttretedatum) {
    const formatted = parseIsoDateToLocal(aktivzitierung.inkrafttretedatum)
    if (formatted) {
      parts.push(formatted)
    }
  }

  if (aktivzitierung.aktenzeichen) {
    parts.push(aktivzitierung.aktenzeichen)
  }

  return parts
}

function calculateFundstelle(periodikum?: string, zitatstelle?: string): string | null {
  if (periodikum && zitatstelle) {
    return `${periodikum} ${zitatstelle}`
  }
  return periodikum || zitatstelle || null
}

function buildFundstellePart(fundstelle: string | null, dokumenttyp?: string): string | null {
  if (fundstelle && dokumenttyp) {
    return `${fundstelle} (${dokumenttyp})`
  }
  if (fundstelle) {
    return fundstelle
  }
  if (dokumenttyp) {
    return `(${dokumenttyp})`
  }
  return null
}

function formatSummary(parts: string[], documentNumber?: string): string {
  const firstPart = parts.join(', ')

  if (documentNumber) {
    return firstPart ? `${firstPart} | ${documentNumber}` : documentNumber
  }

  return firstPart
}

const metaSummary = computed(() => {
  const parts = buildBasicParts(props.aktivzitierung)

  const fundstelle = calculateFundstelle(
    props.aktivzitierung.periodikum,
    props.aktivzitierung.zitatstelle,
  )

  const fundstellePart = buildFundstellePart(fundstelle, props.aktivzitierung.dokumenttyp)
  if (fundstellePart) {
    parts.push(fundstellePart)
  }

  return formatSummary(parts, props.aktivzitierung.documentNumber)
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
