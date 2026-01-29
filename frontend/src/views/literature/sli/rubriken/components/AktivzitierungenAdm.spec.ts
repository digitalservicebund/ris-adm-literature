import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import AktivzitierungenAdm from "./AktivzitierungenAdm.vue";

const globalStubs = {
  Aktivzitierung: {
    template: `<div data-testid="aktivzitierung">
      <slot name="item" :aktivzitierung="{ id: '1' }" />
      <slot name="input" :modelValue="{ id: '1' }" :onUpdateModelValue="() => {}" />
      <slot name="searchResult" :searchResult="{ documentNumber: '123' }" :isAdded="false" :onAdd="() => {}" />
    </div>`,
    props: ["modelValue", "fetchResultsFn", "requireCitationType", "citationTypeScope"],
    emits: ["update:modelValue"],
  },
  AktivzitierungAdmInput: { template: `<div data-testid="adm-input"/>` },
  AktivzitierungAdmItem: { template: `<div data-testid="adm-item"/>` },
  AktivzitierungAdmSearchResult: { template: `<div data-testid="adm-search-result"/>` },
};

describe("AktivzitierungenAdm", () => {
  it("renders correctly", () => {
    render(AktivzitierungenAdm, {
      global: { stubs: globalStubs },
      props: { modelValue: [] },
    });

    expect(
      screen.getByRole("heading", { name: /aktivzitierung \(verwaltungsvorschrift\)/i }),
    ).toBeInTheDocument();

    expect(screen.getByTestId("aktivzitierung")).toBeInTheDocument();

    expect(screen.getByTestId("adm-item")).toBeInTheDocument();
    expect(screen.getByTestId("adm-input")).toBeInTheDocument();
    expect(screen.getByTestId("adm-search-result")).toBeInTheDocument();
  });
});
