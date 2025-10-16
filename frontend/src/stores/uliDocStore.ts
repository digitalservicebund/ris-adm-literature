import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'
import { useGetUliDocUnit, usePutUliDocUnit } from '@/services/documentUnitService'

export const useUliDocumentUnitStore = defineStore('uliDocumentUnit', () => {
  return defineDocumentUnitStore<UliDocumentationUnit>(useGetUliDocUnit, usePutUliDocUnit)
})
