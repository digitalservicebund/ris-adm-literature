import type DocumentationOffice from './documentationOffice'
import Reference from './reference'
import { type FieldOfLaw } from './fieldOfLaw'
import ActiveCitation from './activeCitation'
import type ActiveReference from '@/domain/activeReference.ts'
import type NormReference from './normReference'

export type DocumentType = {
  uuid?: string
  jurisShortcut: string
  label: string
}

export type Court = {
  type?: string
  location?: string
  label: string
  revoked?: string
  responsibleDocOffice?: DocumentationOffice
}

export interface DocumentUnit {
  readonly id: string
  readonly documentNumber: string
  references?: Reference[]
  fieldsOfLaw?: FieldOfLaw[]
  langueberschrift?: string
  keywords?: string[]
  zitierdatum?: string
  inkrafttretedatum?: string
  ausserkrafttretedatum?: string
  gliederung?: string
  kurzreferat?: string
  aktenzeichen?: string[]
  noAktenzeichen?: boolean
  normgeber?: Court
  dokumenttyp?: DocumentType
  dokumenttypZusatz?: string
  activeCitations?: ActiveCitation[]
  activeReferences?: ActiveReference[]
  normReferences?: NormReference[]
  note: string
}
