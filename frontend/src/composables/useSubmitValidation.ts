import { computed, type Ref } from "vue";

export function useSubmitValidation(getValidationErrorMessages: Array<() => string | undefined>): {
  hasValidationErrors: Ref<boolean>;
} {
  const hasValidationErrors = computed(() =>
    getValidationErrorMessages.some((getErrorMessage) => !!getErrorMessage()),
  );
  return { hasValidationErrors };
}
