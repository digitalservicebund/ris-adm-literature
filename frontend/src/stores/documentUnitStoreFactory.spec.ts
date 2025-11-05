import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import { DocumentCategory } from '@/domain/documentType'
import * as documentUnitService from '@/services/documentUnitService'
import type { UseFetchReturn } from '@vueuse/core'

interface MockDocument {
  documentNumber: string
  id: string
  json: string
}

describe('defineDocumentUnitStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads a document unit successfully', async () => {
    // given
    const mockData: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Sample Doc' }
    const executeMock = vi.fn()

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(mockData),
      error: ref(null),
      statusCode: ref(200),
      execute: executeMock,
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    // when
    await store.load('123')

    // then
    expect(executeMock).toHaveBeenCalledTimes(1)
    expect(store.documentUnit.value).toEqual(mockData)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('sets error when loading fails', async () => {
    // given
    const fetchError = new Error('Fetch failed')

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(null),
      error: ref(fetchError),
      statusCode: ref(500),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

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

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(originalDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    vi.spyOn(documentUnitService, 'usePutAdmDocUnit').mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    await store.load('123')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('updates the error state and leaves the original document untouched on a failed update', async () => {
    // given
    const originalDoc: MockDocument = { documentNumber: '123', id: 'docTestId', json: 'Original' }
    const putError = new Error('PUT failed')

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(originalDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn().mockResolvedValue(undefined),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    vi.spyOn(documentUnitService, 'usePutAdmDocUnit').mockReturnValue({
      data: ref(null),
      error: ref(putError),
      statusCode: ref(500),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    await store.load('123')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(false)
    expect(store.error.value).toEqual(putError)
    expect(store.documentUnit.value).toEqual(originalDoc)
    expect(store.isLoading.value).toBe(false)
  })

  it('calling update returns false when no document is stored and state remains unchanged', async () => {
    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    const success = await store.update()

    expect(success).toBe(false)
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
    expect(store.documentUnit.value).toBeNull()
  })

  it('clears document unit on unload', async () => {
    // given
    const mockDoc: MockDocument = { documentNumber: '1', id: 'docTestId', json: 'Doc' }

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(mockDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn().mockResolvedValue(undefined),
    } as Partial<UseFetchReturn<MockDocument>> as UseFetchReturn<MockDocument>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    // when
    await store.load('1')

    // then
    expect(store.documentUnit.value).toEqual(mockDoc)

    // when
    store.unload()

    // then
    expect(store.documentUnit.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
  })
})
