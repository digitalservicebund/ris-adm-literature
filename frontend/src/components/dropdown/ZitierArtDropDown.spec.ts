import { afterEach, describe, expect, it, vi } from "vitest";
import {
  zitierArtAbgrenzungFixture,
  zitierArtUebernahmeFixture,
} from "@/testing/fixtures/zitierArt.fixture.ts";
import ZitierArtDropDown from "./ZitierArtDropDown.vue";
import { DocumentCategory } from "@/domain/documentType";
import { render, screen } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import type { ZitierArt } from "@/domain/zitierArt";

describe("ZitierArtDropDown", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders correctly", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockResolvedValueOnce(
      new Response(
        JSON.stringify({ zitierArten: [zitierArtAbgrenzungFixture, zitierArtUebernahmeFixture] }),
        {
          status: 200,
        },
      ),
    );

    render(ZitierArtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
        sourceDocumentCategory: DocumentCategory.LITERATUR,
        targetDocumentCategory: null,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));
    expect(screen.getByRole("combobox", { name: "Art der Zitierung" })).toBeVisible();
  });

  it("renders correctly on fetching error", async () => {
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockRejectedValueOnce("fetch error");

    render(ZitierArtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
        sourceDocumentCategory: DocumentCategory.LITERATUR,
        targetDocumentCategory: null,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(screen.getByRole("combobox", { name: "Art der Zitierung" })).toBeVisible();
  });

  it("emits updated model value when selection changes", async () => {
    const user = userEvent.setup();
    const fetchSpy = vi.spyOn(window, "fetch");
    fetchSpy.mockResolvedValueOnce(
      new Response(
        JSON.stringify({ zitierArten: [zitierArtAbgrenzungFixture, zitierArtUebernahmeFixture] }),
        {
          status: 200,
        },
      ),
    );

    const { emitted } = render(ZitierArtDropDown, {
      props: {
        inputId: "foo",
        invalid: false,
        modelValue: undefined,
        sourceDocumentCategory: DocumentCategory.LITERATUR,
        targetDocumentCategory: null,
      },
    });

    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    // when
    const input = screen.getByRole("combobox", { name: "Art der Zitierung" });
    await user.type(input, "Abgrenzung");

    const option = await screen.getByRole("option", { name: "Abgrenzung" });

    // then
    await vi.waitFor(() => {
      expect(option).toBeVisible();
    });

    // when
    await user.click(option);

    // then
    const events = emitted()["update:modelValue"] as Array<[ZitierArt]>;
    const finalPayload = events[events.length - 1]![0];
    expect(finalPayload).toEqual(zitierArtAbgrenzungFixture);

    // when
    const removeButton = screen.getByRole("button", { name: "Entfernen" });
    await user.click(removeButton);

    // then
    expect(events[events.length - 1]![0]).toEqual(undefined);
  });
});
