import { render, screen, within } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import { sliDocUnitListItemFixture } from "@/testing/fixtures/sliDocumentUnit.fixture";
import SearchResults from "./SearchResults.vue";

describe("Aktivzitierung search results", () => {
  it("renders 1 row", () => {
    render(SearchResults, {
      props: {
        searchResults: [sliDocUnitListItemFixture],
        isLoading: false,
      },
    });

    const resultsList = screen.getByRole("list");
    expect(resultsList).toBeInTheDocument();

    const listItems = within(resultsList).getAllByRole("listitem");
    expect(listItems).toHaveLength(1);
  });

  it("renders an empty message", () => {
    render(SearchResults, {
      props: {
        searchResults: [],
        isLoading: false,
      },
    });

    expect(screen.getByText("Keine Suchergebnisse gefunden")).toBeInTheDocument();
  });

  it("renders a loading spinner", () => {
    render(SearchResults, {
      props: {
        searchResults: [],
        isLoading: true,
      },
    });

    const spinnerElement = screen.getByRole("progressbar");
    expect(spinnerElement).toBeInTheDocument();
  });
});
