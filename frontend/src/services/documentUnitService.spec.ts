import { describe, it, expect, vi, beforeEach } from 'vitest'
import {
  useGetDocUnit,
  useGetPaginatedDocUnits,
  usePostDocUnit,
  usePutDocUnit,
  usePutPublishDocUnit,
} from '@/services/documentUnitService'
import ActiveReference from '@/domain/activeReference.ts'
import SingleNorm from '@/domain/singleNorm.ts'
import NormReference from '@/domain/normReference'
import { ref } from 'vue'
import { until } from '@vueuse/core'
import ActiveCitation from '@/domain/activeCitation'
import { activeCitationFixture } from '@/testing/fixtures/activeCitation'

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
      fundstellen: [],
      activeCitations: [new ActiveCitation(activeCitationFixture)],
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

  it('data is null when fetch returns a null body', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response(JSON.stringify(null), { status: 200 }))

    const { data, execute } = useGetDocUnit('KSNR054920707')
    await execute()

    expect(data.value).toEqual(null)
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
      fundstellen: [],
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

  it('data is null when update call returns a null body', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response(JSON.stringify(null), { status: 200 }))

    const { data, execute } = usePutDocUnit({
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      note: '',
    })
    await execute()

    expect(data.value).toEqual(null)
  })

  it('publishes a doc unit', async () => {
    const docUnit = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      fieldsOfLaw: [],
      fundstellen: [],
      activeCitations: [],
      activeReferences: [
        new ActiveReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 5' })] }),
      ],
      normReferences: [new NormReference({ singleNorms: [new SingleNorm({ singleNorm: '§ 7' })] })],
      note: '',
    }

    const publishedResp = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      json: {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSNR054920707',
      },
    }

    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(publishedResp), { status: 200 }),
    )

    const { data, error, isFetching, execute } = usePutPublishDocUnit(docUnit)
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeFalsy()
    expect(data.value?.id).toBe(docUnit.id)
  })

  it('returns an error on failed publication', async () => {
    vi.spyOn(window, 'fetch').mockRejectedValue(new Error('fetch failed'))

    const { data, error, isFetching, execute } = usePutPublishDocUnit({
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      note: '',
    })
    await execute()

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeTruthy()
    expect(data.value).toBeNull()
  })

  it('data is null when publish returns a null body', async () => {
    vi.spyOn(window, 'fetch').mockResolvedValue(new Response(JSON.stringify(null), { status: 200 }))

    const { data, execute } = usePutPublishDocUnit({
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      note: '',
    })
    await execute()

    expect(data.value).toEqual(null)
  })

  it('creates a doc unit', async () => {
    const createResp = {
      id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
      documentNumber: 'KSNR054920707',
      json: {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSNR054920707',
      },
    }

    vi.spyOn(window, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(createResp), { status: 201 }),
    )

    const { data, error, isFetching, isFinished } = usePostDocUnit()
    await until(isFinished).toBe(true)

    expect(isFetching.value).toBe(false)
    expect(error.value).toBeFalsy()
    expect(data.value?.id).toBe(createResp.id)
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
})
