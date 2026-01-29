// useCitationTypeRequirement.ts
import { ref } from "vue";
import { useValidationStore } from "@/composables/useValidationStore";

const validationStore = useValidationStore<"citationType">();

const currentCitationType = ref<string | undefined>();

function setCurrentCitationType(value: string | undefined) {
  currentCitationType.value = value?.trim() || undefined;
}

function markMissingAndScroll() {
  validationStore.add("Pflichtfeld nicht befüllt", "citationType");
}

function clear() {
  validationStore.remove("citationType");
}

export function useCitationTypeRequirement() {
  return {
    validationStore,
    currentCitationType,
    setCurrentCitationType,
    markMissingAndScroll,
    clear,
  };
}
