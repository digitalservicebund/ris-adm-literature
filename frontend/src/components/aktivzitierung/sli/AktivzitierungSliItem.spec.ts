import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/vue";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";
import AktivzitierungSliItem from "./AktivzitierungSliItem.vue";

const baseItem: AktivzitierungSli = { id: "test-id" };

const metaTestCases = [
  {
    name: "renders year only when only year is provided",
    data: {
      ...baseItem,
      veroeffentlichungsJahr: "2024",
    },
    expected: "2024",
  },
  {
    name: "renders authors only when only authors are provided",
    data: {
      ...baseItem,
      verfasser: ["Müller", "Schmidt"],
    },
    expected: "Müller, Schmidt",
  },
  {
    name: "renders year and authors separated by comma",
    data: {
      ...baseItem,
      veroeffentlichungsJahr: "2024",
      verfasser: ["Müller", "Schmidt"],
    },
    expected: "2024, Müller, Schmidt",
  },
  {
    name: "trims authors and removes trailing commas",
    data: {
      ...baseItem,
      verfasser: ["  Müller,", "  ", "Schmidt  "],
    },
    expected: "Müller, Schmidt",
  },
  {
    name: "renders document types in parentheses after main parts",
    data: {
      ...baseItem,
      veroeffentlichungsJahr: "2024",
      verfasser: ["Müller"],
      dokumenttypen: [
        { abbreviation: "Bib", name: "Bibliographie" },
        { abbreviation: "Dis", name: "Dissertation" },
      ],
    },
    expected: "2024, Müller (Bib, Dis)",
  },
  {
    name: "renders only document types when no year or authors exist",
    data: {
      ...baseItem,
      dokumenttypen: [
        { abbreviation: "Bib", name: "Bibliographie" },
        { abbreviation: "Dis", name: "Dissertation" },
      ],
    },
    expected: "(Bib, Dis)",
  },
  {
    name: "renders empty string when no year, no authors and no document types",
    data: {
      ...baseItem,
      veroeffentlichungsJahr: undefined,
      verfasser: [],
      dokumenttypen: [],
    },
    expected: "",
  },
];

describe("AktivzitierungSliItem (metaSummary)", () => {
  it.each(metaTestCases)("$name", ({ data, expected }) => {
    render(AktivzitierungSliItem, {
      props: { aktivzitierung: data as AktivzitierungSli },
    });

    if (expected) {
      expect(screen.getByText(expected)).toBeInTheDocument();
    } else {
      // metaSummary renders as empty string → element exists but has no content
      const summaryEl = screen.getByText("", { selector: ".ris-body1-regular" });
      expect(summaryEl).toBeInTheDocument();
      expect(summaryEl).toHaveTextContent("");
    }
  });

  it("renders the titleSummary (titel) in the second line", () => {
    render(AktivzitierungSliItem, {
      props: {
        aktivzitierung: {
          ...baseItem,
          titel: "Mein SLI‑Titel",
        },
      },
    });

    expect(screen.getByText("Mein SLI‑Titel")).toBeInTheDocument();
  });

  it("renders empty string for titleSummary when titel is missing", () => {
    render(AktivzitierungSliItem, {
      props: {
        aktivzitierung: {
          ...baseItem,
          titel: undefined,
        },
      },
    });

    const titleEl = screen.getByText("", { selector: ".ris-body2-regular" });
    expect(titleEl).toBeInTheDocument();
    expect(titleEl).toHaveTextContent("");
  });

  it("renders filled icon when documentNumber exists", () => {
    render(AktivzitierungSliItem, {
      props: {
        aktivzitierung: {
          ...baseItem,
          documentNumber: "DOC-123",
        },
      },
    });

    expect(screen.getByTestId("icon-filled")).toBeInTheDocument();
    expect(screen.queryByTestId("icon-outline")).not.toBeInTheDocument();
  });

  it("renders outline icon when documentNumber is missing", () => {
    render(AktivzitierungSliItem, {
      props: {
        aktivzitierung: {
          ...baseItem,
          documentNumber: undefined,
        },
      },
    });

    expect(screen.getByTestId("icon-outline")).toBeInTheDocument();
    expect(screen.queryByTestId("icon-filled")).not.toBeInTheDocument();
  });
});
