import { defineStore } from 'pinia'
import { defineDocumentUnitStore } from './documentUnitStoreFactory'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'
import { DocumentCategory } from '@/domain/documentType'

export const useUliDocumentUnitStore = defineStore('uliDocumentUnit', () => {
  return defineDocumentUnitStore<UliDocumentationUnit>(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG)
})
