import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import MockAdapter from 'axios-mock-adapter'
import { axiosInstance } from '@/services/httpClient'
import FieldOfLawService from '@/services/fieldOfLawService.ts'

const fieldOfLawResponse = {
  fieldsOfLaw: [],
  page: {
    content: {
      fieldsOfLaw: [
        {
          hasChildren: false,
          identifier: 'PR-01',
          text: 'Arbeitsvertrag: Abschluss, Klauseln, Arten, Betriebsübergang',
          linkedFields: [],
          norms: [
            {
              abbreviation: 'BGB',
              singleNormDescription: '§ 611a',
            },
            {
              abbreviation: 'GewO',
              singleNormDescription: '§ 105',
            },
          ],
          children: [],
          parent: {
            hasChildren: true,
            identifier: 'PR',
            text: 'Phantasierecht',
            linkedFields: [],
            norms: [],
            children: [],
            parent: undefined,
          },
        },
      ],
    },
    size: 1,
    number: 1,
    numberOfElements: 1,
    first: true,
    last: true,
    empty: false,
  },
}

describe('fieldOfLawService', () => {
  let server: MockAdapter

  beforeEach(() => {
    server = new MockAdapter(axiosInstance)
  })

  afterEach(() => {
    server.restore()
  })

  it('responds with data property and no error when http code is 200', async () => {
    server.onAny().reply(200, fieldOfLawResponse)

    const response = await FieldOfLawService.getChildrenOf('root')

    expect(server.history.get).toBeDefined()
    expect(server.history.get[0].url).toBe('/api/lookup-tables/fields-of-law/root/children')
    expect(response.data).toBeDefined()
    expect(response.error).toBeUndefined()
  })

  it('responds with no data property and error when http code is >= 300', async () => {
    server.onAny().reply(400)

    const response = await FieldOfLawService.getChildrenOf('THIS_DOES_NOT_EXIST')

    expect(response.data).toBeUndefined()
    expect(response.error).toBeDefined()
  })

  it('responds with data property and no error when http code is 200 on get parent and children', async () => {
    server.onAny().reply(200, fieldOfLawResponse)

    const response = await FieldOfLawService.getParentAndChildrenForIdentifier('PR-01')

    expect(server.history.get).toBeDefined()
    expect(server.history.get[0].url).toBe('/api/lookup-tables/fields-of-law/PR-01')
    expect(response.data).toBeDefined()
    expect(response.error).toBeUndefined()
  })

  it('responds with no data property and error when http code is >= 300 get parent and children', async () => {
    server.onAny().reply(500)

    const response = await FieldOfLawService.getParentAndChildrenForIdentifier('PR-YY-XX-ZZ')

    expect(response.data).toBeUndefined()
    expect(response.error).toBeDefined()
  })

  it('responds with data property and no error when http code is 200 on search', async () => {
    server.onAny().reply(200, fieldOfLawResponse)

    const response = await FieldOfLawService.searchForFieldsOfLaw(0, 10, '', 'PR', '')

    expect(server.history.get).toBeDefined()
    expect(server.history.get[0].url).toBe(
      '/api/lookup-tables/fields-of-law?pageNumber=0&pageSize=10&identifier=PR&text=&norm=',
    )
    expect(response.data).toBeDefined()
    expect(response.error).toBeUndefined()
  })

  it('responds with no data property and error when http code is >= 300 on search', async () => {
    server.onAny().reply(500)

    const response = await FieldOfLawService.searchForFieldsOfLaw(0, 10, '', 'PR', '')

    expect(response.data).toBeUndefined()
    expect(response.error).toBeDefined()
  })
})
