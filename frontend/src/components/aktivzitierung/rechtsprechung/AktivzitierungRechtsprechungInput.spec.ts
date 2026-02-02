import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi, beforeEach } from "vitest";
import userEvent from "@testing-library/user-event";
import { InputText } from "primevue";
import AktivzitierungRechtsprechungInput from "./AktivzitierungRechtsprechungInput.vue";
import type { AktivzitierungRechtsprechung } from "@/domain/AktivzitierungRechtsprechung";
import { useCitationTypeRequirement } from "@/composables/useCitationaTypeRequirement";

function renderComponent(props: {
  aktivzitierung?: AktivzitierungRechtsprechung;
  showCancelButton?: boolean;
  showDeleteButton?: boolean;
  showSearchButton?: boolean;
}) {
  return render(AktivzitierungRechtsprechungInput, {
    props: {
      aktivzitierung: props.aktivzitierung,
      showCancelButton: props.showCancelButton ?? false,
      showDeleteButton: props.showDeleteButton ?? false,
      showSearchButton: props.showSearchButton ?? false,
    },
    global: {
      stubs: {
        ZitierArtDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Art der Zitierung" :value="modelValue?.label || ''" @input="$emit('update:modelValue', { abbreviation: $event.target.value, label: $event.target.value })" />`,
        },
        CourtDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `<input aria-label="Gericht" :value="modelValue?.type || ''" @input="$emit('update:modelValue', { type: $event.target.value })" />`,
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

describe("AktivzitierungRechtsprechungInput", () => {
  beforeEach(() => {
    const { clear } = useCitationTypeRequirement("rechtsprechung");
    clear();
  });

  it("emits 'save' with full object only when 'Übernehmen' is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungRechtsprechung = {
      id: "123",
      aktenzeichen: "Akte X",
      citationType: "Abgr",
      gerichttyp: "AG",
      entscheidungsdatum: "01.01.2025",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", { name: "Aktenzeichen" });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    await user.clear(input);
    await user.type(input, "Akte Y");

    expect(emitted()["update:modelValue"]).toBeUndefined();

    await user.click(saveButton);

    expect(emitted().save![0]).toEqual([
      expect.objectContaining({
        id: "123",
        aktenzeichen: "Akte Y",
        citationType: "Abgr",
        entscheidungsdatum: "01.01.2025",
      }),
    ]);
  });

  it("does not emit save when citation type is empty", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungRechtsprechung = {
      id: "123",
      aktenzeichen: "Some AZ",
      gerichttyp: "AG",
      entscheidungsdatum: "01.01.2025",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(emitted().save).toBeUndefined();
  });

  it("does not emit save when citation type validation error is present", async () => {
    const user = userEvent.setup();
    const { validationStore } = useCitationTypeRequirement("rechtsprechung");

    const initialValue: AktivzitierungRechtsprechung = {
      id: "123",
      citationType: "Abgr",
      aktenzeichen: "AZ",
      entscheidungsdatum: "01.01.2025",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    validationStore.add("Pflichtfeld nicht befüllt", "citationType");

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(emitted().save).toBeUndefined();
  });

  it.each([
    { label: "Art der Zitierung", inputValue: "Abgr", expectedKey: "citationType" },
    { label: "Gericht", inputValue: "AG", expectedKey: "gerichttyp" },
    { label: "Entscheidungsdatum", inputValue: "01.01.2025", expectedKey: "entscheidungsdatum" },
    { label: "Aktenzeichen", inputValue: "§3", expectedKey: "aktenzeichen" },
    { label: "Dokumenttyp", inputValue: "VWV", expectedKey: "dokumenttyp" },
  ])(
    "updates $expectedKey locally and emits on save",
    async ({ label, inputValue, expectedKey }) => {
      const baseMandatory = {
        id: "123",
        citationType: "Abgr",
        gerichttyp: "AG",
        aktenzeichen: "AZ",
        entscheidungsdatum: "01.01.2025",
      } as AktivzitierungRechtsprechung;

      const user = userEvent.setup();
      const { emitted } = renderComponent({
        aktivzitierung: { ...baseMandatory },
      });

      const input = screen.getByRole("textbox", { name: label });
      await user.clear(input);
      await user.type(input, inputValue);

      await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

      const saveEvents = emitted().save as Array<[AktivzitierungRechtsprechung]>;
      const lastEventPayload = saveEvents[0]![0];

      expect(lastEventPayload[expectedKey as keyof AktivzitierungRechtsprechung]).toBe(inputValue);
    },
  );

  it("resets local state after saving a NEW entry (no initial id)", async () => {
    const user = userEvent.setup();
    vi.stubGlobal("crypto", { randomUUID: () => "new-uuid" });

    const { emitted } = renderComponent({ aktivzitierung: undefined });

    const citationTypeInput = screen.getByRole("textbox", { name: "Art der Zitierung" });
    await user.type(citationTypeInput, "Abgr");

    const gerichtInput = screen.getByRole("textbox", { name: "Gericht" });
    await user.type(gerichtInput, "AG");

    const entscheidungsdatumInput = screen.getByRole("textbox", { name: "Entscheidungsdatum" });
    await user.type(entscheidungsdatumInput, "01.01.2025");

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
    const initialValue: AktivzitierungRechtsprechung = {
      id: "123",
      aktenzeichen: "Search Query",
      entscheidungsdatum: "01.01.2025",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue, showSearchButton: true });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(emitted().search).toBeTruthy();
    expect(emitted().search![0]).toEqual([
      expect.objectContaining({
        id: "123",
        aktenzeichen: "Search Query",
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
    const initialValue: AktivzitierungRechtsprechung = { id: "123", aktenzeichen: "Initial Title" };
    const newValue: AktivzitierungRechtsprechung = { id: "123", aktenzeichen: "Updated via Prop" };

    const { rerender } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", {
      name: "Aktenzeichen",
    });
    expect(input).toHaveValue("Initial Title");

    await rerender({ aktivzitierung: newValue });

    expect(input).toHaveValue("Updated via Prop");
  });

  it("shows validation error when citation type is empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        gerichttyp: "AG",
        aktenzeichen: "Some AZ",
        entscheidungsdatum: "01.01.2025",
      },
    });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });

  it("shows validation errors for Gericht when empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        citationType: "Abgr",
        aktenzeichen: "Some AZ",
        entscheidungsdatum: "01.01.2025",
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });

  it("shows validation errors for Aktenzeichen when empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        citationType: "Abgr",
        gerichttyp: "AG",
        entscheidungsdatum: "01.01.2025",
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });
  it("shows validation errors for Entscheidungsdatum when empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        citationType: "Abgr",
        gerichttyp: "AG",
        aktenzeichen: "Some AZ",
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });
});
