import Reference from './reference'
import { type FieldOfLaw } from './fieldOfLaw'
import ActiveCitation from './activeCitation'
import type ActiveReference from '@/domain/activeReference.ts'
import type NormReference from './normReference'
import type { Normgeber } from './normgeber'
import type { DocumentType } from './documentType'
import type { Fundstelle } from './fundstelle'
import type { Page } from './pagination'

export interface DocumentUnitSearchParams {
  documentNumber: string
  langueberschrift: string
  fundstelle: string
}

export interface PaginatedDocumentUnitListResponse {
  documentationUnitsOverview: DocumentUnitListItem[]
  page: Page
}

export interface DocumentUnitListItem {
  readonly id: string
  readonly documentNumber: string
  zitierdatum?: string
  langueberschrift?: string
  fundstellen: string[]
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
  dokumenttyp?: DocumentType
  dokumenttypZusatz?: string
  activeCitations?: ActiveCitation[]
  activeReferences?: ActiveReference[]
  normReferences?: NormReference[]
  note: string
  normgeberList?: Normgeber[]
}
