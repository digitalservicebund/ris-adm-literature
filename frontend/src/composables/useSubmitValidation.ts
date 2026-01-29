import { computed, type Ref } from "vue";

export function useSubmitValidation(
  errorGetters: Array<() => string | undefined>
): { hasValidationErrors: Ref<boolean> } {
  const hasValidationErrors = computed(() =>
    errorGetters.some((getter) => !!getter())
  );
  return { hasValidationErrors };
}
