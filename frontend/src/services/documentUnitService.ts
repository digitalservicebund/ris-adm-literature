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
import type { SliDocumentationUnit, SliDocumentUnitResponse } from '@/domain/sli/sliDocumentUnit'

const ADM_DOCUMENTATION_UNITS_URL = '/adm/documentation-units'
const LITERATURE_DOCUMENTATION_UNITS_URL = '/literature/documentation-units'
const ULI_LITERATURE_DOCUMENTATION_UNITS_URL = '/literature/uli/documentation-units'
const SLI_LITERATURE_DOCUMENTATION_UNITS_URL = '/literature/sli/documentation-units'

export function usePutPublishAdmDocUnit(
  documentUnit: AdmDocumentationUnit,
): UseFetchReturn<AdmDocumentationUnit> {
  return useApiFetch(`${ADM_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}/publish`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToAdmDocUnit(data) : null,
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

export function usePutPublishUliDocUnit(
  documentUnit: UliDocumentationUnit,
): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(
    `${ULI_LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}/publish`,
    {
      afterFetch: ({ data }) => {
        return {
          data: data ? mapResponseToUliDocUnit(data) : null,
        }
      },
      immediate: false,
    },
  )
    .json()
    .put(documentUnit)
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
        }
      },
      immediate: false,
    },
  )
    .json()
    .put(documentUnit)
}

export function usePostAdmDocUnit(): UseFetchReturn<AdmDocumentationUnit> {
  return useApiFetch(ADM_DOCUMENTATION_UNITS_URL).json().post()
}

export function usePostUliDocUnit(): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(ULI_LITERATURE_DOCUMENTATION_UNITS_URL).json().post()
}

export function usePostSliDocUnit(): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(SLI_LITERATURE_DOCUMENTATION_UNITS_URL).json().post()
}

export function useGetAdmPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<AdmDocUnitSearchParams | undefined>,
): UseFetchReturn<AdmDocumentationUnit> {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${ADM_DOCUMENTATION_UNITS_URL}`, {
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

export function useGetAdmDocUnit(documentNumber: string): UseFetchReturn<AdmDocumentationUnit> {
  return useApiFetch(`${ADM_DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToAdmDocUnit(data) : null,
      }
    },
    immediate: false,
  }).json()
}

export function useGetUliDocUnit(documentNumber: string): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToUliDocUnit(data) : null,
      }
    },
    immediate: false,
  }).json()
}

export function useGetSliDocUnit(documentNumber: string): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToSliDocUnit(data) : null,
      }
    },
    immediate: false,
  }).json()
}

export function usePutAdmDocUnit(
  documentUnit: AdmDocumentationUnit,
): UseFetchReturn<AdmDocumentationUnit> {
  return useApiFetch(`${ADM_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToAdmDocUnit(data) : null,
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

export function usePutUliDocUnit(
  documentUnit: UliDocumentationUnit,
): UseFetchReturn<UliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToUliDocUnit(data) : null,
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

export function usePutSliDocUnit(
  documentUnit: SliDocumentationUnit,
): UseFetchReturn<SliDocumentationUnit> {
  return useApiFetch(`${LITERATURE_DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
    afterFetch: ({ data }) => {
      return {
        data: data ? mapResponseToSliDocUnit(data) : null,
      }
    },
    immediate: false,
  })
    .json()
    .put(documentUnit)
}

function mapResponseToSliDocUnit(data: SliDocumentUnitResponse): SliDocumentationUnit {
  const documentUnit: SliDocumentationUnit = {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
  }

  documentUnit.note = documentUnit.note || ''
  return documentUnit
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
