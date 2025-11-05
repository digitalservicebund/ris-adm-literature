import type { DocumentType } from '../documentType'

export interface UliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumentTyp?: DocumentType[]
  hauptsachtitel?: string
  hauptsachtitelZusatz?: string
  dokumentarischerTitel?: string
  note: string
}

export interface UliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: UliDocumentationUnit
}

export const requiredUliDocUnitFields = ['veroeffentlichungsjahr'] as const
