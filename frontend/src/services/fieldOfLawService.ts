import type { ServiceResponse } from './httpClient'
import type { Page } from '@/components/Pagination.vue'
import type { FieldOfLaw } from '@/domain/fieldOfLaw'
import fieldsOfLawMocking from './fieldsOfLaw.json'

const content: FieldOfLaw[] = fieldsOfLawMocking.map((fieldOfLaw) => {
  return {
    identifier: fieldOfLaw.identifier,
    text: fieldOfLaw.text,
    linkedFields: fieldOfLaw.linkedFields,
    norms: [],
    children: [],
    hasChildren: false,
  }
})

interface FieldOfLawService {
  getChildrenOf(identifier: string): Promise<ServiceResponse<FieldOfLaw[]>>
  getTreeForIdentifier(identifier: string): Promise<ServiceResponse<FieldOfLaw>>
  searchForFieldsOfLaw(
    page: number,
    size: number,
    query?: string,
    identifier?: string,
    norm?: string,
  ): Promise<ServiceResponse<Page<FieldOfLaw>>>
}

const service: FieldOfLawService = {
  async getChildrenOf(identifier: string) {
    console.log(identifier)
    return {
      status: 200,
      data: content,
    }
  },
  async getTreeForIdentifier(identifier: string) {
    console.log(identifier)
    return {
      status: 200,
      data: content[0],
    }
  },
  async searchForFieldsOfLaw(
    page: number,
    size: number,
    query?: string,
    identifier?: string,
    norm?: string,
  ) {
    console.log(page, size, query, identifier, norm)
    return {
      status: 200,
      data: {
        content,
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
