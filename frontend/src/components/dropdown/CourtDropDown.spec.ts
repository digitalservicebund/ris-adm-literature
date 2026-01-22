import { describe, it, expect, vi, afterEach } from "vitest";
import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import CourtDropDown from "./CourtDropDown.vue";
import { agAachenFixture, berufsgerichtBremenFixture } from "@/testing/fixtures/court.fixture";
import type { Court } from "@/domain/court";

describe("CourtDropdown", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders correctly", async () => {
    const fetchSpy = vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify({ courts: [agAachenFixture, berufsgerichtBremenFixture] }), {
        status: 200,
      }),
    );

    render(CourtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));
    expect(screen.getByRole("combobox", { name: "Gericht" })).toBeVisible();
  });

  it("renders correctly on fetching error", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockRejectedValueOnce("fetch error");

    render(CourtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(screen.getByRole("combobox", { name: "Gericht" })).toBeVisible();
  });

  it("emits updated model value when selection changes", async () => {
    const user = userEvent.setup();
    const fetchSpy = vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify({ courts: [agAachenFixture, berufsgerichtBremenFixture] }), {
        status: 200,
      }),
    );

    const { emitted } = render(CourtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    // when
    const input = screen.getByRole("combobox", { name: "Gericht" });
    await user.type(input, "ag");

    const option = await screen.getByRole("option", { name: "AG Aachen" });

    // then
    await vi.waitFor(() => {
      expect(option).toBeVisible();
    });

    // when
    await user.click(option);

    // then
    const events = emitted()["update:modelValue"] as Array<[Court]>;
    const finalPayload = events[events.length - 1]![0];
    expect(finalPayload).toEqual(agAachenFixture);

    // when
    const removeButton = screen.getByRole("button", { name: "Entfernen" });
    await user.click(removeButton);

    // then
    expect(events[events.length - 1]![0]).toEqual(undefined);
  });

  it("shows type and location as initial label", () => {
    render(CourtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: {
          id: "2",
          type: "AG",
          location: "Aachen",
        },
      },
    });

    const input = screen.getByRole("combobox", { name: "Gericht" });
    expect(input).toHaveValue("AG Aachen");
  });
});
