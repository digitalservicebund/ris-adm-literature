import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/vue";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";
import AktivzitierungUliItem from "./AktivzitierungUliItem.vue";

const baseItem: AktivzitierungUli = { id: "test-id" };

const metaTestCases = [
  {
    name: "renders authors only when only authors are provided",
    data: {
      ...baseItem,
      verfasser: ["Müller", "Schmidt"],
    },
    expected: "Müller, Schmidt",
  },
  {
    name: "renders fundstelle (periodikum + zitatstelle) when both provided",
    data: {
      ...baseItem,
      periodikum: "BAnz",
      zitatstelle: "AT 27.08.2024",
    },
    expected: "BAnz AT 27.08.2024",
  },
  {
    name: "renders periodikum only when zitatstelle is missing",
    data: {
      ...baseItem,
      periodikum: "BAnz",
    },
    expected: "BAnz",
  },
  {
    name: "renders zitatstelle only when periodikum is missing",
    data: {
      ...baseItem,
      zitatstelle: "27.08.2024",
    },
    expected: "27.08.2024",
  },
  {
    name: "renders authors and fundstelle separated by comma",
    data: {
      ...baseItem,
      verfasser: ["Prof Dr Seyller"],
      periodikum: "BAnz",
      zitatstelle: "AT 27.08.2024",
    },
    expected: "Prof Dr Seyller, BAnz AT 27.08.2024",
  },
  {
    name: "renders document types in parentheses after main parts",
    data: {
      ...baseItem,
      documentNumber: "VALID1234567",
      verfasser: ["Müller"],
      periodikum: "BAnz",
      zitatstelle: "AT 27.08.2024",
      dokumenttypen: [
        { abbreviation: "Bib", name: "Bibliographie" },
        { abbreviation: "Dis", name: "Dissertation" },
      ],
    },
    expected: "Müller, BAnz AT 27.08.2024 (Bib, Dis) | VALID1234567",
  },
  {
    name: "renders only document types when no authors or fundstelle exist",
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
    name: "renders empty string when no authors, fundstelle or document types",
    data: {
      ...baseItem,
      verfasser: [],
      periodikum: undefined,
      zitatstelle: undefined,
      dokumenttypen: [],
    },
    expected: "",
  },
];

describe("AktivzitierungUliItem", () => {
  it.each(metaTestCases)("$name", ({ data, expected }) => {
    render(AktivzitierungUliItem, {
      props: { aktivzitierung: data as AktivzitierungUli },
    });

    if (expected) {
      expect(screen.getByText(expected)).toBeInTheDocument();
    } else {
      const summaryEl = screen.getByText("", { selector: ".ris-body1-regular" });
      expect(summaryEl).toBeInTheDocument();
      expect(summaryEl).toHaveTextContent("");
    }
  });

  it("renders title", () => {
    render(AktivzitierungUliItem, {
      props: { aktivzitierung: { id: "theId", titel: "TheTitle" } },
    });

    expect(screen.getByText("TheTitle")).toBeVisible();
  });
});
