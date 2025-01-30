import RelatedDocumentation from '@/domain/relatedDocumentation'

interface DocumentUnitService {
  searchByRelatedDocumentation(
    query: RelatedDocumentation,
    requestParams?: { [key: string]: string } | undefined,
  ): unknown
}

const service: DocumentUnitService = {
  async searchByRelatedDocumentation(
    query: RelatedDocumentation = new RelatedDocumentation(),
    requestParams: { string?: string } = {},
  ) {
    return {
      status: 200,
      data: {
        content: [
          {
            uuid: null,
            newEntry: false,
            documentNumber: 'YYTestDoc0014',
            status: {
              publicationStatus: 'PUBLISHED',
              withError: true,
              createdAt: null,
            },
            court: {
              id: '95bc0906-3dc9-4785-98c9-bd4f137327ac',
              type: 'AG',
              location: 'Aachen',
              label: 'AG Aachen',
              revoked: null,
              responsibleDocOffice: {
                abbreviation: 'BGH',
                uuid: '7d5bb15b-7190-45fa-9416-62fb3624f161',
              },
            },
            decisionDate: '1989-01-01',
            fileNumber: null,
            documentType: {
              uuid: '98fda464-2513-4de0-a86e-f2e789a2fc95',
              jurisShortcut: 'BE',
              label: 'Beschluss',
            },
            createdByReference: null,
            documentationOffice: {
              abbreviation: 'DS',
              uuid: '58bfa31a-4cba-4fcd-88a0-4bf02810cde9',
            },
            creatingDocOffice: null,
            hasPreviewAccess: false,
          },
        ],
        pageable: {
          pageNumber: 0,
          pageSize: 15,
          sort: {
            sorted: false,
            unsorted: true,
            empty: true,
          },
          offset: 0,
          paged: true,
          unpaged: false,
        },
        first: true,
        last: true,
        size: 15,
        number: 0,
        sort: {
          sorted: false,
          unsorted: true,
          empty: true,
        },
        numberOfElements: 1,
        empty: false,
      },
    }
  },
}

export default service
