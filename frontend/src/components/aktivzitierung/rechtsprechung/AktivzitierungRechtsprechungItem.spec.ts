import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/vue";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import AktivzitierungAdmItem from "./AktivzitierungRechtsprechungItem.vue";

const baseItem = { id: "test-id" };

const testCases = [
  {
    name: "should render all fields separated by comma",
    data: {
      ...baseItem,
      citationType: "Ablehnung",
      gericht: "BGH",
      entscheidungsdatum: "2024-01-01",
      aktenzeichen: "3 StR 245/04",
      dokumenttyp: "Urteil",
      documentNumber: "123-A",
    },
    expected: "Ablehnung | BGH, 01.01.2024, 3 StR 245/04 (Urteil) | 123-A",
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
    name: "should render dokumenttyp in parentheses",
    data: {
      ...baseItem,
      documentNumber: "123",
      dokumenttyp: "Urteil",
    },
    expected: "(Urteil) | 123",
  },
  {
    name: "should render dokumenttyp and entscheidungsdatum combined",
    data: {
      ...baseItem,
      documentNumber: "123",
      entscheidungsdatum: "2024-01-01",
      dokumenttyp: "Anordnung",
    },
    expected: "01.01.2024 (Anordnung) | 123",
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

describe("AktivzitierungRechtsprechungItem (metaSummary)", () => {
  it.each(testCases)("$name", ({ data, expected }) => {
    render(AktivzitierungAdmItem, {
      props: { aktivzitierung: data as AktivzitierungAdm },
    });

    if (expected) {
      expect(screen.getByText(expected)).toBeInTheDocument();
    }
  });
});
