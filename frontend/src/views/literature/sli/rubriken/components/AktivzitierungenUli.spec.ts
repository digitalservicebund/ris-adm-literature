import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import AktivzitierungenUli from "./AktivzitierungenUli.vue";

const globalStubs = {
  Aktivzitierung: {
    template: `<div data-testid="aktivzitierung">
      <slot name="item" :aktivzitierung="{ id: '1' }" />
      <slot name="input" :aktivzitierung="{ id: '1' }" :showCancelButton="false" :showDeleteButton="false" :onSave="() => {}" :onDelete="() => {}" :onCancel="() => {}" />
      <slot name="searchResult" :searchResult="{ id: '1', documentNumber: '123' }" :isAdded="false" :onAdd="() => {}" />
    </div>`,
    props: ["modelValue", "fetchResultsFn"],
    emits: ["update:modelValue"],
  },
  AktivzitierungUliInput: { template: `<div data-testid="input"/>` },
  AktivzitierungUliItem: { template: `<div data-testid="item"/>` },
};

describe("AktivzitierungenUli", () => {
  it("renders correctly", () => {
    render(AktivzitierungenUli, {
      global: { stubs: globalStubs },
      props: { modelValue: [] },
    });

    expect(
      screen.getByRole("heading", { name: "Aktivzitierung Literatur (unselbst.)" }),
    ).toBeInTheDocument();

    expect(screen.getByTestId("aktivzitierung")).toBeInTheDocument();
    expect(screen.getByTestId("item")).toBeInTheDocument();
    expect(screen.getByTestId("input")).toBeInTheDocument();
  });
});
