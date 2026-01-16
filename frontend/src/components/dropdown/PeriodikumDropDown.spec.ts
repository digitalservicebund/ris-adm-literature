import { describe, it, expect, vi, afterEach } from "vitest";
import { amtsblattFixture, bundesanzeigerFixture } from "@/testing/fixtures/periodikum.fixture";
import PeriodikumDropDown from "./PeriodikumDropDown.vue";
import type { Periodikum } from "@/domain/fundstelle";
import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";

describe("PeriodikumDropDown", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders correctly", async () => {
    const fetchSpy = vi
      .spyOn(window, "fetch")
      .mockResolvedValue(
        new Response(
          JSON.stringify({ legalPeriodicals: [bundesanzeigerFixture, amtsblattFixture] }),
          { status: 200 },
        ),
      );

    render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));
    expect(screen.getByRole("combobox", { name: "Periodikum" })).toBeVisible();
  });

  it("renders correctly on fetching error", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockRejectedValueOnce("fetch error");

    render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(screen.getByRole("combobox", { name: "Periodikum" })).toBeVisible();
  });

  it("emits updated model value when selection changes", async () => {
    const user = userEvent.setup();
    const fetchSpy = vi
      .spyOn(window, "fetch")
      .mockResolvedValue(
        new Response(
          JSON.stringify({ legalPeriodicals: [bundesanzeigerFixture, amtsblattFixture] }),
          { status: 200 },
        ),
      );

    const { emitted } = render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    // when
    const input = screen.getByRole("combobox", { name: "Periodikum" });
    await user.type(input, "bun");

    const option = await screen.getByRole("option", { name: "BAnz | Bundesanzeiger" });

    // then
    await vi.waitFor(() => {
      expect(option).toBeVisible();
    });

    // when
    await user.click(option);

    // then
    const events = emitted()["update:modelValue"] as Array<[Periodikum]>;
    const finalPayload = events[events.length - 1]![0];
    expect(finalPayload).toEqual(bundesanzeigerFixture);

    // when
    const removeButton = screen.getByRole("button", { name: "Entfernen" });
    await user.click(removeButton);

    // then
    expect(events[events.length - 1]![0]).toEqual(undefined);
  });

  it("returns a combined string when both abbreviation and title are present", () => {
    render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: {
          id: "1",
          abbreviation: "BGBI",
          title: "Bundesgesetzblatt",
        },
      },
    });

    const input = screen.getByRole("combobox", { name: "Periodikum" });
    expect(input).toHaveValue("BGBI | Bundesgesetzblatt");
  });

  it("returns only the abbreviation when title is missing", () => {
    render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: {
          id: "2",
          abbreviation: "JK",
          // title is missing
        } as Periodikum,
      },
    });

    const input = screen.getByRole("combobox", { name: "Periodikum" });
    expect(input).toHaveValue("JK");
  });

  it("returns an empty string when modelValue is undefined", () => {
    render(PeriodikumDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    const input = screen.getByRole("combobox", { name: "Periodikum" });
    expect(input).toHaveValue("");
  });
});
