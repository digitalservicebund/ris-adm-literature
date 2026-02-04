<script lang="ts" setup>
import { computed } from "vue";
import { parseIsoDateToLocal } from "@/utils/dateHelpers";
import IconBaselineDescription from "~icons/ic/baseline-description";
import IconOutlineDescription from "~icons/ic/outline-description";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";

const props = defineProps<{
  aktivzitierung: AktivzitierungRechtsprechung;
}>();

function buildMainParts(a: AktivzitierungRechtsprechung): string {
  const court = [a.gerichttyp, a.gerichtort].filter(Boolean).join(" ");

  const mainParts = [
    court || undefined,
    a.entscheidungsdatum && parseIsoDateToLocal(a.entscheidungsdatum),
    a.aktenzeichen,
  ]
    .filter(Boolean)
    .join(", ");

  return a.dokumenttyp ? `${mainParts} (${a.dokumenttyp})` : mainParts;
}

const metaSummary = computed(() => {
  const a = props.aktivzitierung;
  const sections: string[] = [];
  const mainParts = buildMainParts(a);

  if (a.citationType) {
    sections.push(a.citationType);
  }

  if (mainParts.length) {
    sections.push(mainParts);
  }

  if (a.documentNumber) {
    sections.push(a.documentNumber);
  }

  return sections.join(" | ");
});
</script>

<template>
  <div class="flex w-full items-center gap-10">
    <IconBaselineDescription v-if="!!aktivzitierung.documentNumber" class="text-neutral-800" />
    <IconOutlineDescription v-else class="text-neutral-800" />
    <div class="flex flex-col">
      <div class="ris-body1-regular">
        {{ metaSummary }}
      </div>
    </div>
  </div>
</template>
