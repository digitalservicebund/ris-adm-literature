import type { DocumentType } from './documentType'

export interface AktivzitierungSli {
  id: string
  uuid?: string
  documentNumber?: string
  titel?: string
  veroeffentlichungsJahr?: string
  dokumenttypen?: DocumentType[]
  verfasser?: string[]
}
