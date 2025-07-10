import type { FailedValidationServerResponse, ServiceResponse } from './httpClient'
import type { Page } from '@/components/Pagination.vue'
import type {
  DocumentUnit,
  DocumentUnitSearchParams,
  PaginatedDocumentUnitListResponse,
} from '@/domain/documentUnit'
import ActiveCitation from '@/domain/activeCitation'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import errorMessages from '@/i18n/errors.json'
import httpClient from './httpClient'
import type { DocumentUnitResponse } from '@/domain/documentUnitResponse.ts'
import Reference from '@/domain/reference.ts'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'
import { useApiFetch } from './apiService'
import { type UseFetchReturn } from '@vueuse/core'
import { buildUrlWithParams } from '@/utils/urlHelpers'
import { computed, type Ref } from 'vue'

const DOCUMENTATION_UNITS_URL = 'documentation-units'

export function useGetDocUnit(documentNumber: string): UseFetchReturn<DocumentUnit> {
  return useApiFetch(`/${DOCUMENTATION_UNITS_URL}/${documentNumber}`, {
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
  return useApiFetch(`/${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`, {
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

export function usePostDocUnit(documentUnit: DocumentUnit): UseFetchReturn<DocumentUnit> {
  return useApiFetch(`/${DOCUMENTATION_UNITS_URL}`, {
    afterFetch: ({ data }) => {
      if (!data) return { data }

      return {
        data: mapResponseDataToDocumentUnit(data),
      }
    },
  })
    .json()
    .post(documentUnit)
}

export function useGetPaginatedDocUnits(
  pageNumber: Ref<number>,
  pageSize: number,
  search: Ref<DocumentUnitSearchParams | undefined>,
): UseFetchReturn<DocumentUnit> {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`/${DOCUMENTATION_UNITS_URL}`, {
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

interface DocumentUnitService {
  getByDocumentNumber(documentNumber: string): Promise<ServiceResponse<DocumentUnitResponse>>

  createNew(): Promise<ServiceResponse<DocumentUnitResponse>>

  update(
    documentUnit: DocumentUnit,
  ): Promise<ServiceResponse<DocumentUnitResponse | FailedValidationServerResponse>>

  searchByRelatedDocumentation(
    query: RelatedDocumentation,
    requestParams?: { [key: string]: string } | undefined,
  ): Promise<ServiceResponse<{ activeCitations: RelatedDocumentation[]; page: Page }>>

  getPaginatedDocumentUnitList(
    pageNumber: number,
    pageSize: number,
    searchParams?: DocumentUnitSearchParams,
    sortByProperty?: string,
    sortDirection?: string,
  ): Promise<ServiceResponse<PaginatedDocumentUnitListResponse>>
}

function mapResponseDataToDocumentUnit(data: DocumentUnitResponse): DocumentUnit {
  const documentUnit: DocumentUnit = {
    ...data.json,
    id: data.id,
    documentNumber: data.documentNumber,
  }
  documentUnit.references = documentUnit.references?.map(
    (reference) => new Reference({ ...reference }),
  )
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

const service: DocumentUnitService = {
  async getByDocumentNumber(documentNumber: string) {
    const response = await httpClient.get<DocumentUnitResponse>(
      `${DOCUMENTATION_UNITS_URL}/${documentNumber}`,
    )
    if (response.status >= 300 || response.error) {
      response.data = undefined
      response.error = {
        title:
          response.status == 403
            ? errorMessages.DOCUMENT_UNIT_NOT_ALLOWED.title
            : errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED.title,
      }
    } else {
      response.data.json = mapResponseDataToDocumentUnit(response.data)
    }
    return response
  },

  async createNew() {
    const response = await httpClient.post<unknown, DocumentUnitResponse>(DOCUMENTATION_UNITS_URL, {
      headers: {
        Accept: 'application/json',
      },
    })
    if (response.status >= 300) {
      response.error = {
        title: errorMessages.DOCUMENT_UNIT_CREATION_FAILED.title,
      }
    } else {
      response.data = <DocumentUnitResponse>{
        ...(response.data as DocumentUnitResponse),
      }
    }
    return response
  },

  async update(documentUnit: DocumentUnit) {
    const response = await httpClient.put<
      DocumentUnit,
      DocumentUnitResponse | FailedValidationServerResponse
    >(
      `${DOCUMENTATION_UNITS_URL}/${documentUnit.documentNumber}`,
      {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      },
      documentUnit,
    )

    if (response.status == 200) {
      const data = response.data as DocumentUnitResponse
      data.json = mapResponseDataToDocumentUnit(data)
    } else if (response.status >= 300) {
      response.error = {
        title:
          response.status == 403
            ? errorMessages.NOT_ALLOWED.title
            : errorMessages.DOCUMENT_UNIT_UPDATE_FAILED.title,
      }
      // good enough condition to detect validation errors (@Valid)?
      if (response.status == 400 && JSON.stringify(response.data).includes('Validation failed')) {
        response.error.validationErrors = (response.data as FailedValidationServerResponse).errors
      } else {
        response.data = undefined
      }
    }
    return response
  },

  async searchByRelatedDocumentation(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    query: RelatedDocumentation = new RelatedDocumentation(),
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    requestParams: { string?: string } = {},
  ) {
    return {
      status: 200,
      data: {
        activeCitations: [
          new ActiveCitation({
            uuid: '123',
            court: {
              type: 'type1',
              location: 'location1',
              label: 'label1',
            },
            decisionDate: '2022-02-01',
            documentType: {
              abbreviation: 'VV',
              name: 'Verwaltungsvorschrift',
            },
            fileNumber: 'test fileNumber1',
          }),
        ],
        page: {
          size: 1,
          number: 0,
          numberOfElements: 1,
          totalElements: 20,
          first: true,
          last: false,
          empty: false,
        },
      },
    }
  },

  async getPaginatedDocumentUnitList(
    pageNumber: number,
    pageSize: number,
    search: DocumentUnitSearchParams,
  ) {
    const response = await httpClient.get<PaginatedDocumentUnitListResponse>(
      `${DOCUMENTATION_UNITS_URL}`,
      {
        params: {
          pageNumber: pageNumber.toString(),
          pageSize: pageSize.toString(),
          documentNumber: search?.documentNumber?.toString(),
          fundstellen: search?.fundstellen?.toString(),
          langueberschrift: search?.langueberschrift?.toString(),
          zitierdaten: search?.zitierdaten?.toString(),
          sortByProperty: 'documentNumber',
          sortDirection: 'DESC',
        },
      },
    )

    if (response.status >= 300) {
      response.error = {
        title: errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED.title,
      }
    }

    return response
  },
}

export default service
