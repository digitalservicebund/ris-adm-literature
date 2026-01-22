import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import AktivzitierungenRechtsprechung from "./AktivzitierungenRechtsprechung.vue";

const globalStubs = {
  Aktivzitierung: {
    template: `<div data-testid="aktivzitierung">
      <slot name="item" :aktivzitierung="{ id: '1' }" />
      <slot name="input" :modelValue="{ id: '1' }" :onUpdateModelValue="() => {}" />
    </div>`,
    props: ["modelValue", "fetchResultsFn"],
    emits: ["update:modelValue"],
  },
  AktivzitierungRechtsprechungInput: { template: `<div data-testid="rechtsprechung-input"/>` },
  AktivzitierungRechtsprechungItem: { template: `<div data-testid="rechtsprechung-item"/>` },
};

describe("AktivzitierungenAdm", () => {
  it("renders correctly", () => {
    render(AktivzitierungenRechtsprechung, {
      global: { stubs: globalStubs },
      props: { modelValue: [] },
    });

    expect(
      screen.getByRole("heading", { name: /aktivzitierung \(Rechtsprechung\)/i }),
    ).toBeInTheDocument();

    expect(screen.getByTestId("aktivzitierung")).toBeInTheDocument();

    expect(screen.getByTestId("rechtsprechung-item")).toBeInTheDocument();
    expect(screen.getByTestId("rechtsprechung-input")).toBeInTheDocument();
  });
});
