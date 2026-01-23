import { describe, it, expect, beforeEach, vi } from "vitest";
import { useCitationTypeRequirement } from "@/composables/useCitationaTypeRequirement";

describe("useCitationTypeRequirement", () => {
  let mockElement: HTMLElement;
  let mockScrollIntoView: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    // Reset module state by clearing validation errors and currentCitationType
    const instance1 = useCitationTypeRequirement();
    instance1.clear();
    instance1.setCurrentCitationType(undefined);

    // Mock DOM element and scrollIntoView
    mockScrollIntoView = vi.fn();
    mockElement = {
      scrollIntoView: mockScrollIntoView,
    } as unknown as HTMLElement;

    vi.spyOn(document, "getElementById").mockReturnValue(mockElement);
  });

  describe("setCurrentCitationType", () => {
    it("sets currentCitationType with trimmed value", () => {
      const { setCurrentCitationType, currentCitationType } =
        useCitationTypeRequirement();

      setCurrentCitationType("  Vergleiche  ");
      expect(currentCitationType.value).toBe("Vergleiche");
    });

    it("sets currentCitationType to undefined for empty string", () => {
      const { setCurrentCitationType, currentCitationType } =
        useCitationTypeRequirement();

      setCurrentCitationType("");
      expect(currentCitationType.value).toBeUndefined();
    });

    it("sets currentCitationType to undefined for whitespace-only string", () => {
      const { setCurrentCitationType, currentCitationType } =
        useCitationTypeRequirement();

      setCurrentCitationType("   ");
      expect(currentCitationType.value).toBeUndefined();
    });

    it("sets currentCitationType to undefined when passed undefined", () => {
      const { setCurrentCitationType, currentCitationType } =
        useCitationTypeRequirement();

      setCurrentCitationType(undefined);
      expect(currentCitationType.value).toBeUndefined();
    });

    it("preserves value without whitespace", () => {
      const { setCurrentCitationType, currentCitationType } =
        useCitationTypeRequirement();

      setCurrentCitationType("Ablehnung");
      expect(currentCitationType.value).toBe("Ablehnung");
    });
  });

  describe("markMissingAndScroll", () => {
    it("adds validation error with correct message and field", () => {
      const { markMissingAndScroll, validationStore } =
        useCitationTypeRequirement();

      markMissingAndScroll();

      const error = validationStore.getByField("citationType");
      expect(error).toBeDefined();
      expect(error?.message).toBe("Pflichtfeld nicht befüllt");
      expect(error?.instance).toBe("citationType");
    });

    it("scrolls to citation type field when element exists", () => {
      const { markMissingAndScroll } = useCitationTypeRequirement();

      markMissingAndScroll();

      expect(document.getElementById).toHaveBeenCalledWith("activeCitationPredicate");
      expect(mockScrollIntoView).toHaveBeenCalledWith({
        behavior: "smooth",
        block: "center",
      });
    });

    it("does not throw when citation type field element does not exist", () => {
      vi.spyOn(document, "getElementById").mockReturnValue(null);

      const { markMissingAndScroll } = useCitationTypeRequirement();

      expect(() => markMissingAndScroll()).not.toThrow();
      expect(mockScrollIntoView).not.toHaveBeenCalled();
    });

    it("replaces existing validation error when called multiple times", () => {
      const { markMissingAndScroll, validationStore } =
        useCitationTypeRequirement();

      markMissingAndScroll();
      markMissingAndScroll();

      const errors = validationStore.getAll();
      expect(errors).toHaveLength(1);
      expect(errors[0]?.message).toBe("Pflichtfeld nicht befüllt");
    });
  });

  describe("clear", () => {
    it("removes validation error for citationType field", () => {
      const { markMissingAndScroll, clear, validationStore } =
        useCitationTypeRequirement();

      markMissingAndScroll();
      expect(validationStore.getByField("citationType")).toBeDefined();

      clear();
      expect(validationStore.getByField("citationType")).toBeUndefined();
    });

    it("does not throw when no validation error exists", () => {
      const { clear } = useCitationTypeRequirement();

      expect(() => clear()).not.toThrow();
    });

    it("only removes citationType error, not other errors", () => {
      const { markMissingAndScroll, clear, validationStore } =
        useCitationTypeRequirement();

      // Add citationType error
      markMissingAndScroll();

      // Manually add another error (simulating a different field)
      validationStore.add("Some other error", "otherField" as "citationType");

      clear();

      expect(validationStore.getByField("citationType")).toBeUndefined();
      expect(validationStore.getByField("otherField" as "citationType")).toBeDefined();
    });
  });

  describe("shared state across instances", () => {
    it("validationStore is shared across instances", () => {
      const instance1 = useCitationTypeRequirement();
      const instance2 = useCitationTypeRequirement();

      instance1.markMissingAndScroll();

      expect(instance2.validationStore.getByField("citationType")).toBeDefined();
      expect(instance1.validationStore.getByField("citationType")).toEqual(
        instance2.validationStore.getByField("citationType"),
      );
    });

    it("currentCitationType is shared across instances", () => {
      const instance1 = useCitationTypeRequirement();
      const instance2 = useCitationTypeRequirement();

      instance1.setCurrentCitationType("Vergleiche");

      expect(instance2.currentCitationType.value).toBe("Vergleiche");
      expect(instance1.currentCitationType.value).toBe(
        instance2.currentCitationType.value,
      );
    });

    it("clearing validation error in one instance affects all instances", () => {
      const instance1 = useCitationTypeRequirement();
      const instance2 = useCitationTypeRequirement();

      instance1.markMissingAndScroll();
      instance2.clear();

      expect(instance1.validationStore.getByField("citationType")).toBeUndefined();
      expect(instance2.validationStore.getByField("citationType")).toBeUndefined();
    });

    it("updating currentCitationType in one instance affects all instances", () => {
      const instance1 = useCitationTypeRequirement();
      const instance2 = useCitationTypeRequirement();

      instance1.setCurrentCitationType("Ablehnung");
      instance2.setCurrentCitationType("Vergleiche");

      expect(instance1.currentCitationType.value).toBe("Vergleiche");
      expect(instance2.currentCitationType.value).toBe("Vergleiche");
    });
  });
});