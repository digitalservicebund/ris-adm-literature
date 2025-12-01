import type { DocumentType } from '../documentType'
import type { AktivzitierungLiterature } from '../AktivzitierungLiterature.ts'

export interface SliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  hauptsachtitel?: string
  dokumentarischerTitel?: string
  hauptsachtitelZusatz?: string
  note: string
  aktivzitierungLiteratures?: AktivzitierungLiterature[]
}

export interface SliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: SliDocumentationUnit
}
