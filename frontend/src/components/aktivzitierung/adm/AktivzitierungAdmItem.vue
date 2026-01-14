<script lang="ts" setup>
import { computed } from "vue";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm.ts";
import { parseIsoDateToLocal } from "@/utils/dateHelpers";
import IconReceiptLongFilled from "~icons/ic/baseline-receipt-long";
import IconReceiptLongOutline from "~icons/ic/outline-receipt-long";

type MetaSummaryParts = {
  citationType?: string;
  mainParts: string[];
  documentNumber?: string;
};

const props = defineProps<{
  aktivzitierung: AktivzitierungAdm;
}>();

function formatMetaSummary({ citationType, mainParts, documentNumber }: MetaSummaryParts): string {
  const sections: string[] = [];

  if (citationType) {
    sections.push(citationType);
  }

  if (mainParts.length) {
    sections.push(mainParts.join(", "));
  }

  if (documentNumber) {
    sections.push(documentNumber);
  }

  return sections.join(" | ");
}

function calculateFundstelle(periodikum?: string, zitatstelle?: string): string | null {
  if (periodikum && zitatstelle) {
    return `${periodikum} ${zitatstelle}`;
  }
  return periodikum || zitatstelle || null;
}

function buildFundstellePart(fundstelle: string | null, dokumenttyp?: string): string | null {
  if (fundstelle && dokumenttyp) {
    return `${fundstelle} (${dokumenttyp})`;
  }
  if (fundstelle) {
    return fundstelle;
  }
  if (dokumenttyp) {
    return `(${dokumenttyp})`;
  }
  return null;
}

function buildMainParts(a: AktivzitierungAdm): string[] {
  return [
    a.normgeber,
    a.inkrafttretedatum && parseIsoDateToLocal(a.inkrafttretedatum),
    a.aktenzeichen,
    buildFundstellePart(calculateFundstelle(a.periodikum, a.zitatstelle), a.dokumenttyp),
  ].filter(Boolean) as string[];
}

const metaSummary = computed(() =>
  formatMetaSummary({
    citationType: props.aktivzitierung.citationType,
    mainParts: buildMainParts(props.aktivzitierung),
    documentNumber: props.aktivzitierung.documentNumber,
  }),
);
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
