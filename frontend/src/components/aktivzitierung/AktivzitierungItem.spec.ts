import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect } from "vitest";
import AktivzitierungItem from "./AktivzitierungItem.vue";

type DummyT = { id: string; documentNumber?: string; citationType?: string };

function renderComponent(props?: { aktivzitierung: DummyT; isEditing: boolean }) {
  return render(AktivzitierungItem, {
    props,
    slots: {
      item: `
          <template #item="{ aktivzitierung }">
            <div data-testid="doc-number">{{ Object.values(aktivzitierung).join(',') }}</div>
          </template>`,
      input: `
          <template #input="{ aktivzitierung, onSave, onCancel, onDelete }">
            <div data-testid="input-slot-wrapper">
              <button @click="onSave({ ...aktivzitierung, title: 'NEW_ITEM' })">Übernehmen</button>
              <button @click="onCancel">Abbrechen</button>
              <button @click="onDelete(aktivzitierung.id)">Löschen</button>
            </div>
          </template>
          `,
    },
  });
}

describe("AktivzitierungAdmItem", () => {
  const item: DummyT = { id: "123", citationType: "Anmerkung" };

  it("renders read-only view when isEditing is false", () => {
    renderComponent({ aktivzitierung: item, isEditing: false });

    // The slot content should render
    const div = screen.getByTestId("doc-number");
    expect(div).toBeInTheDocument();
    expect(div).toHaveTextContent("Anmerkung");

    // Edit button exists
    const editButton = screen.getByRole("button", { name: "Eintrag bearbeiten" });
    expect(editButton).toBeInTheDocument();

    // Remove button does not exist
    const remove = screen.queryByRole("button", { name: "Eintrag löschen" });
    expect(remove).not.toBeInTheDocument();
  });

  it("edit button is hidden and remove button is shown when aktivzitierung has a doc number", () => {
    renderComponent({
      aktivzitierung: { id: "123", citationType: "Anmerkung", documentNumber: "DOC123" },
      isEditing: false,
    });

    const div = screen.getByTestId("doc-number");
    expect(div).toHaveTextContent("DOC123");
    expect(div).toHaveTextContent("Anmerkung");

    const editButton = screen.queryByRole("button", { name: "Eintrag bearbeiten" });
    expect(editButton).not.toBeInTheDocument();

    const saveButton = screen.getByRole("button", { name: "Eintrag löschen" });
    expect(saveButton).toBeInTheDocument();
  });

  it("emits editStart when edit button is clicked", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({ aktivzitierung: item, isEditing: false });

    const editButton = screen.getByRole("button", { name: "Eintrag bearbeiten" });
    await user.click(editButton);

    const events = emitted().editStart as Array<[string]>;
    expect(events).toHaveLength(1);
    expect(events[0]![0]).toBe("123");
  });

  it("renders AktivzitierungInput when isEditing is true", () => {
    renderComponent({ aktivzitierung: item, isEditing: true });

    // Save button inside AktivzitierungInput exists
    const saveButton = screen.getByRole("button", { name: "Übernehmen" });
    expect(saveButton).toBeInTheDocument();
  });

  it("passes save event through the input slot", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({ aktivzitierung: item, isEditing: true });

    // Click 'Übernehmen' (defined in our slot stub)
    await user.click(screen.getByRole("button", { name: "Übernehmen" }));

    // Check emitted save (renamed from update)
    expect(emitted().save).toBeTruthy();
    expect(emitted().save[0][0]).toEqual(
      expect.objectContaining({
        id: "123",
        title: "NEW_ITEM",
      }),
    );
  });

  it("emits delete for an aktivzitierung coming from search", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({
      aktivzitierung: { id: "123", citationType: "Anmerkung", documentNumber: "DOC123" },
      isEditing: false,
    });

    const deleteButton = screen.getByRole("button", { name: "Eintrag löschen" });
    await user.click(deleteButton);

    const emits = emitted();

    // Verify events were emitted
    expect(emits.delete).toBeTruthy();
    expect(emits.delete![0]).toEqual(["123"]); // emitted id

    expect(emits.cancelEdit).toBeTruthy();
    expect(emits.cancelEdit?.length).toBe(1);
  });
});
