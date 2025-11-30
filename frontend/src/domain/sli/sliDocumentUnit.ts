import type { DocumentType } from '../documentType'
import type ActiveReferenceLiterature from '../activeReferenceLiterature'

export interface SliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  hauptsachtitel?: string
  dokumentarischerTitel?: string
  hauptsachtitelZusatz?: string
  note: string
  activeReferenceLiteratures?: ActiveReferenceLiterature[]
}

export interface SliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: SliDocumentationUnit
}
