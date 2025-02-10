import type { ServiceResponse } from './httpClient'
import type { Page } from '@/components/Pagination.vue'
import DocumentUnit from '@/domain/documentUnit'
import ActiveCitation from '@/domain/activeCitation'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import errorMessages from '@/i18n/errors.json'

interface DocumentUnitService {
  getByDocumentNumber(documentNumber: string): Promise<ServiceResponse<DocumentUnit>>

  createNew(): Promise<ServiceResponse<DocumentUnit>>

  searchByRelatedDocumentation(
    query: RelatedDocumentation,
    requestParams?: { [key: string]: string } | undefined,
  ): Promise<ServiceResponse<Page<RelatedDocumentation>>>
}

const documents: {
  [documentNumber: string]: {
    id: string
    documentNumber: string
  }
} = {
  KSNR054920707: {
    id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
    documentNumber: 'KSNR054920707',
  },
}

const service: DocumentUnitService = {
  async getByDocumentNumber(documentNumber: string) {
    if (documents.hasOwnProperty(documentNumber)) {
      return {
        status: 200,
        data: new DocumentUnit({
          id: documents[documentNumber].id,
          documentNumber: documents[documentNumber].documentNumber,
        }),
      }
    }
    return {
      status: 400,
      error: errorMessages.DOCUMENT_UNIT_SEARCH_FAILED,
    }
  },

  async createNew() {
    return {
      status: 200,
      data: new DocumentUnit({
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSNR054920707',
      }),
    }
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
