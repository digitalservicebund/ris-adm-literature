<script lang="ts" setup>
import { computed } from "vue";
import { parseIsoDateToLocal } from "@/utils/dateHelpers";
import IconReceiptLongFilled from "~icons/ic/baseline-receipt-long";
import IconReceiptLongOutline from "~icons/ic/outline-receipt-long";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";

const props = defineProps<{
  aktivzitierung: AktivzitierungRechtsprechung;
}>();

function buildMainParts(a: AktivzitierungRechtsprechung): string {
  const mainParts = [
    a.gerichttyp,
    a.gerichtort,
    a.entscheidungsdatum && parseIsoDateToLocal(a.entscheidungsdatum),
    a.aktenzeichen,
  ]
    .filter(Boolean)
    .join(", ");

  if (a.dokumenttyp) {
    return `${mainParts} (${a.dokumenttyp})`;
  }

  return mainParts;
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
    <IconReceiptLongFilled v-if="!!aktivzitierung.documentNumber" class="text-neutral-800" />
    <IconReceiptLongOutline v-else class="text-neutral-800" />
    <div class="flex flex-col">
      <div class="ris-body1-regular">
        {{ metaSummary }}
      </div>
    </div>
  </div>
</template>
