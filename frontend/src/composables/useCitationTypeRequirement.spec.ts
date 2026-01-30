import { describe, it, expect, beforeEach } from "vitest";
import { useCitationTypeRequirement } from "@/composables/useCitationaTypeRequirement";

describe("useCitationTypeRequirement", () => {
  beforeEach(() => {
    useCitationTypeRequirement("adm").clear();
    useCitationTypeRequirement("rechtsprechung").clear();
    useCitationTypeRequirement("adm").setCurrentCitationType(undefined);
  });

  describe("setCurrentCitationType", () => {
    it("sets currentCitationType with trimmed value", () => {
      const { setCurrentCitationType, currentCitationType } = useCitationTypeRequirement();

      setCurrentCitationType("  Vergleiche  ");
      expect(currentCitationType.value).toBe("Vergleiche");
    });

    it("sets currentCitationType to undefined for empty string", () => {
      const { setCurrentCitationType, currentCitationType } = useCitationTypeRequirement();

      setCurrentCitationType("");
      expect(currentCitationType.value).toBeUndefined();
    });

    it("sets currentCitationType to undefined for whitespace-only string", () => {
      const { setCurrentCitationType, currentCitationType } = useCitationTypeRequirement();

      setCurrentCitationType("   ");
      expect(currentCitationType.value).toBeUndefined();
    });

    it("sets currentCitationType to undefined when passed undefined", () => {
      const { setCurrentCitationType, currentCitationType } = useCitationTypeRequirement();

      setCurrentCitationType(undefined);
      expect(currentCitationType.value).toBeUndefined();
    });

    it("preserves value without whitespace", () => {
      const { setCurrentCitationType, currentCitationType } = useCitationTypeRequirement();

      setCurrentCitationType("Ablehnung");
      expect(currentCitationType.value).toBe("Ablehnung");
    });
  });

  describe("setCitationTypeValidationError", () => {
    it("adds validation error with correct message and field", () => {
      const { setCitationTypeValidationError, validationStore } = useCitationTypeRequirement("adm");

      setCitationTypeValidationError("adm");

      const error = validationStore.getByField("citationType");
      expect(error).toBeDefined();
      expect(error?.message).toBe("Pflichtfeld nicht befüllt");
      expect(error?.instance).toBe("citationTypeAdm");
    });

    it("does not throw when called", () => {
      const { setCitationTypeValidationError } = useCitationTypeRequirement("adm");

      expect(() => setCitationTypeValidationError("adm")).not.toThrow();
    });

    it("replaces existing validation error when called multiple times", () => {
      const { setCitationTypeValidationError, validationStore } = useCitationTypeRequirement("adm");

      setCitationTypeValidationError("adm");
      setCitationTypeValidationError("adm");

      const error = validationStore.getByField("citationType");
      expect(error).toBeDefined();
      expect(error?.message).toBe("Pflichtfeld nicht befüllt");
    });
  });

  describe("clear", () => {
    it("removes validation error for citationType field", () => {
      const { setCitationTypeValidationError, clear, validationStore } =
        useCitationTypeRequirement("adm");

      setCitationTypeValidationError("adm");
      expect(validationStore.getByField("citationType")).toBeDefined();

      clear();
      expect(validationStore.getByField("citationType")).toBeUndefined();
    });

    it("does not throw when no validation error exists", () => {
      const { clear } = useCitationTypeRequirement("adm");

      expect(() => clear()).not.toThrow();
    });

    it("only removes error for own scope, not other scopes", () => {
      const adm = useCitationTypeRequirement("adm");
      const rechtsprechung = useCitationTypeRequirement("rechtsprechung");

      adm.setCitationTypeValidationError("adm");
      rechtsprechung.setCitationTypeValidationError("rechtsprechung");

      adm.clear();

      expect(adm.validationStore.getByField("citationType")).toBeUndefined();
      expect(rechtsprechung.validationStore.getByField("citationType")).toBeDefined();
    });
  });

  describe("shared state across instances", () => {
    it("validationStore is shared across instances with same scope", () => {
      const instance1 = useCitationTypeRequirement("adm");
      const instance2 = useCitationTypeRequirement("adm");

      instance1.setCitationTypeValidationError("adm");

      expect(instance2.validationStore.getByField("citationType")).toBeDefined();
      expect(instance1.validationStore.getByField("citationType")).toEqual(
        instance2.validationStore.getByField("citationType"),
      );
    });

    it("currentCitationType is shared across instances", () => {
      const instance1 = useCitationTypeRequirement("adm");
      const instance2 = useCitationTypeRequirement("adm");

      instance1.setCurrentCitationType("Vergleiche");

      expect(instance2.currentCitationType.value).toBe("Vergleiche");
      expect(instance1.currentCitationType.value).toBe(instance2.currentCitationType.value);
    });

    it("clearing validation error in one instance affects same-scope instances", () => {
      const instance1 = useCitationTypeRequirement("adm");
      const instance2 = useCitationTypeRequirement("adm");

      instance1.setCitationTypeValidationError("adm");
      instance2.clear();

      expect(instance1.validationStore.getByField("citationType")).toBeUndefined();
      expect(instance2.validationStore.getByField("citationType")).toBeUndefined();
    });

    it("updating currentCitationType in one instance affects all instances", () => {
      const instance1 = useCitationTypeRequirement("adm");
      const instance2 = useCitationTypeRequirement("adm");

      instance1.setCurrentCitationType("Ablehnung");
      instance2.setCurrentCitationType("Vergleiche");

      expect(instance1.currentCitationType.value).toBe("Vergleiche");
      expect(instance2.currentCitationType.value).toBe("Vergleiche");
    });
  });
});
