import { describe, it, expect, vi } from 'vitest'
import service from '@/services/documentUnitService'
import HttpClient from '@/services/httpClient'
import RelatedDocumentation from '@/domain/relatedDocumentation'

describe('documentUnitService', () => {
  it('appends correct error message if status 500', async () => {
    const result = await service.getByDocumentNumber('KSNRXXXXXX')
    expect(result.error?.title).toEqual('Die Suchergebnisse konnten nicht geladen werden.')
    expect(result.error?.description).toEqual('Bitte versuchen Sie es später erneut.')
    expect(result.data).toBeUndefined()
  })

  it('returns correct documentation unit if exist', async () => {
    const result = await service.getByDocumentNumber('KSNR054920707')
    expect(result.data?.id).toEqual('8de5e4a0-6b67-4d65-98db-efe877a260c4')
    expect(result.data?.documentNumber).toEqual('KSNR054920707')
    expect(result.error).toBeUndefined()
  })

  it('create new returns a new documentation unit', async () => {
    // given
    const httpMock = vi.spyOn(HttpClient, 'post').mockResolvedValue({
      status: 200,
      data: 'foo',
    })

    // when
    await service.createNew()

    // then
    expect(httpMock).toHaveBeenCalledWith('documentation-units', {
      headers: {
        Accept: 'application/json',
      },
    })
  })

  it('searchByRelatedDocumentation', async () => {
    const result = await service.searchByRelatedDocumentation(new RelatedDocumentation())
    expect(result.status).toEqual(200)
  })
})
