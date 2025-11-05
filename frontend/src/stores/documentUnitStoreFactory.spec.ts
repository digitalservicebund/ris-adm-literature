import { describe, it, expect, vi, beforeEach } from 'vitest'
import { ref } from 'vue'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import { DocumentCategory } from '@/domain/documentType'
import * as documentUnitService from '@/services/documentUnitService'
import type { UseFetchReturn } from '@vueuse/core'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import { admDocumentUnitFixture } from '@/testing/fixtures/admDocumentUnit'
import { uliDocumentUnitFixture } from '@/testing/fixtures/uliDocumentUnit'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'

describe('defineDocumentUnitStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads a adm document unit successfully', async () => {
    // given
    const executeMock = vi.fn()
    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(admDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: executeMock,
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    // when
    await store.load('KSNR054920707')

    // then
    expect(executeMock).toHaveBeenCalledTimes(1)
    expect(store.documentUnit.value).toEqual(admDocumentUnitFixture)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('loads a uli document unit successfully', async () => {
    // given
    const executeMock = vi.fn()

    vi.spyOn(documentUnitService, 'useGetUliDocUnit').mockReturnValue({
      data: ref(uliDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: executeMock,
    } as Partial<UseFetchReturn<UliDocumentationUnit>> as UseFetchReturn<UliDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG)

    // when
    await store.load('KSLU054920707')

    // then
    expect(executeMock).toHaveBeenCalledTimes(1)
    expect(store.documentUnit.value).toEqual(uliDocumentUnitFixture)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('sets error when loading fails', async () => {
    // given
    const fetchError = new Error('Fetch failed')

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(null),
      error: ref(fetchError),
      statusCode: ref(500),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    // when
    await store.load('does-not-exist')

    // then
    expect(store.documentUnit.value).toBeNull()
    expect(store.error.value).toEqual(fetchError)
    expect(store.isLoading.value).toBe(false)
  })

  it('updates a adm document unit successfully', async () => {
    // given
    const updatedDoc: AdmDocumentationUnit = { ...admDocumentUnitFixture, note: 'updated' }

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(admDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    vi.spyOn(documentUnitService, 'usePutAdmDocUnit').mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    await store.load('KSNR054920707')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('updates a uli document unit successfully', async () => {
    // given
    const updatedDoc: AdmDocumentationUnit = { ...uliDocumentUnitFixture, note: 'updated' }

    vi.spyOn(documentUnitService, 'useGetUliDocUnit').mockReturnValue({
      data: ref(uliDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<UliDocumentationUnit>> as UseFetchReturn<UliDocumentationUnit>)

    vi.spyOn(documentUnitService, 'usePutUliDocUnit').mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<UliDocumentationUnit>> as UseFetchReturn<UliDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG)
    await store.load('KSLU054920707')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('publishes a adm document unit successfully', async () => {
    // given
    const updatedDoc: AdmDocumentationUnit = { ...admDocumentUnitFixture, note: 'updated' }

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(admDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    vi.spyOn(documentUnitService, 'usePutPublishAdmDocUnit').mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    await store.load('KSNR054920707')

    // when
    const success = await store.publish()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('publishes a uli document unit successfully', async () => {
    // given
    const updatedDoc: AdmDocumentationUnit = { ...uliDocumentUnitFixture, note: 'updated' }

    vi.spyOn(documentUnitService, 'useGetUliDocUnit').mockReturnValue({
      data: ref(uliDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<UliDocumentationUnit>> as UseFetchReturn<UliDocumentationUnit>)

    vi.spyOn(documentUnitService, 'usePutPublishUliDocUnit').mockReturnValue({
      data: ref(updatedDoc),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<UliDocumentationUnit>> as UseFetchReturn<UliDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG)
    await store.load('KSLU054920707')

    // when
    const success = await store.publish()

    // then
    expect(success).toBe(true)
    expect(store.documentUnit.value).toEqual(updatedDoc)
    expect(store.error.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
  })

  it('updates the error state and leaves the original document untouched on a failed update', async () => {
    // given
    const putError = new Error('PUT failed')

    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(admDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn().mockResolvedValue(undefined),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    vi.spyOn(documentUnitService, 'usePutAdmDocUnit').mockReturnValue({
      data: ref(null),
      error: ref(putError),
      statusCode: ref(500),
      execute: vi.fn(),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    await store.load('KSNR054920707')

    // when
    const success = await store.update()

    // then
    expect(success).toBe(false)
    expect(store.error.value).toEqual(putError)
    expect(store.documentUnit.value).toEqual(admDocumentUnitFixture)
    expect(store.isLoading.value).toBe(false)
  })

  it('calling update returns false when no document is stored and state remains unchanged', async () => {
    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    const success = await store.update()

    expect(success).toBe(false)
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
    expect(store.documentUnit.value).toBeNull()
  })

  it('clears document unit on unload', async () => {
    // given
    vi.spyOn(documentUnitService, 'useGetAdmDocUnit').mockReturnValue({
      data: ref(admDocumentUnitFixture),
      error: ref(null),
      statusCode: ref(200),
      execute: vi.fn().mockResolvedValue(undefined),
    } as Partial<UseFetchReturn<AdmDocumentationUnit>> as UseFetchReturn<AdmDocumentationUnit>)

    const store = defineDocumentUnitStore(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)

    // when
    await store.load('KSNR054920707')

    // then
    expect(store.documentUnit.value).toEqual(admDocumentUnitFixture)

    // when
    store.unload()

    // then
    expect(store.documentUnit.value).toBeNull()
    expect(store.isLoading.value).toBe(false)
    expect(store.error.value).toBeNull()
  })
})
