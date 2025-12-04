import type { DocumentType } from './documentType'

export interface AktivzitierungLiterature {
  id: string
  uuid?: string
  documentNumber?: string
  titel?: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  verfasser?: string[]
}
