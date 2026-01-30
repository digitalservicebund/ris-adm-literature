// useCitationTypeRequirement.ts
import { ref } from "vue";
import { useValidationStore } from "@/composables/useValidationStore";
import type { ValidationError } from "@/components/input/types";

export type CitationTypeScope = "adm" | "rechtsprechung";

const SCOPE_KEYS: Record<CitationTypeScope, "citationTypeAdm" | "citationTypeRs"> = {
  adm: "citationTypeAdm",
  rechtsprechung: "citationTypeRs",
};

type CitationTypeStoreKey = "citationTypeAdm" | "citationTypeRs";

const store = useValidationStore<CitationTypeStoreKey>();

const currentCitationType = ref<string | undefined>();

function setCurrentCitationType(value: string | undefined) {
  currentCitationType.value = value?.trim() || undefined;
}

function setCitationTypeValidationError(scope: CitationTypeScope) {
  store.add("Pflichtfeld nicht befüllt", SCOPE_KEYS[scope]);
}

function createScopedStore(scope: CitationTypeScope) {
  const key = SCOPE_KEYS[scope];
  return {
    getByField: (field: string): ValidationError | undefined =>
      field === "citationType" ? store.getByField(key) : undefined,
    add: (message: string, instance: string) => {
      if (instance === "citationType") store.add(message, key);
    },
    remove: (field: string) => {
      if (field === "citationType") store.remove(key);
    },
  };
}

export function useCitationTypeRequirement(scope?: CitationTypeScope) {
  const validationStore = scope
    ? createScopedStore(scope)
    : (null as unknown as ReturnType<typeof createScopedStore>);
  const clear = scope ? () => store.remove(SCOPE_KEYS[scope]) : () => {};

  return {
    validationStore: validationStore ?? (store as unknown as ReturnType<typeof createScopedStore>),
    currentCitationType,
    setCurrentCitationType,
    setCitationTypeValidationError,
    clear,
  };
}
