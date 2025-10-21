import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import { DocumentTypeCode } from '@/domain/documentType'

export const useAdmDocUnitStore = defineStore('admDocumentUnit', () => {
  return defineDocumentUnitStore<AdmDocumentationUnit>(DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN)
})
