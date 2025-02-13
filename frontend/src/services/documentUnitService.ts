import type { ServiceResponse } from './httpClient'
import type { Page } from '@/components/Pagination.vue'
import DocumentUnit from '@/domain/documentUnit'
import ActiveCitation from '@/domain/activeCitation'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import errorMessages from '@/i18n/errors.json'
import httpClient from './httpClient'

interface DocumentUnitService {
  getByDocumentNumber(documentNumber: string): Promise<ServiceResponse<DocumentUnit>>

  createNew(): Promise<ServiceResponse<DocumentUnit>>

  searchByRelatedDocumentation(
    query: RelatedDocumentation,
    requestParams?: { [key: string]: string } | undefined,
  ): Promise<ServiceResponse<Page<RelatedDocumentation>>>
}

const document: {
  id: string
  documentNumber: string
} = {
  id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
  documentNumber: 'KSNR054920707',
}

const service: DocumentUnitService = {
  async getByDocumentNumber(documentNumber: string) {
    if (documentNumber.startsWith('KSNR')) {
      return {
        status: 200,
        data: new DocumentUnit({
          id: document.id,
          documentNumber: documentNumber,
        }),
      }
    }
    return {
      status: 400,
      error: errorMessages.DOCUMENT_UNIT_SEARCH_FAILED,
    }
  },

  async createNew() {
    const response = await httpClient.post<unknown, DocumentUnit>('documentation-units', {
      headers: {
        Accept: 'application/json',
      },
    })
    if (response.status >= 300) {
      response.error = {
        title: errorMessages.DOCUMENT_UNIT_CREATION_FAILED.title,
      }
    } else {
      response.data = new DocumentUnit({
        ...(response.data as DocumentUnit),
      })
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
        content: [
          new ActiveCitation({
            uuid: '123',
            court: {
              type: 'type1',
              location: 'location1',
              label: 'label1',
            },
            decisionDate: '2022-02-01',
            documentType: {
              jurisShortcut: 'documentTypeShortcut1',
              label: 'documentType1',
            },
            fileNumber: 'test fileNumber1',
          }),
        ],
        size: 0,
        number: 0,
        numberOfElements: 20,
        first: true,
        last: false,
        empty: false,
      },
    }
  },
}

export default service
