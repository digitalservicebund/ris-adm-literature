import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import { describe, it, expect } from "vitest";
import AktivzitierungSliInput from "./AktivzitierungSliInput.vue";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";

function renderComponent(modelValue: AktivzitierungSli) {
  return render(AktivzitierungSliInput, {
    props: {
      modelValue,
    },
    global: {
      stubs: {
        DokumentTyp: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Dokumenttyp"
              @input="$emit('update:modelValue', [{
                abbreviation: $event.target.value,
                name: $event.target.value
              }])"
            />
          `,
        },
        RisChipsInput: {
          props: ["modelValue"],
          emits: ["update:modelValue"],
          template: `
            <input
              aria-label="Verfasser/in"
              :value="(modelValue || []).join(',')"
              @input="$emit('update:modelValue', $event.target.value.split(',').filter(Boolean))"
            />
          `,
        },
      },
    },
  });
}

describe("AktivzitierungSliInput", () => {
  it("emits updated AktivzitierungSli when title changes", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      titel: "Old title",
    };

    const { emitted } = renderComponent(initialValue);

    const input = screen.getByRole("textbox", {
      name: "Hauptsachtitel / Dokumentarischer Titel",
    });

    await user.clear(input);
    await user.type(input, "New title");

    const events = emitted()["update:modelValue"] as Array<[AktivzitierungSli]>;
    const finalPayload = events[events.length - 1]![0];

    expect(finalPayload).toEqual({
      ...initialValue,
      titel: "New title",
    });
  });

  it("emits updated AktivzitierungSli when year changes", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      veroeffentlichungsJahr: "2020",
    };

    const { emitted } = renderComponent(initialValue);

    const input = screen.getByRole("textbox", { name: "Veröffentlichungsjahr" });
    await user.clear(input);
    await user.type(input, "2024");

    const events = emitted()["update:modelValue"] as Array<[AktivzitierungSli]>;
    const finalPayload = events[events.length - 1]![0];

    expect(finalPayload).toEqual({
      ...initialValue,
      veroeffentlichungsJahr: "2024",
    });
  });

  it("emits updated AktivzitierungSli when dokumenttypen changes", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      dokumenttypen: [],
    };

    const { emitted } = renderComponent(initialValue);

    const input = screen.getByRole("textbox", { name: "Dokumenttyp" });
    await user.clear(input);
    await user.type(input, "Bib");

    const events = emitted()["update:modelValue"] as Array<[AktivzitierungSli]>;
    const finalPayload = events[events.length - 1]![0];

    expect(finalPayload.dokumenttypen).toEqual([{ abbreviation: "Bib", name: "Bib" }]);
  });

  it("emits updated AktivzitierungSli when verfasser changes", async () => {
    const user = userEvent.setup();
    const initialValue: AktivzitierungSli = {
      id: "123",
      verfasser: [],
    };

    const { emitted } = renderComponent(initialValue);

    const input = screen.getByRole("textbox", { name: "Verfasser/in" });
    await user.clear(input);
    await user.type(input, "Müller");

    const events = emitted()["update:modelValue"] as Array<[AktivzitierungSli]>;
    const finalPayload = events[events.length - 1]![0];

    expect(finalPayload.verfasser).toEqual(["Müller"]);
  });
});
