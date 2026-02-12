import { useApiFetch } from "../apiService";
import { type UseFetchReturn } from "@vueuse/core";
import type {
  UliDocumentationUnit,
  UliDocumentUnitResponse,
  UliDocUnitListItem,
  UliDocUnitSearchParams,
} from "@/domain/uli/uliDocumentUnit";
import type { SliDocumentUnitResponse } from "@/domain/sli/sliDocumentUnit";
import { computed, type Ref } from "vue";
import { buildUrlWithParams } from "@/utils/urlHelpers";
import { splitTrimFirstComma } from "@/utils/stringsUtil";
import type { AktivzitierungUli } from "@/domain/AktivzitierungUli";

const LITERATURE_DOCUMENTATION_UNITS_URL = "/literature/documentation-units";
const ULI_LITERATURE_DOCUMENTATION_UNITS_URL = "/literature/uli/documentation-units";

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

export function usePostUliDocUnit(): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(ULI_LITERATURE_DOCUMENTATION_UNITS_URL).json().post();
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

export function mapUliSearchResultToAktivzitierung(result: UliDocUnitListItem): AktivzitierungUli {
  const firstFundstelle = result.fundstellen?.[0] || "";
  const [periodikum, zitatstelle] = splitTrimFirstComma(firstFundstelle);

  return {
    id: crypto.randomUUID(),
    titel: result.titel,
    documentNumber: result.documentNumber,
    verfasser: result.verfasser ?? [],
    dokumenttypen:
      result.dokumenttypen?.map((abbr) => ({
        abbreviation: abbr,
        name: abbr,
      })) ?? [],
    periodikum: periodikum || undefined,
    zitatstelle: zitatstelle || undefined,
  };
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
