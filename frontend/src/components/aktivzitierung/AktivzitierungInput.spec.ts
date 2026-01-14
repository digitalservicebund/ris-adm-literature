import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect } from "vitest";
import AktivzitierungInput from "./AktivzitierungInput.vue";

// Dummy type for generic T
type DummyT = { id: string; documentNumber?: string; documentTypes?: string[]; count?: number };

describe("AktivzitierungInput", () => {
  it('emits "update" when save button is clicked', async () => {
    const user = userEvent.setup();
    const initial = { id: "123", documentNumber: "Initial" };

    const { emitted } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: initial,
        showCancelButton: true,
        showDeleteButton: false,
      },
      slots: {
        default: `<template #default="{ modelValue, onUpdateModelValue }">
                    <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
                  </template>`,
      },
    });

    const input = screen.getByTestId("input") as HTMLInputElement;
    await user.clear(input);
    await user.type(input, "Updated");

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    const events = emitted().save as [DummyT][];
    expect(events).toBeTruthy();
    const payload = events[0]![0];

    expect(payload).toEqual({
      ...initial,
      documentNumber: "Updated",
      id: "123",
    });
  });

  it('emits "cancel" when cancel button is clicked', async () => {
    const user = userEvent.setup();
    const { emitted } = render(AktivzitierungInput, {
      props: { showCancelButton: true, showDeleteButton: false },
    });

    const cancelButton = screen.getByRole("button", { name: "Abbrechen" });
    await user.click(cancelButton);

    const events = emitted().cancel;
    expect(events).toHaveLength(1);
  });

  it('emits "delete" when delete button is clicked for existing entry', async () => {
    const user = userEvent.setup();
    const existing = { id: "abc" };

    const { emitted } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: existing,
        showCancelButton: true,
        showDeleteButton: true,
      },
    });

    const deleteButton = screen.getByRole("button", { name: "Eintrag löschen" });
    await user.click(deleteButton);

    const events = emitted().delete as [string][];
    expect(events).toHaveLength(1);
    expect(events[0]![0]).toBe("abc");
  });

  it("does not show delete button if flag is set", async () => {
    const { emitted } = render(AktivzitierungInput, {
      props: { showCancelButton: false, showDeleteButton: false },
    });

    expect(screen.queryByRole("button", { name: "Eintrag löschen" })).not.toBeInTheDocument();
    expect(emitted().delete).toBeUndefined();
  });

  it("updates aktivzitierungRef when aktivzitierung prop changes", async () => {
    const initialValue: DummyT = { id: "1", documentNumber: "OLD" };
    const newValue: DummyT = { id: "2", documentNumber: "NEW" };

    const { rerender } = render(AktivzitierungInput, {
      props: {
        aktivzitierung: initialValue,
        showCancelButton: false,
        showDeleteButton: false,
      },
      slots: {
        default: `<template #default="{ modelValue }">
                    <input data-testid="docnumber" :value="modelValue.documentNumber" readonly />
                  </template>`,
      },
    });

    // Initial value should appear in slot
    const input = screen.getByTestId("docnumber") as HTMLInputElement;
    expect(input.value).toBe("OLD");

    // Rerender with new aktivzitierung prop
    await rerender({ aktivzitierung: newValue });

    // The slot should now show the updated value
    const updatedInput = screen.getByTestId("docnumber") as HTMLInputElement;
    expect(updatedInput.value).toBe("NEW");

    // Rerender with undefined aktivzitierung
    await rerender({ aktivzitierung: undefined });

    // The slot should show the existing value
    expect(updatedInput.value).toBe("NEW");
  });

  it("disables the save button when fields are empty or only whitespace", async () => {
    render(AktivzitierungInput, {
      props: { showCancelButton: false, showDeleteButton: false },
      slots: {
        default: `<template #default="{ modelValue, onUpdateModelValue }">
                      <input data-testid="input" :value="modelValue.documentNumber" @input="onUpdateModelValue({ ...modelValue, documentNumber: $event.target.value })"/>
                    </template>`,
      },
    });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    const input = screen.getByTestId("input");

    // 1. Initial state (only ID exists) -> Should be empty/disabled
    expect(saveButton).toBeDisabled();

    // 2. Whitespace only -> Should be empty/disabled
    await userEvent.type(input, "   ");
    expect(saveButton).toBeDisabled();

    // 3. Valid text -> Should be enabled
    await userEvent.type(input, "DOC-123");
    expect(saveButton).toBeEnabled();
  });

  it("considers undefined as empty", async () => {
    const initial: DummyT = { id: "1", documentNumber: undefined };

    render(AktivzitierungInput, {
      props: { aktivzitierung: initial, showCancelButton: false, showDeleteButton: false },
    });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    expect(saveButton).toBeDisabled();
  });

  it("considers empty arrays as empty", async () => {
    const initial: DummyT = { id: "1", documentNumber: "", documentTypes: [] };

    render(AktivzitierungInput, {
      props: { aktivzitierung: initial, showCancelButton: false, showDeleteButton: false },
    });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    expect(saveButton).toBeDisabled();
  });

  it("save button is enabled for a field of type number", async () => {
    const initial: DummyT = { id: "1", documentNumber: "", count: 10 };

    render(AktivzitierungInput, {
      props: { aktivzitierung: initial, showCancelButton: false, showDeleteButton: false },
    });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    expect(saveButton).toBeEnabled();
  });
});
