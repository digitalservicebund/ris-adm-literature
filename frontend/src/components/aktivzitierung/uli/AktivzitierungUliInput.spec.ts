import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import AktivzitierungUliInput from "./AktivzitierungUliInput.vue";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";

function renderComponent(props: {
  aktivzitierung?: AktivzitierungUli;
  showCancelButton?: boolean;
  showDeleteButton?: boolean;
  showSearchButton?: boolean;
}) {
  return render(AktivzitierungUliInput, {
    props: {
      aktivzitierung: props.aktivzitierung,
      showCancelButton: props.showCancelButton ?? false,
      showDeleteButton: props.showDeleteButton ?? false,
      showSearchButton: props.showSearchButton ?? false,
    },
    global: {
      stubs: {
        PeriodikumDropDown: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Periodikum"
              :value="modelValue?.abbreviation ?? ''"
              @input="$emit('update:modelValue', { abbreviation: $event.target.value })"
            />
          `,
        },
        DokumentTyp: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Dokumenttyp"
              @input="$emit('update:modelValue', [{ abbreviation: $event.target.value, name: $event.target.value }])"
            />
          `,
        },
        RisChipsInput: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Verfasser/in"
              :value="(modelValue || []).join(',')"
              @input="$emit('update:modelValue', $event.target.value.split(',').filter(Boolean))"
            />
          `,
        },
      },
    },
  });
}

describe("AktivzitierungUliInput", () => {
  it("emits 'save' with full object only when 'Übernehmen' is clicked and all required fields filled", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungUli = {
      id: "123",
      periodikum: "BAnz",
      zitatstelle: "AT 27.08.2024",
      verfasser: ["Müller"],
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const zitatstelleInput = screen.getByRole("textbox", { name: "Zitatstelle" });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    await user.clear(zitatstelleInput);
    await user.type(zitatstelleInput, "AT 28.08.2024");

    expect(emitted().save).toBeUndefined();

    await user.click(saveButton);

    expect(emitted().save![0]).toEqual([
      expect.objectContaining({
        id: "123",
        periodikum: "BAnz",
        zitatstelle: "AT 28.08.2024",
        verfasser: ["Müller"],
      }),
    ]);
  });

  it("does not emit save when periodikum is empty", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungUli = {
      id: "123",
      zitatstelle: "AT 27.08.2024",
      verfasser: ["Müller"],
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(emitted().save).toBeUndefined();
  });

  it("does not emit save when zitatstelle is empty", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungUli = {
      id: "123",
      periodikum: "BAnz",
      verfasser: ["Müller"],
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(emitted().save).toBeUndefined();
  });

  it("does not emit save when verfasser is empty", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungUli = {
      id: "123",
      periodikum: "BAnz",
      zitatstelle: "AT 27.08.2024",
      verfasser: [],
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(emitted().save).toBeUndefined();
  });

  it.each([
    { label: "Periodikum", inputValue: "BAnz", expectedKey: "periodikum" as const },
    { label: "Zitatstelle", inputValue: "AT 27.08.2024", expectedKey: "zitatstelle" as const },
    { label: "Verfasser/in", inputValue: "Müller", expectedKey: "verfasser" as const },
    { label: "Dokumentnummer", inputValue: "DOC-123", expectedKey: "documentNumber" as const },
  ])(
    "updates $expectedKey locally and emits on save",
    async ({ label, inputValue, expectedKey }) => {
      const baseMandatory: AktivzitierungUli = {
        id: "123",
        periodikum: "BAnz",
        zitatstelle: "AT 27.08.2024",
        verfasser: ["Müller"],
      };

      const user = userEvent.setup();
      const { emitted } = renderComponent({
        aktivzitierung: { ...baseMandatory },
      });

      const input = screen.getByRole("textbox", { name: label });
      await user.clear(input);
      await user.type(input, inputValue);

      await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

      const saveEvents = emitted().save as Array<[AktivzitierungUli]>;
      const lastEventPayload = saveEvents[0]![0];

      if (expectedKey === "verfasser") {
        expect(lastEventPayload.verfasser).toEqual([inputValue]);
      } else {
        expect(lastEventPayload[expectedKey]).toBe(inputValue);
      }
    },
  );

  it("emits 'save' with updated dokumenttypen when Dokumenttyp is used", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: {
        id: "123",
        periodikum: "BAnz",
        zitatstelle: "AT 27.08.2024",
        verfasser: ["Müller"],
        dokumenttypen: [],
      },
    });

    const docTypeInput = screen.getByRole("textbox", { name: "Dokumenttyp" });
    await user.type(docTypeInput, "Aufsatz");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(emitted().save).toBeTruthy();
    const saveEvents = emitted().save as Array<[AktivzitierungUli]>;
    const lastEventPayload = saveEvents[0]![0];
    expect(lastEventPayload.dokumenttypen).toEqual([{ abbreviation: "Aufsatz", name: "Aufsatz" }]);
  });

  it("disables 'Übernehmen' button when form is empty", () => {
    renderComponent({ aktivzitierung: undefined });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    expect(saveButton).toBeDisabled();
  });

  it("emits 'delete' with correct ID", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "delete-me-id" },
      showDeleteButton: true,
    });

    const deleteButton = screen.getByRole("button", { name: "Eintrag löschen" });
    await user.click(deleteButton);

    expect(emitted().delete![0]).toEqual(["delete-me-id"]);
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

  it("resets local state after saving a new entry (no initial aktivzitierung)", async () => {
    const user = userEvent.setup();
    vi.stubGlobal("crypto", { randomUUID: () => "new-uuid" });

    const { emitted } = renderComponent({ aktivzitierung: undefined });

    const periodikumInput = screen.getByRole("textbox", { name: "Periodikum" });
    await user.type(periodikumInput, "BAnz");

    const zitatstelleInput = screen.getByRole("textbox", { name: "Zitatstelle" });
    await user.type(zitatstelleInput, "AT 27.08.2024");

    const verfasserInput = screen.getByRole("textbox", { name: "Verfasser/in" });
    await user.type(verfasserInput, "Müller");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(zitatstelleInput).toHaveValue("");
    expect(emitted().save).toHaveLength(1);
  });

  it("updates internal state when 'aktivzitierung' prop changes (watch)", async () => {
    const initialValue: AktivzitierungUli = {
      id: "123",
      zitatstelle: "Initial Zitatstelle",
      periodikum: "BAnz",
      verfasser: ["Müller"],
    };
    const newValue: AktivzitierungUli = {
      id: "123",
      zitatstelle: "Updated via Prop",
      periodikum: "BAnz",
      verfasser: ["Müller"],
    };

    const { rerender } = renderComponent({ aktivzitierung: initialValue });

    const zitatstelleInput = screen.getByRole("textbox", { name: "Zitatstelle" });
    expect(zitatstelleInput).toHaveValue("Initial Zitatstelle");

    await rerender({ aktivzitierung: newValue });

    expect(zitatstelleInput).toHaveValue("Updated via Prop");
  });

  it("shows validation error when periodikum is empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        zitatstelle: "AT 27.08.2024",
        verfasser: ["Müller"],
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });

  it("shows validation error when zitatstelle is empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        periodikum: "BAnz",
        verfasser: ["Müller"],
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });

  it("shows validation error when verfasser is empty and Übernehmen is clicked", async () => {
    const user = userEvent.setup();
    renderComponent({
      aktivzitierung: {
        id: "123",
        periodikum: "BAnz",
        zitatstelle: "AT 27.08.2024",
        verfasser: [],
      },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(screen.getByText("Pflichtfeld nicht befüllt")).toBeInTheDocument();
  });

  it("emits 'search' with current state when search button is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungUli = {
      id: "123",
      documentNumber: "DOC-123",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue, showSearchButton: true });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(emitted().search).toBeTruthy();
    expect(emitted().search![0]).toEqual([
      expect.objectContaining({
        id: "123",
        documentNumber: "DOC-123",
      }),
    ]);
  });
});
