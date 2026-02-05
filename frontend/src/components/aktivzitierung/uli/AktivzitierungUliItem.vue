<script lang="ts" setup>
import { computed } from "vue";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";
import IconBaselineDescription from "~icons/ic/outline-class";
import IconBaselineDescriptionFilled from "~icons/ic/class";

const props = defineProps<{
  aktivzitierung: AktivzitierungUli;
}>();

function calculateFundstelle(periodikum?: string, zitatstelle?: string): string | null {
  if (periodikum && zitatstelle) {
    return `${periodikum} ${zitatstelle}`;
  }
  return periodikum || zitatstelle || null;
}

const documentTypeAbbreviations = computed(
  () =>
    props.aktivzitierung?.dokumenttypen
      ?.map((dt) => dt?.abbreviation)
      .filter(Boolean)
      .join(", ") || "",
);

const metaSummary = computed(() => {
  const fundstelle = calculateFundstelle(
    props.aktivzitierung.periodikum,
    props.aktivzitierung.zitatstelle,
  );

  const authors = props.aktivzitierung.verfasser?.filter(Boolean).join(", ") ?? "";

  const parts: string[] = [];
  if (authors) parts.push(authors);
  if (fundstelle) parts.push(fundstelle);

  let main = parts.join(", ");

  if (documentTypeAbbreviations.value) {
    main = main
      ? `${main} (${documentTypeAbbreviations.value})`
      : `(${documentTypeAbbreviations.value})`;
  }

  return main;
});
</script>

<template>
  <div class="flex w-full items-center gap-10">
    <IconBaselineDescriptionFilled
      v-if="!!aktivzitierung.documentNumber"
      class="text-neutral-800"
      data-testid="icon-filled"
    />
    <IconBaselineDescription v-else class="text-neutral-800" data-testid="icon-outline" />
    <div class="flex flex-col">
      <div class="ris-body1-regular">
        {{ metaSummary }}
      </div>
    </div>
  </div>
</template>
