import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import AktivzitierungUliSearchResult from "./AktivzitierungUliSearchResult.vue";
import { uliDocumentUnitFixture } from "@/testing/fixtures/uliDocumentUnit.fixture";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";

describe("AktivzitierungUliSearchResult", () => {
  it("renders correctly with basic fixture", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          ...uliDocumentUnitFixture,
          documentNumber: "ULI-001",
          verfasser: ["Müller"],
          fundstellen: ["NJW 2024, 123"],
          dokumenttypen: [],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("Müller, NJW 2024, 123 | ULI-001")).toBeInTheDocument();
  });

  it("renders correctly all fields (Authors, Fundstellen, DocNum, Title)", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          id: "test-id",
          documentNumber: "KSLS054920710",
          titel: "TheHauptTitle",
          verfasser: ["Müller", "Zimmermann"],
          fundstellen: ["NJW 2024, 123", "BeckRS 2024, 456"],
        },
        isAdded: false,
      },
    });

    expect(
      screen.getByText("Müller, Zimmermann, NJW 2024, 123, BeckRS 2024, 456 | KSLS054920710"),
    ).toBeInTheDocument();
    expect(screen.getByText("TheHauptTitle")).toBeInTheDocument();
  });

  it("renders correctly when authors (verfasser) are missing", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          id: "id1",
          documentNumber: "DOC-123",
          fundstellen: ["NJW 2023, 1"],
          titel: "Title without author",
          verfasser: [],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("NJW 2023, 1 | DOC-123")).toBeInTheDocument();
  });

  it("renders only documentNumber when verfasser and fundstellen are missing", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          id: "id3",
          documentNumber: "DOC-ONLY",
          titel: "Minimal Entry",
          verfasser: [],
          fundstellen: [],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("DOC-ONLY")).toBeInTheDocument();
    expect(screen.getByText("Minimal Entry")).toBeInTheDocument();
  });

  it("emits add with mapped searchResult when clicking the + button", async () => {
    const user = userEvent.setup();
    const searchResult = {
      id: "id-add",
      documentNumber: "DOC-ADD",
      titel: "Some title",
      verfasser: ["Name 1"],
      fundstellen: ["Ref 1"],
    };

    const { emitted } = render(AktivzitierungUliSearchResult, {
      props: { searchResult, isAdded: false },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung hinzufügen" }));

    expect(emitted().add).toBeTruthy();
    const payload = (emitted().add as [AktivzitierungUli[]])[0][0];
    expect(payload!.documentNumber).toBe("DOC-ADD");
  });

  it("disables button and shows 'Bereits hinzugefügt' tag", async () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: { id: "1", documentNumber: "DOC-1", titel: "Title" },
        isAdded: true,
      },
    });

    expect(screen.getByText("Bereits hinzugefügt")).toBeInTheDocument();
    const button = screen.getByRole("button", { name: "Aktivzitierung hinzufügen" });
    expect(button).toBeDisabled();
  });

  it("maps document type names to abbreviations and includes them in parentheses", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          id: "id-mapped",
          documentNumber: "DOC-123",
          verfasser: ["Name"],
          fundstellen: ["Ref"],
          dokumenttypen: ["Bibliographie"],
          titel: "Title",
        },
        isAdded: false,
        documentTypeNameToAbbreviation: new Map([["Bibliographie", "Bib"]]),
      },
    });

    expect(screen.getByText("Name, Ref (Bib) | DOC-123")).toBeInTheDocument();
  });

  it("shows only mapped doc types when verfasser/fundstellen are missing", () => {
    render(AktivzitierungUliSearchResult, {
      props: {
        searchResult: {
          id: "id-mapped-no-meta",
          documentNumber: "DOC-ONLY",
          dokumenttypen: ["Bibliographie"],
          titel: "Minimal",
          verfasser: [],
          fundstellen: [],
        },
        isAdded: false,
        documentTypeNameToAbbreviation: new Map([["Bibliographie", "Bib"]]),
      },
    });

    expect(screen.getByText("(Bib) | DOC-ONLY")).toBeInTheDocument();
  });
});
