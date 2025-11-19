import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import { DocumentCategory } from '@/domain/documentType'

// Mocks for stores
const mockAdmStore = { name: 'admStore' }
const mockUliStore = { name: 'uliStore' }

// Mock modules
vi.mock('@/stores/admDocumentUnitStore', () => ({
  useAdmDocUnitStore: vi.fn(() => mockAdmStore),
}))
vi.mock('@/stores/uliDocStore', () => ({
  useUliDocumentUnitStore: vi.fn(() => mockUliStore),
}))

// Mock vue-router useRoute
let mockRoute: { meta: { documentCategory?: string } }
vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
}))

describe('useStoreForRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('returns admDocumentUnit store when route.meta.documentCategory="VERWALTUNGSVORSCHRIFTEN"', () => {
    mockRoute = { meta: { documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockAdmStore)
  })

  it('returns uliDocumentUnit store when route.meta.documentCategory="LITERATUR_UNSELBSTAENDIG"', () => {
    mockRoute = { meta: { documentCategory: DocumentCategory.LITERATUR_UNSELBSTAENDIG } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockUliStore)
  })

  it('throws an error when route.meta.documentCategory is invalid', () => {
    mockRoute = { meta: { documentCategory: 'invalidDocType' } }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.documentCategory="invalidDocType"',
    )
  })

  it('throws an error when route.meta.documentCategory is missing', () => {
    mockRoute = { meta: {} }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.documentCategory="undefined"',
    )
  })
})
