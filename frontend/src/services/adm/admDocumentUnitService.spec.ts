import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  mapAdmSearchResultToAktivzitierung,
  useGetAdmDocUnit,
  useGetAdmPaginatedDocUnits,
  usePostAdmDocUnit,
  usePutAdmDocUnit,
  usePutPublishAdmDocUnit,
} from "@/services/adm/admDocumentUnitService";
import ActiveReference from "@/domain/activeReference.ts";
import SingleNorm from "@/domain/singleNorm.ts";
import NormReference from "@/domain/normReference";
import { ref } from "vue";
import { until } from "@vueuse/core";
import ActiveCitation from "@/domain/activeCitation";
import { activeCitationFixture } from "@/testing/fixtures/activeCitation.fixture";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";

describe("admDocumentUnitService", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    vi.resetModules();
  });

  it("fetches a adm doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      fieldsOfLaw: [],
      fundstellen: [],
      activeCitations: [new ActiveCitation(activeCitationFixture)],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 5" })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 7" })] })],
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

    const { data, error, isFetching, execute } = useGetAdmDocUnit("KSNR054920707");
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value).toEqual(docUnit);
  });

  it("data is null when adm fetch returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = useGetAdmDocUnit("KSNR054920707");
    await execute();

    expect(data.value).toEqual(null);
  });

  it("returns an error on failed adm fetch ", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = useGetAdmDocUnit("KSNR054920708");
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("updates a adm doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      fieldsOfLaw: [],
      fundstellen: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 5" })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 7" })] })],
      note: "",
    };

    const updatedResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      json: {
        id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
        documentNumber: "KSNR054920707",
      },
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(updatedResp), { status: 200 }),
    );

    const { data, error, isFetching, execute } = usePutAdmDocUnit(docUnit);
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value?.id).toBe(docUnit.id);
  });

  it("returns an error on failed adm update", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePutAdmDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      note: "",
    });
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("data is null when adm update call returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = usePutAdmDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      note: "",
    });
    await execute();

    expect(data.value).toEqual(null);
  });

  it("publishes a adm doc unit", async () => {
    const docUnit = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      fieldsOfLaw: [],
      fundstellen: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 5" })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: "§ 7" })] })],
      note: "",
    };

    const publishedResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      json: {
        id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
        documentNumber: "KSNR054920707",
      },
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(publishedResp), { status: 200 }),
    );

    const { data, error, isFetching, execute } = usePutPublishAdmDocUnit(docUnit);
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value?.id).toBe(docUnit.id);
  });

  it("returns an error on failed adm publication", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePutPublishAdmDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      note: "",
    });
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("data is null when publish an adm doc returns a null body", async () => {
    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(null), { status: 200 }),
    );

    const { data, execute } = usePutPublishAdmDocUnit({
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      note: "",
    });
    await execute();

    expect(data.value).toEqual(null);
  });

  it("creates a adm doc unit", async () => {
    const createResp = {
      id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
      documentNumber: "KSNR054920707",
      json: {
        id: "8de5e4a0-6b67-4d65-98db-efe877a260c4",
        documentNumber: "KSNR054920707",
      },
      administrativeData: { note: "" },
    };

    vi.spyOn(window, "fetch").mockResolvedValue(
      new Response(JSON.stringify(createResp), { status: 201 }),
    );

    const { data, error, isFetching, isFinished } = usePostAdmDocUnit();
    await until(isFinished).toBe(true);

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeFalsy();
    expect(data.value?.id).toBe(createResp.id);
  });

  it("returns an error on failed adm creation", async () => {
    vi.spyOn(window, "fetch").mockRejectedValue(new Error("fetch failed"));

    const { data, error, isFetching, execute } = usePostAdmDocUnit();
    await execute();

    expect(isFetching.value).toBe(false);
    expect(error.value).toBeTruthy();
    expect(data.value).toBeNull();
  });

  it("gets a paginated list of doc units", async () => {
    const fetchSpy = vi
      .spyOn(window, "fetch")
      .mockResolvedValue(new Response(JSON.stringify({}), { status: 200 }));

    const { error, isFetching } = useGetAdmPaginatedDocUnits(ref(5), 100, ref(undefined));
    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1));

    expect(isFetching.value).toBe(false);
    expect(fetchSpy).toHaveBeenCalledWith(
      "/api/adm/documentation-units?pageNumber=5&pageSize=100&sortByProperty=documentNumber&sortDirection=DESC",
      expect.anything(),
    );
    expect(error.value).toBeFalsy();
  });
});

describe("mapAdmSearchResultToAktivzitierung", () => {
  vi.stubGlobal("crypto", {
    randomUUID: () => "mocked-uuid",
  });

  it("maps a normal input with fundstellen correctly", () => {
    const input = {
      id: "be-id",
      documentNumber: "DOC-1",
      inkrafttretedatum: "2024-01-01",
      dokumenttyp: "VV",
      normgeberList: ["BMJ"],
      aktenzeichenList: ["AZ-123"],
      fundstellen: ["BAnz AT 01.01.2024, S. 1"],
    };

    const result: AktivzitierungAdm = mapAdmSearchResultToAktivzitierung(input);

    expect(result).toEqual({
      id: "mocked-uuid",
      documentNumber: "DOC-1",
      inkrafttretedatum: "2024-01-01",
      dokumenttyp: "VV",
      normgeber: "BMJ",
      aktenzeichen: "AZ-123",
      periodikum: "BAnz AT 01.01.2024",
      zitatstelle: "S. 1",
    });
  });

  it("handles missing fundstellen gracefully", () => {
    const input = {
      id: "be-id",
      documentNumber: "DOC-2",
      inkrafttretedatum: "2024-01-02",
      dokumenttyp: "VV",
      normgeberList: [],
      aktenzeichenList: [],
      fundstellen: undefined,
    };

    const result: AktivzitierungAdm = mapAdmSearchResultToAktivzitierung(input);

    expect(result).toEqual({
      id: "mocked-uuid",
      documentNumber: "DOC-2",
      inkrafttretedatum: "2024-01-02",
      dokumenttyp: "VV",
      normgeber: undefined,
      aktenzeichen: undefined,
      periodikum: undefined,
      zitatstelle: undefined,
    });
  });

  it("handles empty fundstellen array", () => {
    const input = {
      id: "be-id",
      documentNumber: "DOC-3",
      inkrafttretedatum: "2024-01-03",
      dokumenttyp: "VV",
      normgeberList: [],
      aktenzeichenList: [],
      fundstellen: [],
    };

    const result = mapAdmSearchResultToAktivzitierung(input);

    expect(result.periodikum).toBeUndefined();
    expect(result.zitatstelle).toBeUndefined();
  });
});
