import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'
import {
  useGetUliDocUnit,
  usePutPublishUliDocUnit,
  usePutUliDocUnit,
} from '@/services/documentUnitService'
import { missingUliDocumentUnitFields } from '@/utils/validators'

export const useUliDocumentUnitStore = defineStore('uliDocumentUnit', () => {
  return defineDocumentUnitStore<UliDocumentationUnit>({
    getDocument: useGetUliDocUnit,
    putDocument: usePutUliDocUnit,
    publishDocument: usePutPublishUliDocUnit,
    missingFields: missingUliDocumentUnitFields,
  })
})
