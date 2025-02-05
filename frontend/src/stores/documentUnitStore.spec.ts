import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import DocumentUnit from '@/domain/documentUnit'
// import { RisJsonPatch } from '@/domain/risJsonPatch'
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
      const mockDocumentUnit = new DocumentUnit('123', {})
      const serviceResponse: ServiceResponse<DocumentUnit> = {
        status: 200,
        data: mockDocumentUnit,
        error: undefined,
      }

      const documentUnitServiceMock = vi
        .spyOn(documentUnitService, 'getByDocumentNumber')
        .mockResolvedValueOnce(serviceResponse)

      const store = useDocumentUnitStore()
      const response = await store.loadDocumentUnit('123')

      expect(documentUnitServiceMock).toHaveBeenCalledOnce()
      expect(response).toEqual(serviceResponse)
      expect(store.documentUnit).toEqual(mockDocumentUnit)
    })

    it('handles failure to load a document unit', async () => {
      const serviceResponse: ServiceResponse<DocumentUnit> = {
        status: 200,
        data: undefined,
        error: errorMessages.DOCUMENT_UNIT_COULD_NOT_BE_LOADED,
      }

      const documentUnitServiceMock = vi
        .spyOn(documentUnitService, 'getByDocumentNumber')
        .mockResolvedValueOnce(serviceResponse)

      const store = useDocumentUnitStore()
      const response = await store.loadDocumentUnit('123')

      expect(documentUnitServiceMock).toHaveBeenCalledOnce()
      expect(response).toEqual(serviceResponse)
      expect(store.documentUnit).toBeUndefined()
    })
  })
})
