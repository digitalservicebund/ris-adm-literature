import { describe, it, expect } from 'vitest'
import { buildUrlWithParams } from './urlHelpers'

describe('buildUrlWithParams', () => {
  it('appends single query param', () => {
    const url = buildUrlWithParams('/api/test', { lang: 'en' })
    expect(url).toBe('/api/test?lang=en')
  })

  it('appends multiple query params', () => {
    const url = buildUrlWithParams('/api/data', {
      page: 2,
      sort: 'asc',
      active: true,
    })
    expect(url).toBe('/api/data?page=2&sort=asc&active=true')
  })

  it('ignores undefined, null, and empty string values', () => {
    const url = buildUrlWithParams('/api/items', {
      search: '',
      type: undefined,
      category: null,
      visible: true,
    })
    expect(url).toBe('/api/items?visible=true')
  })

  it('returns baseUrl when no valid params', () => {
    const url = buildUrlWithParams('/api/empty', {
      search: '',
      page: undefined,
      filter: null,
    })
    expect(url).toBe('/api/empty')
  })

  it('encodes special characters', () => {
    const url = buildUrlWithParams('/api/query', {
      q: 'coffee & cream',
      filter: 'name/email',
    })
    expect(url).toBe('/api/query?q=coffee+%26+cream&filter=name%2Femail')
  })
})
