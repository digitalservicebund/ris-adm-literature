import httpClient, { type ServiceResponse } from './httpClient'
import type { Page } from '@/components/Pagination.vue'
import type { FieldOfLaw } from '@/domain/fieldOfLaw'
import fieldsOfLawMocking from './fieldsOfLaw.json'
import errorMessages from '@/i18n/errors.json'

const content: FieldOfLaw[] = fieldsOfLawMocking.map((fieldOfLaw) => {
  return {
    identifier: fieldOfLaw.identifier,
    text: fieldOfLaw.text,
    linkedFields: fieldOfLaw.linkedFields,
    norms: [],
    children: [],
    hasChildren: fieldOfLaw.text == 'Arbeitsrecht',
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
    const response = await httpClient.get<{ fieldsOfLaw: FieldOfLaw[] }>(
      `lookup-tables/fields-of-law/${identifier}/children`,
    )
    if (response.status >= 300) {
      response.error = {
        title: errorMessages.FIELDS_OF_LAW_COULD_NOT_BE_LOADED.title.replace(
          '${identifier}',
          identifier,
        ),
      }
    }

    if (response.error) return response

    return {
      status: response.status,
      data: response.data?.fieldsOfLaw,
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
