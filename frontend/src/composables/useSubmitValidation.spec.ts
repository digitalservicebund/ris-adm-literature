import { describe, it, expect, vi } from "vitest";
import { ref } from "vue";
import { useSubmitValidation } from "./useSubmitValidation";

describe("useSubmitValidation", () => {
  it("returns hasValidationErrors false when all getValidationErrorMessages return undefined", () => {
    const returnsUndefined = vi.fn(() => undefined);
    const alsoReturnsUndefined = vi.fn(() => undefined);

    const { hasValidationErrors } = useSubmitValidation([returnsUndefined, alsoReturnsUndefined]);

    expect(hasValidationErrors.value).toBe(false);
    expect(returnsUndefined).toHaveBeenCalled();
    expect(alsoReturnsUndefined).toHaveBeenCalled();
  });

  it("returns hasValidationErrors false when all getValidationErrorMessages return empty string", () => {
    const returnsEmptyString = vi.fn(() => "");
    const alsoReturnsEmptyString = vi.fn(() => "");

    const { hasValidationErrors } = useSubmitValidation([
      returnsEmptyString,
      alsoReturnsEmptyString,
    ]);

    expect(hasValidationErrors.value).toBe(false);
  });

  it("returns hasValidationErrors true when one getValidationErrorMessages returns a message", () => {
    const returnsUndefined = vi.fn(() => undefined);
    const returnsErrorMessage = vi.fn(() => "Pflichtfeld nicht befüllt");

    const { hasValidationErrors } = useSubmitValidation([returnsUndefined, returnsErrorMessage]);

    expect(hasValidationErrors.value).toBe(true);
  });

  it("returns hasValidationErrors true when first getValidationErrorMessages returns a message", () => {
    const returnsErrorMessage = vi.fn(() => "Some error");
    const returnsUndefined = vi.fn(() => undefined);

    const { hasValidationErrors } = useSubmitValidation([returnsErrorMessage, returnsUndefined]);

    expect(hasValidationErrors.value).toBe(true);
  });

  it("returns hasValidationErrors false when no getValidationErrorMessages are passed", () => {
    const { hasValidationErrors } = useSubmitValidation([]);

    expect(hasValidationErrors.value).toBe(false);
  });

  it("hasValidationErrors updates when getValidationErrorMessages result changes (reactivity)", () => {
    const errorMessage = ref<string | undefined>(undefined);
    const readsErrorFromRef = () => errorMessage.value;

    const { hasValidationErrors } = useSubmitValidation([readsErrorFromRef]);

    expect(hasValidationErrors.value).toBe(false);

    errorMessage.value = "New error";

    expect(hasValidationErrors.value).toBe(true);

    errorMessage.value = undefined;

    expect(hasValidationErrors.value).toBe(false);
  });
});
