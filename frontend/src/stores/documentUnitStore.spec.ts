import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import DocumentUnit from '@/domain/documentUnit'
import errorMessages from '@/i18n/errors.json'
import documentUnitService from '@/services/documentUnitService'
import { type ServiceResponse } from '@/services/httpClient'
import { useDocumentUnitStore } from '@/stores/documentUnitStore'

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
      const mockDocumentUnit = new DocumentUnit({ uuid: '123', documentNumber: 'KSNR054920707' })
      const serviceResponse: ServiceResponse<DocumentUnit> = {
        status: 200,
        data: mockDocumentUnit,
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
      expect(store.documentUnit).toEqual(mockDocumentUnit)
    })

    it('handles failure to load a document unit', async () => {
      // given
      const serviceResponse: ServiceResponse<DocumentUnit> = {
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
})
