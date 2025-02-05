import { describe, it, expect } from 'vitest'
import service from '@/services/documentUnitService'
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
    expect(result.data?.uuid).toEqual('8de5e4a0-6b67-4d65-98db-efe877a260c4')
    expect(result.data?.documentNumber).toEqual('KSNR054920707')
    expect(result.error).toBeUndefined()
  })

  it('create new returns a new documentation unit', async () => {
    const result = await service.createNew()
    expect(result.status).toEqual(200)
    expect(result.data?.uuid).toEqual('8de5e4a0-6b67-4d65-98db-efe877a260c4')
    expect(result.data?.documentNumber).toEqual('KSNR054920707')
    expect(result.error).toBeUndefined()
  })

  it('searchByRelatedDocumentation', async () => {
    const result = await service.searchByRelatedDocumentation(new RelatedDocumentation())
    expect(result.status).toEqual(200)
  })
})
