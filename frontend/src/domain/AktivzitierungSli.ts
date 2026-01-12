import type { DocumentType } from './documentType'

export interface AktivzitierungSli {
  id: string
  documentNumber?: string
  titel?: string
  veroeffentlichungsJahr?: string
  dokumenttypen?: DocumentType[]
  verfasser?: string[]
}
