import { type FieldOfLaw } from './fieldOfLaw'
import ActiveCitation from './activeCitation'
import type ActiveReference from '@/domain/activeReference.ts'
import type NormReference from './normReference'
import type { Normgeber } from './normgeber'
import type { DocumentType } from './documentType'
import type { Page } from './pagination'
import type { Fundstelle } from './fundstelle'
import type { Definition } from './definition'

export interface DocumentUnitSearchParams {
  documentNumber: string
  langueberschrift: string
  fundstellen: string
  zitierdaten: string
}

export interface PaginatedDocumentUnitListResponse {
  documentationUnitsOverview: DocumentUnitListItem[]
  page: Page
}

export interface DocumentUnitListItem {
  readonly id: string
  readonly documentNumber: string
  zitierdaten: string[]
  langueberschrift?: string
  fundstellen: string[]
}

export interface DocumentUnit {
  readonly id: string
  readonly documentNumber: string
  fundstellen?: Fundstelle[]
  fieldsOfLaw?: FieldOfLaw[]
  langueberschrift?: string
  keywords?: string[]
  zitierdaten?: string[]
  inkrafttretedatum?: string
  ausserkrafttretedatum?: string
  gliederung?: string
  kurzreferat?: string
  aktenzeichen?: string[]
  dokumenttyp?: DocumentType
  dokumenttypZusatz?: string
  activeCitations?: ActiveCitation[]
  activeReferences?: ActiveReference[]
  normReferences?: NormReference[]
  note: string
  normgeberList?: Normgeber[]
  berufsbilder?: string[]
  titelAspekte?: string[]
  definitionen?: Definition[]
}

export const requiredDocumentUnitFields = [
  'langueberschrift',
  'inkrafttretedatum',
  'dokumenttyp',
  'normgeberList',
  'zitierdaten',
] as const
