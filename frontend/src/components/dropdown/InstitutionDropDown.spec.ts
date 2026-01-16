import { afterEach, describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import InstitutionDropDown from "./InstitutionDropDown.vue";
import { institutionFixture, legalEntityFixture } from "@/testing/fixtures/institution.fixture";
import type { Institution } from "@/domain/normgeber";

describe("InstitutionDropdown", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders correctly", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockResolvedValueOnce(
      new Response(JSON.stringify({ zitierArten: [legalEntityFixture, institutionFixture] }), {
        status: 200,
      }),
    );

    render(InstitutionDropDown, {
      props: {
        inputId: "foo",
        isInvalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));
    expect(screen.getByRole("combobox", { name: "Normgeber" })).toBeVisible();
  });

  it("renders correctly on fetching error", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockRejectedValueOnce("fetch error");

    render(InstitutionDropDown, {
      props: {
        inputId: "foo",
        isInvalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(screen.getByRole("combobox", { name: "Normgeber" })).toBeVisible();
  });

  it("emits updated model value when selection changes", async () => {
    const user = userEvent.setup();
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockResolvedValueOnce(
      new Response(JSON.stringify({ institutions: [legalEntityFixture, institutionFixture] }), {
        status: 200,
      }),
    );

    const { emitted } = render(InstitutionDropDown, {
      props: {
        inputId: "foo",
        isInvalid: false,
        modelValue: undefined,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    // when
    const input = screen.getByRole("combobox", { name: "Normgeber" });
    await user.type(input, "legalEntity");

    const option = await screen.getByRole("option", { name: "legalEntity" });

    // then
    await vi.waitFor(() => {
      expect(option).toBeVisible();
    });

    // when
    await user.click(option);

    // then
    const events = emitted()["update:modelValue"] as Array<[Institution]>;
    const finalPayload = events[events.length - 1]![0];
    expect(finalPayload).toEqual(legalEntityFixture);

    // when
    const removeButton = screen.getByRole("button", { name: "Entfernen" });
    await user.click(removeButton);

    // then
    expect(events[events.length - 1]![0]).toEqual(undefined);
  });
});
