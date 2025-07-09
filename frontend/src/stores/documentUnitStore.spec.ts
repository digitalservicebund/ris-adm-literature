import { describe, expect, it, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { ref } from 'vue'

describe('useDocumentUnitStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.resetModules()
  })

  it('loads a document unit successfully', async () => {
    // given
    const mockData = { documentNumber: '123', title: 'Sample Doc' }
    const execute = vi.fn().mockResolvedValue(undefined)

    vi.doMock('@/services/documentUnitService', () => ({
      useGetDocUnit: vi.fn().mockReturnValue({
        data: ref(mockData),
        error: ref(null),
        execute,
      }),
    }))

    const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')
    const store = useDocumentUnitStore()

    // when
    await store.loadDocumentUnit('123')

    // then
    expect(store.documentUnit).toEqual(mockData)
    expect(store.error).toBeNull()
    expect(store.isLoading).toBe(false)
    expect(execute).toHaveBeenCalled()
  })

  it('sets error when loading fails', async () => {
    // given
    const fetchError = new Error('Failed to fetch')
    const execute = vi.fn().mockResolvedValue(undefined)

    vi.doMock('@/services/documentUnitService', () => ({
      useGetDocUnit: vi.fn().mockReturnValue({
        data: ref(null),
        error: ref(fetchError),
        execute,
      }),
    }))

    const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')
    const store = useDocumentUnitStore()

    // when
    await store.loadDocumentUnit('wrong-id')

    // then
    expect(store.documentUnit).toBeNull()
    expect(store.error).toEqual(fetchError)
    expect(store.isLoading).toBe(false)
  })

  it('updates document unit successfully', async () => {
    // given
    const originalDoc = { documentNumber: '123', title: 'Original' }
    const updatedDoc = { documentNumber: '123', title: 'Updated' }
    const execute = vi.fn().mockResolvedValue(undefined)

    vi.doMock('@/services/documentUnitService', () => ({
      useGetDocUnit: vi.fn().mockReturnValue({
        data: ref(originalDoc),
        error: ref(null),
        execute: vi.fn().mockResolvedValue(undefined),
      }),
      usePutDocUnit: vi.fn().mockReturnValue({
        data: ref(updatedDoc),
        error: ref(null),
        statusCode: ref(200),
        execute,
      }),
    }))

    const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('123')

    // when
    const success = await store.updateDocumentUnit()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit).toEqual(updatedDoc)
    expect(store.error).toBeNull()
    expect(store.isLoading).toBe(false)
  })

  it('handles update error', async () => {
    // given
    const originalDoc = { documentNumber: '123', title: 'Original' }
    const putError = new Error('PUT failed')
    const execute = vi.fn().mockResolvedValue(undefined)

    vi.doMock('@/services/documentUnitService', () => ({
      useGetDocUnit: vi.fn().mockReturnValue({
        data: ref(originalDoc),
        error: ref(null),
        execute: vi.fn().mockResolvedValue(undefined),
      }),
      usePutDocUnit: vi.fn().mockReturnValue({
        data: ref(null),
        error: ref(putError),
        statusCode: ref(200),
        execute,
      }),
    }))

    const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('123')

    // when
    const success = await store.updateDocumentUnit()

    // then
    expect(success).toBe(false)
    expect(store.documentUnit).toEqual(originalDoc)
    expect(store.error).toEqual(putError)
    expect(store.isLoading).toBe(false)
  })

  it('clears document unit on unload', async () => {
    // given
    vi.doMock('@/services/documentUnitService', () => ({
      useGetDocUnit: vi.fn().mockReturnValue({
        data: ref({ documentNumber: 'abc' }),
        error: ref(null),
        execute: vi.fn().mockResolvedValue(undefined),
      }),
    }))

    const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('abc')

    // when
    store.unloadDocumentUnit()

    // then
    expect(store.documentUnit).toBeNull()
  })
})
