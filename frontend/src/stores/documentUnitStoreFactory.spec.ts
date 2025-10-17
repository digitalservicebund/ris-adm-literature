import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'

interface MockDocument {
  documentNumber: string
  id: string
  json: string
}

describe('defineDocumentUnitStore', () => {
  let execute: ReturnType<typeof vi.fn>

  beforeEach(() => {
    execute = vi.fn().mockResolvedValue(undefined)
  })

  it('loads a document unit successfully', async () => {
    // given
    const mockData: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Sample Doc' }
    const getFn = vi.fn().mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      execute,
    })

    const updateFn = vi.fn() // unused here
    const store = defineDocumentUnitStore(getFn, updateFn)

    // when
    await store.load('123')

    // then
    expect(getFn).toHaveBeenCalledWith('123')
    expect(execute).toHaveBeenCalled()
    expect(store.documentUnit.value).toEqual(mockData)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('sets error when loading fails', async () => {
    // given
    const fetchError = new Error('Fetch failed')
    const getFn = vi.fn().mockReturnValue({
      data: ref(null),
      error: ref(fetchError),
      execute,
    })

    const store = defineDocumentUnitStore<MockDocument>(getFn, vi.fn())

    // when
    await store.load('does-not-exist')

    // then
    expect(store.documentUnit.value).toBeNull()
    expect(store.error.value).toEqual(fetchError)
    expect(store.isLoading.value).toBe(false)
  })

  it('updates document unit successfully', async () => {
    // given
    const originalDoc: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Original' }
    const updatedDoc: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Updated' }

    const getFn = vi.fn().mockReturnValue({
      data: ref(originalDoc),
      error: ref(null),
      execute: vi.fn().mockResolvedValue(undefined),
    })

    const updateFn = vi.fn().mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute,
    })

    const store = defineDocumentUnitStore<MockDocument>(getFn, updateFn)
    await store.load('123')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('handles update error correctly', async () => {
    // given
    const originalDoc: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Original' }
    const putError = new Error('PUT failed')

    const getFn = vi.fn().mockReturnValue({
      data: ref(originalDoc),
      error: ref(null),
      execute: vi.fn().mockResolvedValue(undefined),
    })

    const updateFn = vi.fn().mockReturnValue({
      data: ref(null),
      error: ref(putError),
      statusCode: ref(500),
      execute,
    })

    const store = defineDocumentUnitStore<MockDocument>(getFn, updateFn)
    await store.load('123')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(false)
    expect(store.error.value).toEqual(putError)
    expect(store.documentUnit.value).toEqual(originalDoc)
    expect(store.isLoading.value).toBe(false)
  })

  it('returns false when update is called without a document', async () => {
    const store = defineDocumentUnitStore<MockDocument>(vi.fn(), vi.fn())

    const success = await store.update()

    expect(success).toBe(false)
    expect(store.isLoading.value).toBe(false)
  })

  it('clears document unit on unload', async () => {
    // given
    const mockDoc: MockDocument = { documentNumber: '1', id: 'docTestId', json: 'Doc' }
    const getFn = vi.fn().mockReturnValue({
      data: ref(mockDoc),
      error: ref(null),
      execute,
    })

    const store = defineDocumentUnitStore<MockDocument>(getFn, vi.fn())
    await store.load('1')

    // when
    store.unload()

    // then
    expect(store.documentUnit.value).toBeNull()
  })
})
