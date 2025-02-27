import { describe, it, expect, vi } from 'vitest'
import service from '@/services/documentUnitService'
import HttpClient from '@/services/httpClient'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import { type DocumentUnit } from '@/domain/documentUnit'
import { type DocumentUnitResponse } from '@/domain/documentUnitResponse'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'

describe('documentUnitService', () => {
  it('returns correct documentation unit if exist', async () => {
    // given
    const documentUnit: DocumentUnit = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 5' })] }),
      ],
      // this line does not make sense for the test, but otherwise the mapping of the response data does not get test coverage
      // (it does, actually, in an e2e test, but our tooling does not get it)
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 7' })] })],
    }
    vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 200,
      data: <DocumentUnitResponse>{
        id: documentUnit.id,
        documentNumber: documentUnit.documentNumber,
        json: documentUnit,
      },
    })

    // when
    const result = await service.getByDocumentNumber('KSNR054920707')

    // then
    expect(result.data?.id).toEqual('8de5e4a0-6b67-4d65-98db-efe877a260c4')
    expect(result.data?.documentNumber).toEqual('KSNR054920707')
    expect(result.error).toBeUndefined()
  })

  it('server error on finding a documentation unit - access not allowed', async () => {
    // given
    vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 403,
      data: 'foo',
    })

    // when
    const result = await service.getByDocumentNumber('XXXXXX')

    // then
    expect(result.error?.title).toEqual(
      'Diese Dokumentationseinheit existiert nicht oder Sie haben keine Berechtigung.',
    )
    expect(result.data).toBeUndefined()
  })

  it('server error on finding a documentation unit - ', async () => {
    // given
    vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 500,
      data: 'foo',
    })

    // when
    const result = await service.getByDocumentNumber('XXXXXX')

    // then
    expect(result.error?.title).toEqual('Dokumentationseinheit konnte nicht geladen werden.')
    expect(result.data).toBeUndefined()
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
    const documentUnit: DocumentUnit = {
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      references: [],
    }
    const httpMock = vi.spyOn(HttpClient, 'put').mockResolvedValue({
      status: 200,
      data: <DocumentUnitResponse>{
        id: documentUnit.id,
        documentNumber: documentUnit.documentNumber,
        json: documentUnit,
      },
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
    const documentUnit: DocumentUnit = {
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      fieldsOfLaw: [],
      references: [],
    }

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
    const documentUnit: DocumentUnit = {
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [],
    }

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
    const documentUnit: DocumentUnit = {
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [],
    }

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
    const documentUnit: DocumentUnit = {
      id: 'uuid',
      documentNumber: 'KSNR000000003',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [],
    }

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
