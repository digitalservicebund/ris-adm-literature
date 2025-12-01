import type { DocumentType } from './documentType'

export interface AktivzitierungLiterature {
  id: string
  uuid?: string
  newEntry: boolean
  hauptsachtitel?: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  verfasser?: string[]
}
