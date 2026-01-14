import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/vue";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import AktivzitierungAdmItem from "./AktivzitierungAdmItem.vue";

const baseItem = { id: "test-id" };

const testCases = [
  {
    name: "should render all fields separated by comma",
    data: {
      ...baseItem,
      citationType: "Abgrenzung",
      normgeber: "BMJ",
      inkrafttretedatum: "2024-01-01",
      aktenzeichen: "Az 123",
      periodikum: "BGBl",
      zitatstelle: "I S. 10",
      dokumenttyp: "VO",
      documentNumber: "123-A",
    },
    expected: "Abgrenzung | BMJ, 01.01.2024, Az 123, BGBl I S. 10 (VO) | 123-A",
  },
  {
    name: "should render only citationType and documentNumber (ignoring citationType if docNum missing)",
    data: {
      ...baseItem,
      citationType: "Übernahme",
      documentNumber: "456-B",
    },
    expected: "Übernahme | 456-B",
  },
  {
    name: "should handle fundstelle with periodikum only",
    data: {
      ...baseItem,
      documentNumber: "123",
      periodikum: "NJW",
    },
    expected: "NJW | 123",
  },
  {
    name: "should render dokumenttyp in parentheses",
    data: {
      ...baseItem,
      documentNumber: "123",
      dokumenttyp: "Gesetz",
    },
    expected: "(Gesetz) | 123",
  },
  {
    name: "should render dokumenttyp and fundstelle combined",
    data: {
      ...baseItem,
      documentNumber: "123",
      periodikum: "BGBl",
      zitatstelle: "S. 45",
      dokumenttyp: "Anordnung",
    },
    expected: "BGBl S. 45 (Anordnung) | 123",
  },
  {
    name: "should render aktenzeichen without other basic parts",
    data: {
      ...baseItem,
      documentNumber: "123",
      aktenzeichen: "II ZR 12/23",
    },
    expected: "II ZR 12/23 | 123",
  },
  {
    name: "should render empty string when all fields including docNum are missing",
    data: {
      id: "uuid",
      documentNumber: "",
    },
    expected: "",
  },
  {
    name: "should render documentNumber alone if no meta parts exist",
    data: {
      ...baseItem,
      documentNumber: "ONLY-NUM",
    },
    expected: "ONLY-NUM",
  },
];

describe("AktivzitierungAdmItem (metaSummary)", () => {
  it.each(testCases)("$name", ({ data, expected }) => {
    render(AktivzitierungAdmItem, {
      props: { aktivzitierung: data as AktivzitierungAdm },
    });

    if (expected) {
      expect(screen.getByText(expected)).toBeInTheDocument();
    }
  });
});
