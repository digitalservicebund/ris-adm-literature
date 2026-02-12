import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  useGetUliDocUnit,
  usePostUliDocUnit,
  usePutPublishUliDocUnit,
  usePutUliDocUnit,
  useGetUliPaginatedDocUnits,
  mapUliSearchResultToAktivzitierung,
} from "@/services/uli/uliDocumentUnitService";
import { ref } from "vue";
import {
  docTypeAnordnungFixture,
  docTypeBekanntmachungFixture,
} from "@/testing/fixtures/documentType.fixture";

describe("uliDocumentUnitService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.resetModules();
  });

  it("fetches a ULI doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      note: "",
    };

    const docUnitResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      json: docUnit,
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(docUnitResp), { status: 200 }),
    );

    const { data, error, isFetching, execute } = useGetUliDocUnit("KSLU054920707");
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value).toEqual(docUnit);
  });

  it("data is null when ULI fetch returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = useGetUliDocUnit("KSLU054920707");
    await execute();

    expect(data.value).toEqual(null);
  });

  it("returns an error on failed ULI fetch ", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = useGetUliDocUnit("KSLU054920708");
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("updates a ULI doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    };

    const updatedResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      json: {
        id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
        documentNumber: "KSLU054920707",
      },
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(updatedResp), { status: 200 }),
    );

    const { data, error, isFetching, execute } = usePutUliDocUnit(docUnit);
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value?.id).toBe(docUnit.id);
  });

  it("returns an error on failed ULI update", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePutUliDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    });
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("data is null when ULI update call returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = usePutUliDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    });
    await execute();

    expect(data.value).toEqual(null);
  });

  it("publishes a uli doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    };

    const publishedResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      json: {
        id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
        documentNumber: "KSLU054920707",
      },
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(publishedResp), { status: 200 }),
    );

    const { data, error, isFetching, execute } = usePutPublishUliDocUnit(docUnit);
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value?.id).toBe(docUnit.id);
  });

  it("returns an error on failed uli publication", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePutPublishUliDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    });
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("data is null when publish a uli doc returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = usePutPublishUliDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSLU054920707",
      note: "",
    });
    await execute();

    expect(data.value).toEqual(null);
  });

  it("returns an error on failed ULI creation", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePostUliDocUnit();
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("gets an unfiltered paginated list of uli doc units", async () => {
    const fetchSpy = vi
      .spyOn(window, "fetch")
      .mockResolvedValue(new Response(JSON.stringify({}), { status: 200 }));

    const { error, isFetching, execute } = useGetUliPaginatedDocUnits(ref(5), 100, ref(undefined));
    execute();
    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(isFetching.value).toBe(false);
    expect(fetchSpy).toHaveBeenCalledWith(
      "/api/literature/uli/documentation-units?pageNumber=5&pageSize=100&sortByProperty=documentNumber&sortDirection=DESC",
      expect.anything(),
    );
    expect(error.value).toBeFalsy();
  });

  it("gets an filtered paginated list of uli doc units", async () => {
    const fetchSpy = vi
      .spyOn(window, "fetch")
      .mockResolvedValue(new Response(JSON.stringify({}), { status: 200 }));

    const { error, isFetching, execute } = useGetUliPaginatedDocUnits(
      ref(5),
      100,
      ref({
        periodikum: "Banz",
        zitatstelle: "S. 10",
        verfasser: ["Müller", "Zimmermann"],
        dokumenttypen: [docTypeAnordnungFixture, docTypeBekanntmachungFixture],
      }),
    );
    execute();
    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(isFetching.value).toBe(false);
    expect(fetchSpy).toHaveBeenCalledWith(
      "/api/literature/uli/documentation-units?pageNumber=5&pageSize=100&periodikum=Banz&zitatstelle=S.+10&dokumenttypen=Anordnung%2CBekanntmachung&verfasser=M%C3%BCller%2CZimmermann&sortByProperty=documentNumber&sortDirection=DESC",
      expect.anything(),
    );
    expect(error.value).toBeFalsy();
  });
});

describe("mapUliSearchResultToAktivzitierung", () => {
  vi.stubGlobal("crypto", {
    randomUUID: () => "mocked-uuid",
  });

  it("maps a ULI result with fundstellen correctly", () => {
    const input = {
      id: "uli-id",
      documentNumber: "ULI-1",
      titel: "Legal Research Paper",
      verfasser: ["Müller", "Schmidt"],
      dokumenttypen: ["Aufsatz"],
      fundstellen: ["NJW 2024, 123"],
    };

    const result = mapUliSearchResultToAktivzitierung(input);

    expect(result).toEqual({
      id: "mocked-uuid",
      titel: "Legal Research Paper",
      documentNumber: "ULI-1",
      verfasser: ["Müller", "Schmidt"],
      dokumenttypen: [{ abbreviation: "Aufsatz", name: "Aufsatz" }],
      periodikum: "NJW 2024",
      zitatstelle: "123",
    });
  });

  it("handles empty fundstellen by setting periodikum and zitatstelle to undefined", () => {
    const input = {
      id: "uli-id",
      documentNumber: "ULI-2",
      fundstellen: [],
    };

    const result = mapUliSearchResultToAktivzitierung(input);

    expect(result.periodikum).toBeUndefined();
    expect(result.zitatstelle).toBeUndefined();
  });

  it("handles malformed fundstellen without commas gracefully", () => {
    const input = {
      id: "uli-id",
      documentNumber: "ULI-3",
      fundstellen: ["JustAStatementNoComma"],
    };

    const result = mapUliSearchResultToAktivzitierung(input);

    expect(result.periodikum).toBe("JustAStatementNoComma");
    expect(result.zitatstelle).toBeUndefined();
  });

  it("maps empty arrays for verfasser and dokumenttypen", () => {
    const input = {
      id: "uli-id",
      documentNumber: "ULI-4",
      verfasser: undefined,
      dokumenttypen: undefined,
    };

    const result = mapUliSearchResultToAktivzitierung(input);

    expect(result.verfasser).toEqual([]);
    expect(result.dokumenttypen).toEqual([]);
  });

  it("takes only the first element from fundstellen array", () => {
    const input = {
      id: "uli-id",
      documentNumber: "ULI-5",
      fundstellen: ["First, 1", "Second, 2"],
    };

    const result = mapUliSearchResultToAktivzitierung(input);

    expect(result.periodikum).toBe("First");
    expect(result.zitatstelle).toBe("1");
  });
});
