import { describe, it, expect, vi } from 'vitest'
import service from '@/services/documentUnitService'
import HttpClient from '@/services/httpClient'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import DocumentUnit from '@/domain/documentUnit.ts'

describe('documentUnitService', () => {
  it('appends correct error message if status 500', async () => {
    const result = await service.getByDocumentNumber('XXXXXX')
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

  it('server error on attempting creation of new documentation unit', async () => {
    // given
    vi.spyOn(HttpClient, 'post').mockResolvedValue({
      status: 300,
      data: '',
    })

    // when
    const response = await service.createNew()

    // then
    expect(response.error?.title).toBe('Neue Dokumentationseinheit konnte nicht erstellt werden.')
  })

  it('update given document unit', async () => {
    // given
    const httpMock = vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 200,
      data: 'foo',
    })
    const documentUnit = new DocumentUnit({
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    })

    // when
    await service.update(documentUnit)

    // then
    expect(httpMock).toHaveBeenCalledWith(
      'documentation-units/KSNR000000003',
      {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      },
      documentUnit,
    )
  })

  it('validation error on updating a documentation unit', async () => {
    // given
    vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 400,
      data: { errors: [{ code: 'test', message: 'Validation failed', instance: 'local' }] },
    })
    const documentUnit = new DocumentUnit({
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    })

    // when
    const response = await service.update(documentUnit)

    // then
    expect(response.error?.validationErrors?.[0].code).toBe('test')
  })

  it('validation error on updating a documentation unit without error response', async () => {
    // given
    vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 400,
      data: 'something really strange happened',
    })
    const documentUnit = new DocumentUnit({
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    })

    // when
    const response = await service.update(documentUnit)

    // then
    expect(response.data).toBeUndefined()
  })

  it('server error on updating a documentation unit', async () => {
    // given
    vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 500,
      data: '',
    })
    const documentUnit = new DocumentUnit({
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    })

    // when
    const response = await service.update(documentUnit)

    // then
    expect(response.error?.title).toBe('Dokumentationseinheit konnte nicht aktualisiert werden.')
  })

  it('server error on updating a documentation unit - access not allowed', async () => {
    // given
    vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 403,
      data: '',
    })
    const documentUnit = new DocumentUnit({
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    })

    // when
    const response = await service.update(documentUnit)

    // then
    expect(response.error?.title).toBe('Keine Berechtigung')
  })

  it('searchByRelatedDocumentation', async () => {
    const result = await service.searchByRelatedDocumentation(new RelatedDocumentation())
    expect(result.status).toEqual(200)
  })
})
