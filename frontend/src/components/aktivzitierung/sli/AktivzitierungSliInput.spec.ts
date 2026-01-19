import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import AktivzitierungSliInput from "./AktivzitierungSliInput.vue";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";

function renderComponent(props: {
  aktivzitierung?: AktivzitierungSli;
  showCancelButton?: boolean;
  showDeleteButton?: boolean;
}) {
  return render(AktivzitierungSliInput, {
    props: {
      aktivzitierung: props.aktivzitierung,
      showCancelButton: props.showCancelButton ?? false,
      showDeleteButton: props.showDeleteButton ?? false,
    },
    global: {
      stubs: {
        DokumentTyp: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Dokumenttyp"
              @input="$emit('update:modelValue', [{
                abbreviation: $event.target.value,
                name: $event.target.value
              }])"
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

describe("AktivzitierungSliInput", () => {
  it("emits 'save' with updated title when 'Übernehmen' is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      titel: "Old title",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", {
      name: "Hauptsachtitel / Dokumentarischer Titel",
    });
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });

    await user.clear(input);
    await user.type(input, "New title");

    expect(emitted()["update:modelValue"]).toBeUndefined();

    await user.click(saveButton);

    expect(emitted().save![0]).toEqual([
      expect.objectContaining({
        id: "123",
        titel: "New title",
      }),
    ]);
  });

  it("emits 'save' with updated year", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "123", veroeffentlichungsJahr: "2020" },
    });

    const input = screen.getByRole("textbox", { name: "Veröffentlichungsjahr" });
    await user.clear(input);
    await user.type(input, "2024");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(emitted().save).toBeTruthy();

    const saveEvents = emitted().save as Array<[AktivzitierungSli]>;
    const lastEventPayload = saveEvents[0]![0];

    expect(lastEventPayload.veroeffentlichungsJahr).toBe("2024");
  });

  it("emits 'save' with updated dokumenttypen", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "123", dokumenttypen: [] },
    });

    const input = screen.getByRole("textbox", { name: "Dokumenttyp" });
    await user.type(input, "Bib");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(emitted().save).toBeTruthy();

    const saveEvents = emitted().save as Array<[AktivzitierungSli]>;
    const lastEventPayload = saveEvents[0]![0];

    expect(lastEventPayload.dokumenttypen).toEqual([{ abbreviation: "Bib", name: "Bib" }]);
  });

  it("emits 'save' with updated verfasser", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "123", verfasser: [] },
    });

    const input = screen.getByRole("textbox", { name: "Verfasser/in" });
    await user.type(input, "Müller");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    const saveEvents = emitted().save as Array<[AktivzitierungSli]>;
    const lastEventPayload = saveEvents[0]![0];

    expect(lastEventPayload.verfasser).toEqual(["Müller"]);
  });

  it("disables the 'Übernehmen' button when the form is empty", () => {
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

  it("resets local state after saving a new entry", async () => {
    const user = userEvent.setup();
    vi.stubGlobal("crypto", { randomUUID: () => "new-id" });

    renderComponent({ aktivzitierung: undefined });

    const input = screen.getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" });
    await user.type(input, "Fresh Title");

    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    expect(input).toHaveValue("");
  });

  it("emits 'search' with current state when search button is clicked", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      titel: "Search Query",
    };

    const { emitted } = renderComponent({ aktivzitierung: initialValue });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(emitted().search).toBeTruthy();
    expect(emitted().search![0]).toEqual([
      expect.objectContaining({
        id: "123",
        titel: "Search Query",
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
    const initialValue: AktivzitierungSli = { id: "123", titel: "Initial Title" };
    const newValue: AktivzitierungSli = { id: "123", titel: "Updated via Prop" };

    const { rerender } = renderComponent({ aktivzitierung: initialValue });

    const input = screen.getByRole("textbox", {
      name: "Hauptsachtitel / Dokumentarischer Titel",
    });
    expect(input).toHaveValue("Initial Title");

    await rerender({ aktivzitierung: newValue });

    expect(input).toHaveValue("Updated via Prop");
  });
});
