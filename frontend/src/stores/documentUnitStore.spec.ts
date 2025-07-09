import { describe, expect, it, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { type DocumentUnit } from '@/domain/documentUnit'
import errorMessages from '@/i18n/errors.json'
import documentUnitService from '@/services/documentUnitService'
import { type ServiceResponse } from '@/services/httpClient'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { type DocumentUnitResponse } from '@/domain/documentUnitResponse.ts'
import { ref } from 'vue'

vi.mock('@/services/documentUnitService')

describe('useDocumentUnitStore', () => {
  beforeEach(() => {
    vi.resetModules()
    vi.clearAllMocks()
    setActivePinia(createPinia())
  })

  describe('loadDocumentUnit', () => {
    it('loads a document unit successfully', async () => {
      // given
      const documentUnit: DocumentUnit = {
        id: '123',
        documentNumber: 'KSNR054920707',
        references: [],
        fieldsOfLaw: [],
        note: '',
      }

      vi.doMock('@/services/documentUnitService', () => ({
        useGetDocUnit: vi.fn().mockResolvedValue({
          data: ref(documentUnit),
        }),
      }))

      const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')

      // when
      const store = useDocumentUnitStore()
      const response = await store.loadDocumentUnit('KSNR054920707')

      // then
      expect(response.data.value).toEqual(documentUnit)
      expect(store.documentUnit).toEqual(documentUnit)
    })

    it('handles failure to load a document unit', async () => {
      // given
      const errorResponse = {
        status: 400,
        error: errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED,
      }

      vi.doMock('@/services/documentUnitService', () => ({
        useGetDocUnit: vi.fn().mockResolvedValue({
          data: null,
          error: errorResponse,
        }),
      }))

      const { useDocumentUnitStore } = await import('@/stores/documentUnitStore')

      // when
      const store = useDocumentUnitStore()
      const response = await store.loadDocumentUnit('123')

      // then
      expect(response.error).toEqual(errorResponse)
      expect(store.documentUnit).toBeUndefined()
    })
  })

  it.skip('updates a document unit successfully', async () => {
    // given
    const documentUnitForResponse: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
      note: '',
    }
    const mockDocumentUnitResponse: DocumentUnitResponse = {
      id: '123',
      documentNumber: 'KSNR054920707',
      json: documentUnitForResponse,
    }
    const serviceResponse: ServiceResponse<DocumentUnitResponse> = {
      status: 200,
      data: mockDocumentUnitResponse,
      error: undefined,
    }
    vi.spyOn(documentUnitService, 'getByDocumentNumber').mockResolvedValueOnce(serviceResponse)
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('123')
    const documentUnitServiceMock = vi
      .spyOn(documentUnitService, 'update')
      .mockResolvedValueOnce(serviceResponse)

    // when
    const response = await store.updateDocumentUnit()

    // then
    expect(documentUnitServiceMock).toHaveBeenCalledOnce()
    expect(response).toEqual(serviceResponse)
    expect(store.documentUnit).toEqual(documentUnitForResponse)
  })

  it.skip('updates a document unit fails', async () => {
    // given
    const documentUnit: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
      note: '',
    }
    const mockDocumentUnitResponse: DocumentUnitResponse = {
      id: '123',
      documentNumber: 'KSNR054920707',
      json: documentUnit,
    }
    const serviceResponse: ServiceResponse<DocumentUnitResponse> = {
      status: 200,
      data: mockDocumentUnitResponse,
      error: undefined,
    }
    vi.spyOn(documentUnitService, 'getByDocumentNumber').mockResolvedValueOnce(serviceResponse)
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('123')
    const documentUnitServiceMock = vi.spyOn(documentUnitService, 'update').mockResolvedValueOnce({
      status: 400,
      data: undefined,
      error: { title: 'Error' },
    })

    // when
    const response = await store.updateDocumentUnit()

    // then
    expect(documentUnitServiceMock).toHaveBeenCalledOnce()
    expect(response.error?.title).toEqual('Dokumentationseinheit konnte nicht aktualisiert werden.')
  })

  it.skip('updates a document unit fails because access not allowed', async () => {
    // given
    const documentUnit: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
      note: '',
    }
    const mockDocumentUnitResponse: DocumentUnitResponse = {
      id: '123',
      documentNumber: 'KSNR054920707',
      json: documentUnit,
    }
    const serviceResponse: ServiceResponse<DocumentUnitResponse> = {
      status: 200,
      data: mockDocumentUnitResponse,
      error: undefined,
    }
    vi.spyOn(documentUnitService, 'getByDocumentNumber').mockResolvedValueOnce(serviceResponse)
    const store = useDocumentUnitStore()
    await store.loadDocumentUnit('123')
    const documentUnitServiceMock = vi.spyOn(documentUnitService, 'update').mockResolvedValueOnce({
      status: 403,
      data: undefined,
      error: { title: 'Forbidden' },
    })

    // when
    const response = await store.updateDocumentUnit()

    // then
    expect(documentUnitServiceMock).toHaveBeenCalledOnce()
    expect(response.error?.title).toEqual('Keine Berechtigung')
  })

  it('updates a document unit fails due to empty store', async () => {
    // given
    const store = useDocumentUnitStore()

    // when
    const response = await store.updateDocumentUnit()

    // then
    expect(response.error?.title).toBe('Dokumentationseinheit konnte nicht geladen werden.')
    expect(store.documentUnit).toBeUndefined()
  })
})
