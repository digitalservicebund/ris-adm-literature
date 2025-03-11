import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { type DocumentUnit } from '@/domain/documentUnit'
import errorMessages from '@/i18n/errors.json'
import documentUnitService from '@/services/documentUnitService'
import { type ServiceResponse } from '@/services/httpClient'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'
import { type DocumentUnitResponse } from '@/domain/documentUnitResponse.ts'

vi.mock('@/services/documentUnitService')

describe('useDocumentUnitStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })
  afterEach(() => {
    vi.resetAllMocks()
  })

  describe('loadDocumentUnit', () => {
    it('loads a document unit successfully', async () => {
      // given
      const documentUnit: DocumentUnit = {
        id: '123',
        documentNumber: 'KSNR054920707',
        references: [],
        fieldsOfLaw: [],
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

      const documentUnitServiceMock = vi
        .spyOn(documentUnitService, 'getByDocumentNumber')
        .mockResolvedValueOnce(serviceResponse)

      const store = useDocumentUnitStore()

      // when
      const response = await store.loadDocumentUnit('123')

      // then
      expect(documentUnitServiceMock).toHaveBeenCalledOnce()
      expect(response).toEqual(serviceResponse)
      expect(store.documentUnit).toEqual(documentUnit)
    })

    it('handles failure to load a document unit', async () => {
      // given
      const serviceResponse: ServiceResponse<DocumentUnitResponse> = {
        status: 400,
        error: errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED,
      }

      const documentUnitServiceMock = vi
        .spyOn(documentUnitService, 'getByDocumentNumber')
        .mockResolvedValueOnce(serviceResponse)

      const store = useDocumentUnitStore()

      // when
      const response = await store.loadDocumentUnit('123')

      // then
      expect(documentUnitServiceMock).toHaveBeenCalledOnce()
      expect(response).toEqual(serviceResponse)
      expect(store.documentUnit).toBeUndefined()
    })
  })

  it('updates a document unit successfully', async () => {
    // given
    const documentUnitForResponse: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
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

  it('updates a document unit fails', async () => {
    // given
    const documentUnit: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
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

  it('updates a document unit fails because access not allowed', async () => {
    // given
    const documentUnit: DocumentUnit = {
      id: '123',
      documentNumber: 'KSNR054920707',
      references: [],
      fieldsOfLaw: [],
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
