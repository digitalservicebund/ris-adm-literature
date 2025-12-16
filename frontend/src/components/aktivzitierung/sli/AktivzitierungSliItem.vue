<script lang="ts" setup>
import { computed } from 'vue'
import type { AktivzitierungSli } from '@/domain/AktivzitierungSli'

const props = defineProps<{
  aktivzitierung: AktivzitierungSli
}>()

const metaSummary = computed(() => {
  const parts: string[] = []

  if (props.aktivzitierung.veroeffentlichungsJahr) {
    parts.push(props.aktivzitierung.veroeffentlichungsJahr)
  }

  if (props.aktivzitierung.verfasser && props.aktivzitierung.verfasser.length > 0) {
    const authors = props.aktivzitierung.verfasser
      .map((author) => author.trim().replace(/,$/, ''))
      .filter((author) => author.length > 0)
      .join(', ')
    if (authors) {
      parts.push(authors)
    }
  }

  const mainParts = parts.join(', ')

  if (props.aktivzitierung.dokumenttypen && props.aktivzitierung.dokumenttypen.length > 0) {
    const abbreviations = props.aktivzitierung.dokumenttypen
      .map((dt) => dt.abbreviation)
      .filter(Boolean)
      .join(', ')
    if (abbreviations) {
      return `${mainParts} (${abbreviations})`
    }
  }

  return mainParts
})

const titleSummary = computed(() => {
  return props.aktivzitierung.titel || ''
})
</script>

<template>
  <div class="flex flex-col gap-2">
    <div class="ris-body1-regular">
      {{ metaSummary }}
    </div>
    <div class="ris-body2-regular text-gray-900">
      {{ titleSummary }}
    </div>
  </div>
</template>
