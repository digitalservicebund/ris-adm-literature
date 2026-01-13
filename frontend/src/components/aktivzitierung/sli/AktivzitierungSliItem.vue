<script lang="ts" setup>
import { computed } from "vue";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import IconBaselineDescription from "~icons/ic/outline-class";
import IconBaselineDescriptionFilled from "~icons/ic/class";

const props = defineProps<{
  aktivzitierung: AktivzitierungSli;
}>();

const normalizedAuthors = computed(
  () =>
    props.aktivzitierung.verfasser
      ?.map((author) => author.trim().replace(/,$/, ""))
      .filter(Boolean)
      .join(", ") || "",
);

const documentTypeAbbreviations = computed(
  () =>
    props.aktivzitierung.dokumenttypen
      ?.map((dt) => dt.abbreviation)
      .filter(Boolean)
      .join(", ") || "",
);

const metaSummary = computed(() => {
  const parts: string[] = [];

  if (props.aktivzitierung.veroeffentlichungsJahr) {
    parts.push(props.aktivzitierung.veroeffentlichungsJahr);
  }

  if (normalizedAuthors.value) {
    parts.push(normalizedAuthors.value);
  }

  const main = parts.join(", ");

  if (documentTypeAbbreviations.value) {
    return main
      ? `${main} (${documentTypeAbbreviations.value})`
      : `(${documentTypeAbbreviations.value})`;
  }

  return main;
});

const titleSummary = computed(() => props.aktivzitierung.titel ?? "");
</script>

<template>
  <div class="flex w-full items-center gap-10">
    <IconBaselineDescriptionFilled
      v-if="!!aktivzitierung.documentNumber"
      class="text-neutral-800"
      data-testid="icon-filled"
    />
    <IconBaselineDescription v-else class="text-neutral-800" data-testid="icon-outline" />
    <div class="flex flex-col gap-2">
      <div class="ris-body1-regular">
        {{ metaSummary }}
      </div>
      <div class="ris-body2-regular text-gray-900">
        {{ titleSummary }}
      </div>
    </div>
  </div>
</template>
