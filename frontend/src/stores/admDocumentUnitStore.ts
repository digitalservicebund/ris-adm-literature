import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import { useGetAdmDocUnit, usePutAdmDocUnit } from '@/services/documentUnitService'

export const useAdmDocUnitStore = defineStore('admDocumentUnit', () => {
  return defineDocumentUnitStore<AdmDocumentationUnit>(useGetAdmDocUnit, usePutAdmDocUnit)
})
