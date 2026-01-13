import { createTestingPinia } from "@pinia/testing";
import { flushPromises } from "@vue/test-utils";
import { setActivePinia } from "pinia";
import { useSaveToRemote } from "@/composables/useSaveToRemote";
import { useAdmDocUnitStore } from "@/stores/admDocumentUnitStore";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";

vi.mock("vue", async (importActual) => {
  const vue: Record<string, unknown> = await importActual();
  return { ...vue, onUnmounted: vi.fn() };
});

function mockDocumentUnitStore(callback = vi.fn()) {
  const documentUnitStore = useAdmDocUnitStore();
  documentUnitStore.update = callback;

  return documentUnitStore;
}

describe("useSaveToRemote", () => {
  beforeEach(() => {
    vi.useFakeTimers();
    setActivePinia(createTestingPinia());
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("calls the callback every time the trigger is called", async () => {
    const documentUnitStore = mockDocumentUnitStore();
    const { triggerSave } = useSaveToRemote(documentUnitStore);

    await triggerSave();
    expect(documentUnitStore.update).toHaveBeenCalledTimes(1);

    await triggerSave();
    expect(documentUnitStore.update).toHaveBeenCalledTimes(2);
  });

  it("does not call the callback if a call is still in progress", async () => {
    let resolveCallback: (data: unknown) => void = vi.fn();
    const callback = vi.fn().mockImplementation(function () {
      return new Promise((resolve) => (resolveCallback = resolve));
    });

    const documentUnitStore = mockDocumentUnitStore(callback);
    const { triggerSave } = useSaveToRemote(documentUnitStore);

    triggerSave().then(() => {});
    triggerSave().then(() => {});
    resolveCallback(undefined);
    await flushPromises();
    triggerSave().then(() => {});

    expect(documentUnitStore.update).toHaveBeenCalledTimes(2);
  });

  it("toggles the in progress state while callback runs", async () => {
    let resolveCallback: (data: unknown) => void = vi.fn();
    const callback = vi.fn().mockImplementation(function () {
      return new Promise((resolve) => (resolveCallback = resolve));
    });

    const documentUnitStore = mockDocumentUnitStore(callback);
    const { triggerSave, saveIsInProgress } = useSaveToRemote(documentUnitStore);

    expect(saveIsInProgress.value).toBe(false);

    triggerSave().then(() => {});

    expect(saveIsInProgress.value).toBe(true);

    resolveCallback(undefined);
    await flushPromises();

    expect(saveIsInProgress.value).toBe(false);
  });

  it("also sets back the in progress state when callback throws exception", async () => {
    const callback = vi.fn().mockRejectedValue(new Error());
    const documentUnitStore = mockDocumentUnitStore(callback);
    const { triggerSave, saveIsInProgress } = useSaveToRemote(documentUnitStore);

    await triggerSave();

    expect(saveIsInProgress.value).toBe(false);
  });

  it("sets the response error if update failed", async () => {
    const callback = vi.fn().mockResolvedValue(false);
    const documentUnitStore = mockDocumentUnitStore(callback);
    const { triggerSave, lastSaveError } = useSaveToRemote(documentUnitStore);

    await triggerSave();

    expect(lastSaveError.value).toEqual({
      title: "Dokumentationseinheit konnte nicht aktualisiert werden.",
    });
  });

  it("sets connection error if callback throws exception one", async () => {
    const callback = vi.fn().mockRejectedValue(new Error());
    const documentUnitStore = mockDocumentUnitStore(callback);
    const { triggerSave, lastSaveError } = useSaveToRemote(documentUnitStore);

    await triggerSave();

    expect(lastSaveError.value).toEqual({ title: "Verbindung fehlgeschlagen" });
  });

  it("resets the response error after the next successful save", async () => {
    const documentUnitStore = mockDocumentUnitStore();
    const { triggerSave, lastSaveError } = useSaveToRemote(documentUnitStore);

    expect(lastSaveError.value).toBeUndefined();

    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(false));

    await triggerSave();

    expect(lastSaveError.value).toBeDefined();

    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(true));
    await triggerSave();

    expect(lastSaveError.value).toBeUndefined();
  });

  it("sets the last save on date only after each successfully callback call", async () => {
    const documentUnitStore = mockDocumentUnitStore();
    const { triggerSave, formattedLastSavedOn } = useSaveToRemote(documentUnitStore);

    expect(formattedLastSavedOn.value).toBeUndefined();

    vi.setSystemTime(60_000);
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(true));
    await triggerSave();

    const firstLastSavedOn = formattedLastSavedOn.value;
    expect(firstLastSavedOn).toBeDefined();

    vi.setSystemTime(120_000);
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(false));
    await triggerSave();

    expect(formattedLastSavedOn.value).toBe(firstLastSavedOn);

    vi.setSystemTime(180_000);
    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(true));
    await triggerSave();

    expect(formattedLastSavedOn.value).not.toBe(firstLastSavedOn);
  });

  it("does not reset error if callback did not change anything", async () => {
    const documentUnitStore = mockDocumentUnitStore();
    const { triggerSave, formattedLastSavedOn, lastSaveError } = useSaveToRemote(documentUnitStore);

    mockDocumentUnitStore(vi.fn().mockResolvedValueOnce(false).mockResolvedValueOnce(false));

    // first save attempt with error response
    await triggerSave();
    expect(formattedLastSavedOn.value).toBeUndefined();
    expect(lastSaveError.value).toEqual({
      title: "Dokumentationseinheit konnte nicht aktualisiert werden.",
    });

    // second save attepmpt, nothing changed
    await triggerSave();
    expect(formattedLastSavedOn.value).toBeUndefined();
    expect(lastSaveError.value).toEqual({
      title: "Dokumentationseinheit konnte nicht aktualisiert werden.",
    });
  });
});
