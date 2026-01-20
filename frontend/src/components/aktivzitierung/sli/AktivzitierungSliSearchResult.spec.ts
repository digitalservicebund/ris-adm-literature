import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import AktivzitierungSearchResult from "./AktivzitierungSliSearchResult.vue";
import { sliDocUnitListItemFixture } from "@/testing/fixtures/sliDocumentUnit.fixture";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";

describe("Aktivzitierung search result", () => {
  it("renders correctly", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: sliDocUnitListItemFixture,
        isAdded: false,
      },
    });

    expect(screen.getByText("2025 | KSLS054920710")).toBeInTheDocument();
  });

  it("renders correctly all fields (Year, Authors, DocNum, Title)", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "searchResultTestId",
          documentNumber: "KSLS054920710",
          veroeffentlichungsjahr: "2025",
          titel: "TheHauptTitle",
          verfasser: ["Müller", "Zimmermann"],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("2025, Müller, Zimmermann | KSLS054920710")).toBeInTheDocument();
    expect(screen.getByText("TheHauptTitle")).toBeInTheDocument();
  });

  it("renders correctly when authors (verfasser) are missing", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "id1",
          documentNumber: "DOC-12345",
          veroeffentlichungsjahr: "2023",
          titel: "Book without known author",
          verfasser: [],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("2023 | DOC-12345")).toBeInTheDocument();
    expect(screen.getByText("Book without known author")).toBeInTheDocument();
  });

  it("renders correctly when publication year (veroeffentlichungsjahr) is missing", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "id2",
          documentNumber: "DOC-67890",
          titel: "Report by a known group",
          verfasser: ["Research Team A"],
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("Research Team A | DOC-67890")).toBeInTheDocument();
    expect(screen.getByText("Report by a known group")).toBeInTheDocument();
  });

  it("renders only documentNumber when year and authors are missing", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "id3",
          documentNumber: "DOC-ONLY",
          titel: "Minimal Entry",
        },
        isAdded: false,
      },
    });

    expect(screen.getByText("DOC-ONLY")).toBeInTheDocument();
    expect(screen.getByText("Minimal Entry")).toBeInTheDocument();
  });

  it("emits add with the searchResult when clicking the + button", async () => {
    const user = userEvent.setup();
    const searchResult = {
      id: "id-add",
      documentNumber: "DOC-ADD",
      veroeffentlichungsjahr: "2025",
      titel: "Some title",
      verfasser: ["Name 1"],
    };

    const { emitted } = render(AktivzitierungSearchResult, {
      props: { searchResult, isAdded: false },
    });

    await user.click(screen.getByRole("button", { name: "Aktivzitierung hinzufügen" }));

    expect(emitted().add).toBeTruthy();
    const payload = (emitted().add as [AktivzitierungSli[]])[0][0];
    expect(payload!.documentNumber).toBe("DOC-ADD");
  });

  it("disables button and shows tag when isAdded prop is true", async () => {
    const user = userEvent.setup();
    const searchResult = {
      id: "id-added",
      documentNumber: "DOC-ADDED",
      titel: "Already Added Title",
    };

    const { emitted } = render(AktivzitierungSearchResult, {
      props: {
        searchResult,
        isAdded: true,
      },
    });

    expect(screen.getByText("Bereits hinzugefügt")).toBeInTheDocument();

    const button = screen.getByRole("button", { name: "Aktivzitierung hinzufügen" });
    expect(button).toBeDisabled();

    await user.click(button);
    expect(emitted().add).toBeFalsy();
  });

  it("maps document type names to abbreviations and shows them with year/authors", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "id-mapped",
          documentNumber: "DOC-123",
          veroeffentlichungsjahr: "2025",
          verfasser: ["Name"],
          dokumenttypen: ["Bibliographie"],
          titel: "Title",
        },
        isAdded: false,
        documentTypeNameToAbbreviation: new Map([["Bibliographie", "Bib"]]),
      },
    });

    expect(screen.getByText("2025, Name (Bib) | DOC-123")).toBeInTheDocument();
    expect(screen.getByText("Title")).toBeInTheDocument();
  });

  it("shows only mapped doc types when year/authors are missing", () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: "id-mapped-no-meta",
          documentNumber: "DOC-ONLY",
          dokumenttypen: ["Bibliographie"],
          titel: "Minimal",
        },
        isAdded: false,
        documentTypeNameToAbbreviation: new Map([["Bibliographie", "Bib"]]),
      },
    });

    expect(screen.getByText("(Bib) | DOC-ONLY")).toBeInTheDocument();
    expect(screen.getByText("Minimal")).toBeInTheDocument();
  });
});
