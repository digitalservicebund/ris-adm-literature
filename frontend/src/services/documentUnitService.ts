import type { DocumentUnit, DocumentUnitSearchParams } from '@/domain/documentUnit'
import ActiveCitation from '@/domain/activeCitation'
import type { DocumentUnitResponse } from '@/domain/documentUnitResponse.ts'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'
import { useApiFetch } from './apiService'
import { type UseFetchReturn } from '@vueuse/core'
import { buildUrlWithParams } from '@/utils/urlHelpers'
import { computed, type Ref } from 'vue'

const DOCUMENTATION_UNITS_URL = '/documentation-units'

export function useGetDocUnit(documentNumber: string): UseFetchReturn<DocumentUnit> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      if (!data) return { data }

      return {
        data: mapResponseDataToDocumentUnit(data),
      }
    },
    immediate: false,
  }).json()
}

export function usePutDocUnit(documentUnit: DocumentUnit): UseFetchReturn<DocumentUnit> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      if (!data) return { data }

      return {
        data: mapResponseDataToDocumentUnit(data),
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

export function usePostDocUnit(): UseFetchReturn<DocumentUnit> {
  return useApiFetch(`${DOCUMENTATION_UNITS_URL}`).json().post()
}

export function useGetPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<DocumentUnitSearchParams | undefined>,
): UseFetchReturn<DocumentUnit> {
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

function mapResponseDataToDocumentUnit(data: DocumentUnitResponse): DocumentUnit {
  const documentUnit: DocumentUnit = {
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
