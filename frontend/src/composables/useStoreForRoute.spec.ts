import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useStoreForRoute } from '@/composables/useStoreForRoute'
import { DocumentTypeCode } from '@/domain/documentType'

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
let mockRoute: { meta: { documentTypeCode?: string } }
vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
}))

describe('useStoreForRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('returns admDocumentUnit store when route.meta.documentTypeCode="VERWALTUNGSVORSCHRIFTEN"', () => {
    mockRoute = { meta: { documentTypeCode: DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockAdmStore)
  })

  it('returns uliDocumentUnit store when route.meta.documentTypeCode="LITERATUR_UNSELBSTSTAENDIG"', () => {
    mockRoute = { meta: { documentTypeCode: DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockUliStore)
  })

  it('throws an error when route.meta.documentTypeCode is invalid', () => {
    mockRoute = { meta: { documentTypeCode: 'invalidDocType' } }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.documentTypeCode="invalidDocType"',
    )
  })

  it('throws an error when route.meta.documentTypeCode is missing', () => {
    mockRoute = { meta: {} }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.documentTypeCode="undefined"',
    )
  })
})
