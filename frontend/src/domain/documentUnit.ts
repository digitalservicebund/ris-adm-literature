import type DocumentationOffice from './documentationOffice'
import Reference from './reference'
import { type FieldOfLaw } from './fieldOfLaw'

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
  id: string
  documentNumber: string
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
  normgeber?: boolean
  dokumenttyp?: string
  dokumenttypZusatz?: string
}
