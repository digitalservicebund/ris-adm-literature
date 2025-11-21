import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import {
  useGetSliDocUnit,
  usePutSliDocUnit,
  usePutPublishSliDocUnit,
} from '@/services/documentUnitService'
import { missingSliDocumentUnitFields } from '@/utils/validators'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'

export const useSliDocumentUnitStore = defineStore('sliDocumentUnit', () => {
  return defineDocumentUnitStore<SliDocumentationUnit>({
    getDocument: useGetSliDocUnit,
    putDocument: usePutSliDocUnit,
    publishDocument: usePutPublishSliDocUnit,
    missingFields: missingSliDocumentUnitFields,
  })
})
