import type {
  AdmDocumentationUnit,
  AdmDocumentUnitResponse,
  AdmDocUnitSearchParams,
} from '@/domain/adm/admDocumentUnit'
import ActiveCitation from '@/domain/activeCitation'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'
import { useApiFetch } from './apiService'
import { type UseFetchReturn } from '@vueuse/core'
import { buildUrlWithParams } from '@/utils/urlHelpers'
import { computed, type Ref } from 'vue'
import type { UliDocumentationUnit, UliDocumentUnitResponse } from '@/domain/uli/uliDocumentUnit'

const DOCUMENTATION_UNITS_URL = '/documentation-units'

export function useGetAdmDocUnit(documentNumber: string): UseFetchReturn<AdmDocumentationUnit> {
  return getDocUnit<AdmDocumentUnitResponse, AdmDocumentationUnit>(
    documentNumber,
    mapResponseToAdmDocUnit,
  )
}

export function usePutAdmDocUnit(doc: AdmDocumentationUnit): UseFetchReturn<AdmDocumentationUnit> {
  return putDocUnit<AdmDocumentUnitResponse, AdmDocumentationUnit>(doc, mapResponseToAdmDocUnit)
}

export function useGetUliDocUnit(documentNumber: string): UseFetchReturn<UliDocumentationUnit> {
  return getDocUnit<UliDocumentUnitResponse, UliDocumentationUnit>(
    documentNumber,
    mapResponseToUliDocUnit,
  )
}

export function usePutUliDocUnit(doc: UliDocumentationUnit): UseFetchReturn<UliDocumentationUnit> {
  return putDocUnit<UliDocumentUnitResponse, UliDocumentationUnit>(doc, mapResponseToUliDocUnit)
}

export function usePutPublishAdmDocUnit(
  documentUnit: AdmDocumentationUnit,
): UseFetchReturn<AdmDocumentationUnit> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}/publish`, {
    afterFetch: async ({ data }) => {
      if (!data) return { data }

      return {
        data: mapResponseToAdmDocUnit(data),
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

export function usePostDocUnit<
  T extends { id: string; documentNumber: string },
>(): UseFetchReturn<T> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}`).json().post()
}

export function useGetPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<AdmDocUnitSearchParams | undefined>,
): UseFetchReturn<AdmDocumentationUnit> {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${DOCUMENTATION_UNITS_URL}`, {
      pageNumber: pageNumber.value.toString(),
      pageSize: pageSize.toString(),
      documentNumber: search?.value?.documentNumber?.toString(),
      fundstellen: search?.value?.fundstellen?.toString(),
      langueberschrift: search?.value?.langueberschrift?.toString(),
      zitierdaten: search?.value?.zitierdaten?.toString(),
      sortByProperty: 'documentNumber',
      sortDirection: 'DESC',
    }),
  )

  return useApiFetch(urlWithParams).json()
}

function getDocUnit<TResponse, TModel>(
  documentNumber: string,
  mapFn: (data: TResponse) => TModel,
): UseFetchReturn<TModel> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      if (!data) return { data }
      return {
        data: mapFn(data as TResponse),
      }
    },
    immediate: false,
  }).json()
}

function putDocUnit<TResponse, TModel>(
  documentUnit: TModel & { documentNumber: string },
  mapFn: (data: TResponse) => TModel,
): UseFetchReturn<TModel> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      if (!data) return { data }
      return {
        data: mapFn(data as TResponse),
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

function mapResponseToUliDocUnit(data: UliDocumentUnitResponse): UliDocumentationUnit {
  return {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
  }
}

function mapResponseToAdmDocUnit(data: AdmDocumentUnitResponse): AdmDocumentationUnit {
  const documentUnit: AdmDocumentationUnit = {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
  }

  documentUnit.fieldsOfLaw = documentUnit.fieldsOfLaw || []
  documentUnit.activeCitations = documentUnit.activeCitations?.map(
    (activeCitation) => new ActiveCitation({ ...activeCitation }),
  )
  documentUnit.activeReferences = documentUnit.activeReferences?.map(
    (activeReference) =>
      new ActiveReference({
        ...activeReference,
        singleNorms: activeReference.singleNorms?.map(
          (norm) =>
            new SingleNorm({
              ...norm,
            }),
        ),
      }),
  )
  documentUnit.normReferences = documentUnit.normReferences?.map(
    (normReference) =>
      new NormReference({
        ...normReference,
        singleNorms: normReference.singleNorms?.map(
          (norm) =>
            new SingleNorm({
              ...norm,
            }),
        ),
      }),
  )
  documentUnit.note = documentUnit.note || ''
  return documentUnit
}
