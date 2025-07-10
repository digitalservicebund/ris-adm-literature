import { describe, it, expect, vi, beforeEach } from 'vitest'
import service, {
  useGetDocUnit,
  useGetPaginatedDocUnits,
  usePostDocUnit,
  usePutDocUnit,
} from '@/services/documentUnitService'
import HttpClient from '@/services/httpClient'
import RelatedDocumentation from '@/domain/relatedDocumentation'
import { type DocumentUnit } from '@/domain/documentUnit'
import { type DocumentUnitResponse } from '@/domain/documentUnitResponse'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'
import { ref } from 'vue'

describe('documentUnitService', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    vi.resetModules()
  })

  it('fetches a doc unit', async () => {
    const docUnit = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 5' })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 7' })] })],
      note: '',
    }

    const docUnitResp = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      json: docUnit,
    }

    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(docUnitResp), { status: 200 }),
    )

    const { data, error, isFetching, execute } = useGetDocUnit('KSNR054920707')
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeFalsy()
    expect(data.value).toEqual(docUnit)
  })

  it('returns an error on failed fetch ', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new Error('fetch failed'))

    const { data, error, isFetching, execute } = useGetDocUnit('KSNR054920708')
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeTruthy()
    expect(data.value).toBeNull()
  })

  it('updates a doc unit', async () => {
    const docUnit = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      fieldsOfLaw: [],
      references: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 5' })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 7' })] })],
      note: '',
    }

    const updatedResp = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      json: {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSNR054920707',
        note: 'updated',
      },
    }

    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(updatedResp), { status: 200 }),
    )

    const { data, error, isFetching, execute } = usePutDocUnit(docUnit)
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeFalsy()
    expect(data.value?.id).toBe(docUnit.id)
    expect(data.value?.note).toBe('updated')
  })

  it('returns an error on failed update', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new Error('fetch failed'))

    const { data, error, isFetching, execute } = usePutDocUnit({
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      note: '',
    })
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeTruthy()
    expect(data.value).toBeNull()
  })

  it('creates a doc unit', async () => {
    const createResp = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      json: {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSNR054920707',
        note: 'created',
      },
    }

    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(createResp), { status: 201 }),
    )

    const { data, error, isFetching, execute } = usePostDocUnit()
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeFalsy()
    expect(data.value?.id).toBe(createResp.id)
    expect(data.value?.note).toBe('created')
  })

  it('returns an error on failed creation', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new Error('fetch failed'))

    const { data, error, isFetching, execute } = usePostDocUnit()
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeTruthy()
    expect(data.value).toBeNull()
  })

  it('gets a paginated list of doc units', async () => {
    const fetchSpy = vi
      .spyOn(window, 'fetch')
      .mockResolvedValue(new Response(JSON.stringify({}), { status: 200 }))

    const { error, isFetching } = useGetPaginatedDocUnits(ref(5), 100, ref(undefined))
    await vi.waitFor(() => expect(fetchSpy).toHaveBeenCalledTimes(1))

    expect(isFetching.value).toBe(false)
    expect(fetchSpy).toHaveBeenCalledWith(
      '/api/documentation-units?pageNumber=5&pageSize=100&sortByProperty=documentNumber&sortDirection=DESC',
      expect.anything(),
    )
    expect(error.value).toBeFalsy()
  })

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
      note: '',
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
      note: '',
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
      note: '',
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
      note: '',
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
      note: '',
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
      note: '',
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

  it('returns a list of doc units', async () => {
    // given
    vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 200,
      data: 'foo',
    })

    // when
    const response = await service.getPaginatedDocumentUnitList(0, 1)

    // then
    expect(response.data).toBeTruthy()
  })

  it('populates the retured error on status code error', async () => {
    // given
    vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 401,
      data: 'foo',
    })

    // when
    const response = await service.getPaginatedDocumentUnitList(0, 1)

    // then
    expect(response.error).toBeTruthy()
  })

  it('getPaginatedDocumentUnitList calls HttpClient with correct parameters', async () => {
    // given
    const httpMock = vi.spyOn(HttpClient, 'get').mockResolvedValue({
      status: 200,
      data: 'foo',
    })

    // when
    await service.getPaginatedDocumentUnitList(2, 50, undefined)

    // then
    expect(httpMock).toHaveBeenCalledWith('documentation-units', {
      params: {
        pageNumber: '2',
        pageSize: '50',
        sortByProperty: 'documentNumber',
        sortDirection: 'DESC',
      },
    })
  })
})
