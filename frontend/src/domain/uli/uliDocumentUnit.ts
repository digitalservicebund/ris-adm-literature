import type { DocumentType } from '../documentType'

export interface UliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumentTyp?: DocumentType[]
  note: string
}

export interface UliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: UliDocumentationUnit
}

export const requiredUliDocUnitFields = ['veroeffentlichungsjahr'] as const
