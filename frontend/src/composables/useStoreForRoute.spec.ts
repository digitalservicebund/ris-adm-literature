import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useStoreForRoute } from '@/composables/useStoreForRoute'

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
let mockRoute: { meta: { storeId?: string } }
vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
}))

describe('useStoreForRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('returns admDocumentUnit store when route.meta.storeId="admDocumentUnit"', () => {
    mockRoute = { meta: { storeId: 'admDocumentUnit' } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockAdmStore)
  })

  it('returns uliDocumentUnit store when route.meta.storeId="uliDocumentUnit"', () => {
    mockRoute = { meta: { storeId: 'uliDocumentUnit' } }

    const store = useStoreForRoute()
    expect(store).toEqual(mockUliStore)
  })

  it('throws an error when route.meta.storeId is invalid', () => {
    mockRoute = { meta: { storeId: 'invalidStoreId' } }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.storeId="invalidStoreId"',
    )
  })

  it('throws an error when route.meta.storeId is missing', () => {
    mockRoute = { meta: {} }

    expect(() => useStoreForRoute()).toThrowError(
      'No store found for route meta.storeId="undefined"',
    )
  })
})
