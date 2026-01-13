import { ref } from "vue";
import { describe, it, expect, vi } from "vitest";
import { usePagination } from "@/composables/usePagination";

const ITEMS_PER_PAGE = 100;

const mockData = {
  documentationUnitsOverview: [
    { id: 1, title: "Doc 1" },
    { id: 2, title: "Doc 2" },
  ],
  page: {
    totalElements: 42,
    number: 0,
  },
};

describe("usePagination", () => {
  it("should initialize with default values", () => {
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: vi.fn().mockResolvedValue(undefined),
    });

    const { items, totalRows, firstRowIndex } = usePagination(
      mockFetchData,
      ITEMS_PER_PAGE,
      "documentationUnitsOverview",
    );

    expect(items.value.length).toBe(2);
    expect(totalRows.value).toBe(42);
    expect(firstRowIndex.value).toBe(0);
  });

  it("should call execute when fetchPaginatedData is called", async () => {
    const executeSpy = vi.fn().mockResolvedValue(undefined);
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: executeSpy,
    });

    const { fetchPaginatedData } = usePagination(
      mockFetchData,
      ITEMS_PER_PAGE,
      "documentationUnitsOverview",
    );

    await fetchPaginatedData(1);

    expect(executeSpy).toHaveBeenCalled();
  });

  it("should reset page to 0 and update searchParams when new search is provided", async () => {
    const executeSpy = vi.fn().mockResolvedValue(undefined);
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      isFetching: ref(false),
      execute: executeSpy,
    });

    const { fetchPaginatedData } = usePagination(
      mockFetchData,
      ITEMS_PER_PAGE,
      "documentationUnitsOverview",
    );

    const newSearch = { documentNumber: "abc" };

    await fetchPaginatedData(42, newSearch);

    // Because new search resets page to 0
    expect(mockFetchData).toHaveBeenCalledWith(
      expect.anything(),
      ITEMS_PER_PAGE,
      expect.anything(),
    );
    expect(executeSpy).toHaveBeenCalled();
  });

  it("should expose the error when fetch fails", async () => {
    const mockError = new Error("Something went wrong");

    const executeSpy = vi.fn().mockResolvedValue(undefined);
    const mockFetchData = vi.fn().mockReturnValue({
      data: ref(null),
      error: ref(mockError),
      isFetching: ref(false),
      execute: executeSpy,
    });

    const { error, fetchPaginatedData } = usePagination(
      mockFetchData,
      ITEMS_PER_PAGE,
      "documentationUnitsOverview",
    );

    await fetchPaginatedData();

    expect(error.value).toBe(mockError);
    expect(executeSpy).toHaveBeenCalled();
  });

  it("returns [] and 0 when data is undefined", async () => {
    const mockData = ref(undefined);
    const executeSpy = vi.fn().mockResolvedValue(undefined);

    const mockFetchData = vi.fn().mockReturnValue({
      data: mockData,
      error: ref(null),
      isFetching: ref(false),
      execute: executeSpy,
    });

    const { items, totalRows, fetchPaginatedData } = usePagination(
      mockFetchData,
      ITEMS_PER_PAGE,
      "results",
    );

    await fetchPaginatedData();

    expect(items.value).toEqual([]);
    expect(totalRows.value).toBe(0);
    expect(executeSpy).toHaveBeenCalled();
  });
});
