import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import userEvent from "@testing-library/user-event";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import { InputText } from "primevue";
import AktivzitierungAdmInput from "./AktivzitierungAdmInput.vue";

function renderComponent(props: {
  aktivzitierung?: AktivzitierungAdm;
  showCancelButton?: boolean;
  showDeleteButton?: boolean;
}) {
  return render(AktivzitierungAdmInput, {
    props: {
      aktivzitierung: props.aktivzitierung,
      showCancelButton: props.showCancelButton ?? false,
      showDeleteButton: props.showDeleteButton ?? false,
    },
    global: {
      stubs: {
        ZitierArtDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Art der Zitierung" :value="modelValue?.label || ''" @input="$emit('update:modelValue', { abbreviation: $event.target.value, label: $event.target.value })" />`,
        },
        InstitutionDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Normgeber" :value="modelValue?.name || ''" @input="$emit('update:modelValue', { name: $event.target.value })" />`,
        },
        PeriodikumDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Periodikum" :value="modelValue?.abbreviation || ''" @input="$emit('update:modelValue', { abbreviation: $event.target.value })" />`,
        },
        DokumentTypDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Dokumenttyp" :value="modelValue || ''" @input="$emit('update:modelValue', $event.target.value)" />`,
        },
        DateInput: InputText,
      },
    },
  });
}

describe("AktivzitierungInput", () => {
  it("emits 'save' with full object only when 'Übernehmen' is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungAdm = {
      id: "123",
      documentNumber: "OLDVALUE",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", { name: "Dokumentnummer" });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    await user.clear(input);
    await user.type(input, "NEWVALUE");

    expect(emitted()["update:modelValue"]).toBeUndefined();

    await user.click(saveButton);

    expect(emitted().save![0]).toEqual([
      expect.objectContaining({
        id: "123",
        documentNumber: "NEWVALUE",
      }),
    ]);
  });

  it.each([
    { label: "Art der Zitierung", inputValue: "Abgr", expectedKey: "citationType" },
    { label: "Normgeber", inputValue: "Bundestag", expectedKey: "normgeber" },
    { label: "Inkrafttretedatum", inputValue: "01.01.2025", expectedKey: "inkrafttretedatum" },
    { label: "Aktenzeichen", inputValue: "§3", expectedKey: "aktenzeichen" },
    { label: "Periodikum", inputValue: "NJW", expectedKey: "periodikum" },
    { label: "Zitatstelle", inputValue: "Kap. 7", expectedKey: "zitatstelle" },
    { label: "Dokumenttyp", inputValue: "VWV", expectedKey: "dokumenttyp" },
  ])(
    "updates $expectedKey locally and emits on save",
    async ({ label, inputValue, expectedKey }) => {
      const user = userEvent.setup();
      const { emitted } = renderComponent({ aktivzitierung: { id: "123" } });

      const input = screen.getByRole("textbox", { name: label });
      await user.type(input, inputValue);

      await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

      const saveEvents = emitted().save as Array<[AktivzitierungAdm]>;
      const lastEventPayload = saveEvents[0]![0];

      expect(lastEventPayload[expectedKey as keyof AktivzitierungAdm]).toBe(inputValue);
    },
  );

  it("resets local state after saving a NEW entry (no initial id)", async () => {
    const user = userEvent.setup();
    vi.stubGlobal("crypto", { randomUUID: () => "new-uuid" });

    const { emitted } = renderComponent({ aktivzitierung: undefined });

    const input = screen.getByRole("textbox", { name: "Aktenzeichen" });
    await user.type(input, "TEST-AZ");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(input).toHaveValue("");
    expect(emitted().save).toHaveLength(1);
  });

  it("emits 'delete' with the correct ID", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "999" },
      showDeleteButton: true,
    });

    const deleteButton = screen.getByRole("button", { name: "Eintrag löschen" });
    await user.click(deleteButton);

    expect(emitted().delete![0]).toEqual(["999"]);
  });

  it("disables 'Übernehmen' button when form is empty", () => {
    renderComponent({ aktivzitierung: undefined });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    expect(saveButton).toBeDisabled();
  });

  it("emits 'search' with current state when search button is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungAdm = {
      id: "123",
      zitatstelle: "Search Query",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(emitted().search).toBeTruthy();
    expect(emitted().search![0]).toEqual([
      expect.objectContaining({
        id: "123",
        zitatstelle: "Search Query",
      }),
    ]);
  });

  it("emits 'cancel' when cancel button is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "123" },
      showCancelButton: true,
    });

    const cancelButton = screen.getByRole("button", { name: "Abbrechen" });
    await user.click(cancelButton);

    expect(emitted().cancel).toBeTruthy();
    expect(emitted().cancel).toHaveLength(1);
  });

  it("updates internal state when 'aktivzitierung' prop changes (watch)", async () => {
    const initialValue: AktivzitierungAdm = { id: "123", zitatstelle: "Initial Title" };
    const newValue: AktivzitierungAdm = { id: "123", zitatstelle: "Updated via Prop" };

    const { rerender } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", {
      name: "Zitatstelle",
    });
    expect(input).toHaveValue("Initial Title");

    await rerender({ aktivzitierung: newValue });

    expect(input).toHaveValue("Updated via Prop");
  });
});
