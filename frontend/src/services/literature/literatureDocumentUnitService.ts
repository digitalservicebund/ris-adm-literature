import { useApiFetch } from "../apiService";
import { type UseFetchReturn } from "@vueuse/core";
import type {
  UliDocumentationUnit,
  UliDocumentUnitResponse,
  UliDocUnitSearchParams,
} from "@/domain/uli/uliDocumentUnit";
import type {
  SliDocumentationUnit,
  SliDocumentUnitResponse,
  SliDocUnitListItem,
  SliDocUnitSearchParams,
} from "@/domain/sli/sliDocumentUnit";
import { computed, type Ref } from "vue";
import { buildUrlWithParams } from "@/utils/urlHelpers";
import type {
  AdmAktivzitierungListItem,
  AdmDocUnitSearchParams,
} from "@/domain/adm/admDocumentUnit";
import { splitTrimFirstComma } from "@/utils/stringsUtil";
import type { AktivzitierungAdm } from "@/domain/AktivzitierungAdm";
import type { AktivzitierungSli } from "@/domain/AktivzitierungSli";

const LITERATURE_DOCUMENTATION_UNITS_URL = "/literature/documentation-units";
const ULI_LITERATURE_DOCUMENTATION_UNITS_URL = "/literature/uli/documentation-units";
const SLI_LITERATURE_DOCUMENTATION_UNITS_URL = "/literature/sli/documentation-units";

export function usePutPublishUliDocUnit(
  documentUnit: UliDocumentationUnit,
): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(
    `${ULI_LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}/publish`,
    {
      afterFetch: ({ data }) => {
        return {
          data: data ? mapResponseToUliDocUnit(data) : null,
        };
      },
      immediate: false,
    },
  )
    .json()
    .put(documentUnit);
}

export function usePutPublishSliDocUnit(
  documentUnit: SliDocumentationUnit,
): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(
    `${SLI_LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}/publish`,
    {
      afterFetch: ({ data }) => {
        return {
          data: data ? mapResponseToSliDocUnit(data) : null,
        };
      },
      immediate: false,
    },
  )
    .json()
    .put(documentUnit);
}

export function usePostUliDocUnit(): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(ULI_LITERATURE_DOCUMENTATION_UNITS_URL).json().post();
}

export function usePostSliDocUnit(): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(SLI_LITERATURE_DOCUMENTATION_UNITS_URL).json().post();
}

export function useGetUliDocUnit(documentNumber: string): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToUliDocUnit(data) : null,
      };
    },
    immediate: false,
  }).json();
}

export function useGetSliDocUnit(documentNumber: string): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToSliDocUnit(data) : null,
      };
    },
    immediate: false,
  }).json();
}

export function usePutUliDocUnit(
  documentUnit: UliDocumentationUnit,
): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToUliDocUnit(data) : null,
      };
    },
    immediate: false,
  })
    .json()
    .put(documentUnit);
}

export function usePutSliDocUnit(
  documentUnit: SliDocumentationUnit,
): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToSliDocUnit(data) : null,
      };
    },
    immediate: false,
  })
    .json()
    .put(documentUnit);
}

export function useGetSliPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<SliDocUnitSearchParams | undefined>,
) {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${SLI_LITERATURE_DOCUMENTATION_UNITS_URL}`, {
      pageNumber: pageNumber.value.toString(),
      pageSize: pageSize.toString(),
      titel: search?.value?.titel?.toString(),
      veroeffentlichungsjahr: search?.value?.veroeffentlichungsJahr?.toString(),
      dokumenttypen: search?.value?.dokumenttypen?.map((d) => d.abbreviation).join(),
      verfasser: search?.value?.verfasser?.join(),
      sortByProperty: "documentNumber",
      sortDirection: "DESC",
    }),
  );

  return useApiFetch(urlWithParams, { immediate: false }).json();
}

export function useGetUliPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<UliDocUnitSearchParams | undefined>,
) {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${ULI_LITERATURE_DOCUMENTATION_UNITS_URL}`, {
      pageNumber: pageNumber.value.toString(),
      pageSize: pageSize.toString(),
      periodikum: search?.value?.periodikum?.toString(),
      zitatstelle: search?.value?.zitatstelle?.toString(),
      dokumenttypen: search?.value?.dokumenttypen?.map((d) => d.abbreviation).join(),
      verfasser: search?.value?.verfasser?.join(),
      sortByProperty: "documentNumber",
      sortDirection: "DESC",
    }),
  );

  return useApiFetch(urlWithParams, { immediate: false }).json();
}

export function useGetAdmPaginatedDocUnitsForSli(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<AdmDocUnitSearchParams | undefined>,
) {
  const urlWithParams = computed(() =>
    buildUrlWithParams("/literature/aktivzitierungen/adm", {
      pageNumber: pageNumber.value.toString(),
      pageSize: pageSize.toString(),
      documentNumber: search?.value?.documentNumber?.toString(),
      periodikum: search?.value?.periodikum?.toString(),
      zitatstelle: search?.value?.zitatstelle?.toString(),
      inkrafttretedatum: search?.value?.inkrafttretedatum?.toString(),
      aktenzeichen: search?.value?.aktenzeichen,
      dokumenttyp: search?.value?.dokumenttyp?.toString(),
      normgeber: search?.value?.normgeber,
      sortByProperty: "documentNumber",
      sortDirection: "DESC",
    }),
  );

  return useApiFetch(urlWithParams, {
    immediate: false,
    afterFetch: ({ data }) => ({
      data: data
        ? {
            documentationUnitsOverview: data.documentationUnits,
            page: data.page,
          }
        : null,
    }),
  }).json();
}

export function mapAdmSearchResultToAktivzitierung(
  result: AdmAktivzitierungListItem,
): AktivzitierungAdm {
  const [periodikum, zitatstelle] = result.fundstellen?.[0]
    ? splitTrimFirstComma(result.fundstellen[0])
    : [];

  return {
    id: crypto.randomUUID(),
    documentNumber: result.documentNumber,
    inkrafttretedatum: result.inkrafttretedatum,
    dokumenttyp: result.dokumenttyp,
    normgeber: result.normgeberList?.[0],
    aktenzeichen: result.aktenzeichenList?.[0],
    periodikum,
    zitatstelle,
  };
}

export function mapSliSearchResultToAktivzitierung(result: SliDocUnitListItem): AktivzitierungSli {
  const dokumenttypen = result.dokumenttypen?.map((abbr) => ({
    abbreviation: abbr,
    name: abbr,
  }));

  return {
    id: crypto.randomUUID(),
    titel: result.titel,
    documentNumber: result.documentNumber,
    veroeffentlichungsJahr: result.veroeffentlichungsjahr,
    verfasser: result.verfasser || [],
    dokumenttypen: dokumenttypen || [],
  };
}

function mapResponseToSliDocUnit(data: SliDocumentUnitResponse): SliDocumentationUnit {
  return mapResponseToLiteratureDocUnit(data);
}

function mapResponseToUliDocUnit(data: UliDocumentUnitResponse): UliDocumentationUnit {
  return mapResponseToLiteratureDocUnit(data);
}

function mapResponseToLiteratureDocUnit(data: UliDocumentUnitResponse | SliDocumentUnitResponse) {
  return {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
    note: data.administrativeData.note || "",
  };
}
