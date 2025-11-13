import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'

// Mock the composables and missing fields function
const mockGetDocument = vi.fn()
const mockPutDocument = vi.fn()
const mockPublishDocument = vi.fn()
const mockMissingFields = vi.fn()

// Mock UseFetchReturn
const mockUseFetchReturn = {
  data: ref(null),
  error: ref(null),
  statusCode: ref(200),
  execute: vi.fn(),
}

describe('defineDocumentUnitStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockUseFetchReturn.data.value = null
    mockUseFetchReturn.error.value = null
    mockUseFetchReturn.statusCode.value = 200
    mockUseFetchReturn.execute.mockResolvedValue(undefined)
  })

  it('should initialize with null documentUnit and default state', () => {
    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })
    expect(store.documentUnit.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
  })

  it('should load a document and update state', async () => {
    const mockData = { id: '123', title: 'Test Document' }
    mockGetDocument.mockReturnValue({
      ...mockUseFetchReturn,
      data: ref(mockData),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    await store.load('123')

    expect(mockGetDocument).toHaveBeenCalledWith('123')
    expect(mockUseFetchReturn.execute).toHaveBeenCalled()
    expect(store.documentUnit.value).toEqual(mockData)
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
  })

  it('should set error if load fails', async () => {
    const mockError = new Error('Load failed')
    mockGetDocument.mockReturnValue({
      ...mockUseFetchReturn,
      error: ref(mockError),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    await store.load('123')

    expect(store.error.value).toEqual(mockError)
    expect(store.documentUnit.value).toBeNull()
  })

  it('should update a document and return true on success', async () => {
    const mockOriginal = { id: '123', title: 'Original Document' }
    mockGetDocument.mockReturnValue({
      ...mockUseFetchReturn,
      data: ref(mockOriginal),
    })
    const mockData = { id: '1', title: 'Updated Document' }
    mockPutDocument.mockReturnValue({
      ...mockUseFetchReturn,
      data: ref(mockData),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    await store.load('1')
    const result = await store.update()

    expect(mockPutDocument).toHaveBeenCalledWith(mockOriginal)
    expect(mockUseFetchReturn.execute).toHaveBeenCalled()
    expect(store.documentUnit.value).toEqual(mockData)
    expect(result).toBe(true)
  })

  it('should return false if update fails', async () => {
    const mockError = new Error('Update failed')
    mockPutDocument.mockReturnValue({
      ...mockUseFetchReturn,
      error: ref(mockError),
      statusCode: ref(500),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    store.documentUnit.value = { id: '1', title: 'Original Document' }
    const result = await store.update()

    expect(store.error.value).toEqual(mockError)
    expect(result).toBe(false)
  })

  it('should publish a document and return true on success', async () => {
    mockPublishDocument.mockReturnValue({
      ...mockUseFetchReturn,
      statusCode: ref(200),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    store.documentUnit.value = { id: '1', title: 'Document to Publish' }
    const result = await store.publish()

    expect(mockPublishDocument).toHaveBeenCalledWith(store.documentUnit.value)
    expect(mockUseFetchReturn.execute).toHaveBeenCalled()
    expect(result).toBe(true)
  })

  it('should return false if publish fails', async () => {
    const mockError = new Error('Publish failed')
    mockPublishDocument.mockReturnValue({
      ...mockUseFetchReturn,
      error: ref(mockError),
      statusCode: ref(500),
    })

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    store.documentUnit.value = { id: '1', title: 'Document to Publish' }
    const result = await store.publish()

    expect(store.error.value).toEqual(mockError)
    expect(result).toBe(false)
  })

  it('should unload the document', () => {
    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    store.documentUnit.value = { id: '1', title: 'Document' }
    store.unload()

    expect(store.documentUnit.value).toBeNull()
  })

  it('should return missing fields for the current document', () => {
    const mockMissing = ['field1', 'field2']
    mockMissingFields.mockReturnValue(mockMissing)

    const store = defineDocumentUnitStore({
      getDocument: mockGetDocument,
      putDocument: mockPutDocument,
      publishDocument: mockPublishDocument,
      missingFields: mockMissingFields,
    })

    store.documentUnit.value = { id: '1', title: 'Document' }
    expect(store.missingRequiredFields.value).toEqual(mockMissing)
  })
})
