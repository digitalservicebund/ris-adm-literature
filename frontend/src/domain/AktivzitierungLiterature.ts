import type { DocumentType } from './documentType'

export interface AktivzitierungLiterature {
  id: string
  uuid?: string
  hauptsachtitel?: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  verfasser?: string[]
}
