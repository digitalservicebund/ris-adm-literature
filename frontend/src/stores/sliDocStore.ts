import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import {
  useGetSliDocUnit,
  usePutSliDocUnit,
  usePutPublishUliDocUnit,
} from '@/services/documentUnitService'
import { missingSliDocumentUnitFields } from '@/utils/validators'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'

export const useSliDocumentUnitStore = defineStore('sliDocumentUnit', () => {
  return defineDocumentUnitStore<SliDocumentationUnit>({
    getDocument: useGetSliDocUnit,
    putDocument: usePutSliDocUnit,
    // Replace with usePutPublishSliDocUnit once SLI publishing is implemented
    // Required by defineDocumentUnitStore factory, currently using ULI publish function as placeholder
    publishDocument: usePutPublishUliDocUnit,
    missingFields: missingSliDocumentUnitFields,
  })
})
