import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import Aktivzitierung from "./Aktivzitierung.vue";
import { ref, type Ref } from "vue";
import type { UseFetchReturn } from "@vueuse/core";

const addToastMock = vi.fn();
vi.mock("primevue", () => {
  // Mock 'useToast' hook
  const useToast = vi.fn(() => ({
    add: addToastMock,
  }));

  // Mock components used in the component's template
  const Button = {
    props: ["label", "severity", "size", "aria-label", "disabled", "class"],
    template: '<button><slot name="icon"></slot>{{ label }}</button>',
  };

  return {
    useToast,
    Button,
  };
});

// Dummy Aktivzitierung
type DummyT = { id: string; documentNumber?: string; title?: string };

// Dummy Search Result
type DummySearchResult = {
  id: string;
  documentNumber: string;
  title?: string;
};

type FetchResultsFunction = (
  page: Ref<number>,
  itemsPerPage: number,
  searchParams: Ref,
) => UseFetchReturn<DummySearchResult>;

const mockPagination = {
  firstRowIndex: ref(0),
  totalRows: ref(0),
  items: ref<DummySearchResult[]>([]),
  fetchPaginatedData: vi.fn(),
  isFetching: ref(false),
  error: ref<Error | null>(null),
};

vi.mock("@/composables/usePagination", () => ({
  usePagination: vi.fn(() => mockPagination),
}));

function renderComponent(props: { modelValue: DummyT[]; fetchResultsFn: FetchResultsFunction }) {
  return render(Aktivzitierung, {
    props: {
      modelValue: props.modelValue,
      fetchResultsFn: props.fetchResultsFn,
    },
    slots: {
      item: `
          <template #default="{ aktivzitierung }">
            <div data-testid="item">{{aktivzitierung.citationType}} {{aktivzitierung.title}} {{ aktivzitierung.documentNumber }}</div>
          </template>`,
      input: `
        <template #input="{
          aktivzitierung,
          onSave,
          onCancel,
          onSearch,
          showCancelButton
        }">
          <input
            data-testid="input-title"
            :value="aktivzitierung?.title ?? ''"
            @input="e => local = { ...(aktivzitierung ?? { id: 'tmp' }), title: e.target.value }"
          />
          <input
            data-testid="input-citationType"
            :value="aktivzitierung?.citationType ?? ''"
            @input="e => local = { ...(local ?? aktivzitierung ?? { id: 'tmp' }), citationType: e.target.value }"
          />

          <button @click="onSave(local)">Aktivzitierung übernehmen</button>
          <button @click="onCancel">Abbrechen</button>
          <button @click="onSearch(local)">Dokumente Suchen</button>
        </template>
      `,
      searchResult: `
        <template #default="{ searchResult, isAdded, onAdd }">
          <div data-testid="search-result-title">{{ searchResult.title }}</div>
          <button
            data-testid="search-result-add-btn"
            :disabled="isAdded"
            @click="onAdd(searchResult)"
          >
            Add
          </button>
        </template>
        `,
    },
  });
}

describe("Aktivzitierung", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    mockPagination.items.value = [];
    mockPagination.totalRows.value = 0;
    mockPagination.firstRowIndex.value = 0;
    mockPagination.isFetching.value = false;
  });

  it("renders creation panel if list is empty", () => {
    renderComponent({ modelValue: [], fetchResultsFn: vi.fn() });

    expect(screen.getByTestId("input-title")).toBeInTheDocument();
    expect(screen.getByTestId("input-citationType")).toBeInTheDocument();
  });

  it("renders list items if list has entries", async () => {
    renderComponent({
      modelValue: [{ id: "1", documentNumber: "DOC123" }],
      fetchResultsFn: vi.fn(),
    });

    // The item slot should render documentNumber
    expect(screen.getByText("DOC123")).toBeInTheDocument();
  });

  it("renders initial list items and allows adding a new item", async () => {
    const user = userEvent.setup();
    const initialList: DummyT[] = [{ id: "1", title: "DOC123" }];

    // Use v-model via "modelValue" prop + "update:modelValue" emit
    const model = {
      value: [...initialList],
    };

    const { emitted } = renderComponent({ modelValue: model.value, fetchResultsFn: vi.fn() });
    expect(screen.getByText("DOC123")).toBeInTheDocument();

    const addButton = screen.getByRole("button", { name: "Weitere Angabe" });
    await user.click(addButton);

    // After click, creation panel (input textbox) should appear
    const input = screen.getByTestId("input-title");
    expect(input).toBeInTheDocument();

    // Type a new title
    await user.type(input, "NEWDOC");

    // Click "Übernehmen" button
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    // Check that the component emitted updated list
    const updateEvents = emitted()["update:modelValue"];
    expect(updateEvents).toBeTruthy();

    // The last emitted value should contain the new document
    const events = emitted()["update:modelValue"] as Array<[DummyT[]]>;
    const finalPayload = events[events.length - 1]![0];

    expect(finalPayload.map((i) => i.title)).toContain("NEWDOC");
  });

  it('opens creation panel when "Weitere Angabe" button is clicked', async () => {
    const user = userEvent.setup();

    renderComponent({ modelValue: [{ id: "1" }], fetchResultsFn: vi.fn() });

    // Click "Weitere Angabe" button
    const addButton = screen.getByRole("button", { name: "Weitere Angabe" });
    await user.click(addButton);

    // After click, creation panel (input textbox) should appear
    expect(screen.getByTestId("input-title")).toBeInTheDocument();
  });

  it("handleEditStart sets editingItemId and closes creation panel", async () => {
    const user = userEvent.setup();

    renderComponent({ modelValue: [{ id: "1" }], fetchResultsFn: vi.fn() });

    // Click "edit" button of first item
    const editButtons = screen.getAllByRole("button", { name: "Eintrag bearbeiten" });
    await user.click(editButtons[0]!);

    // The first item should now be in editing mode → save button visible
    expect(screen.getByRole("button", { name: "Aktivzitierung übernehmen" })).toBeInTheDocument();
  });

  it("handleEditEnd clears editingItemId", async () => {
    const user = userEvent.setup();

    renderComponent({ modelValue: [{ id: "1" }], fetchResultsFn: vi.fn() });

    // Start editing first item
    const editButton = screen.getAllByRole("button", { name: "Eintrag bearbeiten" })[0]!;
    await user.click(editButton);

    // Click "Abbrechen" → handleCancelEdit → should clear editingItemId
    const cancelButton = screen.getByRole("button", { name: "Abbrechen" });
    if (cancelButton) await user.click(cancelButton);

    // The save button should no longer exist
    expect(
      screen.queryByRole("button", { name: "Aktivzitierung übernehmen" }),
    ).not.toBeInTheDocument();
  });

  it("handleCancelEdit clears editingItemId", async () => {
    const user = userEvent.setup();

    renderComponent({ modelValue: [{ id: "1" }], fetchResultsFn: vi.fn() });

    const editButton = screen.getAllByRole("button", { name: "Eintrag bearbeiten" })[0]!;
    await user.click(editButton);

    const cancelButton = screen.getByRole("button", { name: "Abbrechen" });
    if (cancelButton) await user.click(cancelButton);

    // Editing mode ended → save button gone
    expect(
      screen.queryByRole("button", { name: "Aktivzitierung übernehmen" }),
    ).not.toBeInTheDocument();
  });

  it("handleUpdateItem calls onUpdateItem and ends editing", async () => {
    const user = userEvent.setup();
    const newTitle = "UPDATED_DOC";

    renderComponent({ modelValue: [{ id: "1" }], fetchResultsFn: vi.fn() });

    // Start editing first item
    const editButton = screen.getAllByRole("button", { name: "Eintrag bearbeiten" })[0]!;
    await user.click(editButton);

    // Update the title
    const input = screen.getByTestId("input-title");
    await user.clear(input);
    await user.type(input, newTitle);

    // Click save → triggers handleUpdateItem
    const saveButton = screen.getByRole("button", { name: "Aktivzitierung übernehmen" });
    await user.click(saveButton);

    expect(screen.getByText("UPDATED_DOC")).toBeVisible();
    expect(
      screen.queryByRole("button", { name: "Aktivzitierung übernehmen" }),
    ).not.toBeInTheDocument();
  });

  it("allows only one aktivzitierung entry to be in edit mode at a time", async () => {
    const user = userEvent.setup();
    const twoEntries = [
      { id: "aktiv-1", title: "doc1" },
      { id: "aktiv-2", title: "doc2" },
    ];

    renderComponent({ modelValue: twoEntries, fetchResultsFn: vi.fn() });

    expect(screen.getByText("doc1")).toBeInTheDocument();
    expect(screen.getByText("doc2")).toBeInTheDocument();

    const editButtons = screen.getAllByRole("button", { name: "Eintrag bearbeiten" });

    await user.click(editButtons[0]!);

    // // then – only one edit form is visible, prefilled with first title
    expect(screen.getByTestId("input-title")).toBeInTheDocument();
    expect(screen.getByTestId("input-title")).toHaveValue("doc1");

    // // when – open edit for second entry
    await user.click(editButtons[1]!);

    // // then – still only one edit form, now for second entry
    expect(screen.getByTestId("input-title")).toBeInTheDocument();
    expect(screen.getByTestId("input-title")).toHaveValue("doc2");
    expect(screen.queryByDisplayValue("doc1")).not.toBeInTheDocument();
  });

  it("hides the creation panel and the add button when an entry is in edit mode", async () => {
    const user = userEvent.setup();
    const twoEntries = [
      { id: "aktiv-1", title: "Titel 1" },
      { id: "aktiv-2", title: "Titel 2" },
    ];

    renderComponent({ modelValue: twoEntries, fetchResultsFn: vi.fn() });

    const editButtons = screen.getAllByRole("button", { name: "Eintrag bearbeiten" });
    const firstEditButton = editButtons[0]!;
    await user.click(firstEditButton);

    const titleInput = screen.getByTestId("input-title");
    expect(titleInput).toHaveValue("Titel 1");

    expect(screen.queryByRole("button", { name: "Weitere Angabe" })).not.toBeInTheDocument();
  });

  it("closes edit mode after cancelling an edited entry", async () => {
    const user = userEvent.setup();

    const existing = [{ id: "aktiv-1", titel: "Alt" }];

    renderComponent({ modelValue: existing, fetchResultsFn: vi.fn() });

    const editButtons = screen.getAllByRole("button", { name: "Eintrag bearbeiten" });
    const firstEditButton = editButtons[0]!;
    await user.click(firstEditButton);

    const cancelButton = screen.getByRole("button", { name: "Abbrechen" });
    await user.click(cancelButton);

    expect(screen.getByRole("button", { name: "Eintrag bearbeiten" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Abbrechen" })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Übernehmen" })).not.toBeInTheDocument();
  });

  it("closes the creation panel when starting to edit an existing entry", async () => {
    const user = userEvent.setup();

    const existing = [{ id: "aktiv-1", title: "Alt" }];

    renderComponent({ modelValue: existing, fetchResultsFn: vi.fn() });

    await user.click(screen.getByRole("button", { name: "Weitere Angabe" }));
    expect(screen.getByTestId("input-title")).toHaveValue("");

    await user.click(screen.getByRole("button", { name: "Eintrag bearbeiten" }));

    const titleInput = screen.getByTestId("input-title");
    expect(titleInput).toHaveValue("Alt");
    expect(screen.queryByRole("button", { name: "Weitere Angabe" })).not.toBeInTheDocument();
  });

  it("triggers a search, shows results and handles pagination", async () => {
    mockPagination.fetchPaginatedData.mockImplementation(async () => {
      mockPagination.items.value = [
        { id: "uuid-1", documentNumber: "DOC-123", title: "Found Item" },
      ];
      mockPagination.totalRows.value = 20;
    });

    const user = userEvent.setup();
    const fetchResultsFn = vi.fn();

    renderComponent({ modelValue: [], fetchResultsFn });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(mockPagination.fetchPaginatedData).toHaveBeenCalledTimes(1);
    expect(await screen.findByText("Passende Suchergebnisse:")).toBeInTheDocument();
    expect(screen.getByText("Found Item")).toBeInTheDocument();

    const nextButton = screen.getByLabelText("Weiter");
    await user.click(nextButton);

    expect(mockPagination.fetchPaginatedData).toHaveBeenCalledTimes(2);
    expect(screen.getByText("Seite 2")).toBeInTheDocument();
  });

  it("should show an error toast on fetching error", async () => {
    mockPagination.fetchPaginatedData.mockImplementation(async () => {
      mockPagination.items.value = [];
      mockPagination.totalRows.value = 20;
      mockPagination.error.value = new Error("fetch error");
    });

    const user = userEvent.setup();
    const fetchResultsFn = vi.fn();

    renderComponent({ modelValue: [], fetchResultsFn });

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    expect(mockPagination.fetchPaginatedData).toHaveBeenCalledTimes(1);
    expect(addToastMock).toHaveBeenCalledWith({
      severity: "error",
      summary: "Dokumentationseinheiten konnten nicht geladen werden.",
    });
  });

  it("persists the citationType after triggering a search and so it is added to the aktivzitierung from search result later", async () => {
    mockPagination.fetchPaginatedData.mockImplementation(async () => {
      mockPagination.items.value = [
        { id: "uuid-1", documentNumber: "DOC-123", title: "Found Item" },
      ];
      mockPagination.totalRows.value = 20;
    });

    const user = userEvent.setup();
    const fetchResultsFn = vi.fn();

    renderComponent({ modelValue: [], fetchResultsFn });

    const input = screen.getByTestId("input-citationType");
    await user.type(input, "Anordnung");

    const searchButton = screen.getByRole("button", { name: "Dokumente Suchen" });
    await user.click(searchButton);

    await user.click(screen.getByRole("button", { name: "Add" }));

    expect(screen.getByText("Anordnung Found Item DOC-123")).toBeInTheDocument();
  });

  it("prevents adding the same document number twice", async () => {
    const user = userEvent.setup();
    const existing = [{ id: "1", documentNumber: "DOC-DUP", title: "Existing" }];
    mockPagination.items.value = [
      { id: "res-1", documentNumber: "DOC-DUP", title: "Search Result" },
    ];

    renderComponent({ modelValue: existing, fetchResultsFn: vi.fn() });

    await user.click(screen.getByRole("button", { name: "Weitere Angabe" }));
    await user.click(screen.getByRole("button", { name: "Dokumente Suchen" }));

    const addBtn = screen.getByRole("button", { name: "Add" });
    expect(addBtn).toBeDisabled();
  });

  it("removes documentNumber from items when they are added manually (addItem/updateItem)", async () => {
    const user = userEvent.setup();
    const { emitted } = renderComponent({ modelValue: [], fetchResultsFn: vi.fn() });

    const input = screen.getByTestId("input-title");
    await user.type(input, "New Title");

    // Click Save (addItem)
    await user.click(screen.getByRole("button", { name: "Aktivzitierung übernehmen" }));

    const events = emitted()["update:modelValue"] as Array<[DummyT[]]>;
    const newItem = events[0]![0][0];

    expect(newItem).not.toHaveProperty("documentNumber");
    expect(newItem!.title).toBe("New Title");
  });
});
