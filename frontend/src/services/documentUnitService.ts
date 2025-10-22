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
import { computed, type MaybeRefOrGetter, type Ref } from 'vue'
import type { UliDocumentationUnit, UliDocumentUnitResponse } from '@/domain/uli/uliDocumentUnit'
import { DocumentTypeCode } from '@/domain/documentType'

const DOCUMENTATION_UNITS_URL = '/documentation-units'

const transformers = {
  [DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN]: mapResponseToAdmDocUnit,
  [DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG]: mapResponseToUliDocUnit,
  [DocumentTypeCode.LITERATUR_SELBSTSTAENDIG]: () => {},
} as const

function useDocUnitFetch(url: MaybeRefOrGetter<string>, docTypeCode: DocumentTypeCode) {
  return useApiFetch(
    url,
    { headers: { 'X-Document-Type': docTypeCode as string } },
    {
      afterFetch: ({ data }) => {
        if (!data) return { data }

        return {
          data: transformers[docTypeCode](data),
        }
      },
      immediate: false,
    },
  )
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
    fetch,
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

export function useGetDocUnit<DocumentationUnit>(
  documentNumber: string,
  documentType: DocumentTypeCode,
): UseFetchReturn<DocumentationUnit> {
  return useDocUnitFetch(`${DOCUMENTATION_UNITS_URL}/${documentNumber}`, documentType).json()
}

export function usePutDocUnit<DocumentationUnit>(
  documentUnit: DocumentationUnit & { documentNumber: string },
  documentType: DocumentTypeCode,
): UseFetchReturn<DocumentationUnit> {
  return useDocUnitFetch(`${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, documentType)
    .json()
    .put(documentUnit)
}

function mapResponseToUliDocUnit(data: UliDocumentUnitResponse): UliDocumentationUnit {
  const documentUnit: UliDocumentationUnit = {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
  }
  documentUnit.note = documentUnit.note || ''
  return documentUnit
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
