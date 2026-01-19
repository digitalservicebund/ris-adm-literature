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
        // Stubbing PrimeVue Button to ensure it renders its label/content
        Button: {
          template: `<button v-bind="$attrs"><slot /></button>`,
        },
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

    // 1. Change internal state
    await user.clear(input);
    await user.type(input, "NEWVALUE");

    // 2. Ensure no update:modelValue is emitted (since we removed it)
    expect(emitted()["update:modelValue"]).toBeUndefined();

    // 3. Click Save
    await user.click(saveButton);

    // 4. Check 'save' event
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

      const savedData = emitted().save[0][0] as AktivzitierungAdm;
      expect(savedData[expectedKey as keyof AktivzitierungAdm]).toBe(inputValue);
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

    // Check for the disabled attribute (PrimeVue Button usually passes this to native button)
    expect(saveButton).toBeDisabled();
  });
});
