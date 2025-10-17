import { type FieldOfLaw } from '@/domain/fieldOfLaw'
import ActiveCitation from '@/domain/activeCitation'
import type ActiveReference from '@/domain/activeReference.ts'
import type NormReference from '@/domain/normReference'
import type { Normgeber } from '@/domain/normgeber'
import type { DocumentType } from '@/domain/documentType'
import type { Page } from '@/domain/pagination'
import type { Fundstelle } from '@/domain/fundstelle'
import type { Definition } from '@/domain/definition'

export interface AdmDocumentationUnit {
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

export interface AdmDocUnitSearchParams {
  documentNumber: string
  langueberschrift: string
  fundstellen: string
  zitierdaten: string
}

export interface AdmDocUnitListItem {
  readonly id: string
  readonly documentNumber: string
  zitierdaten: string[]
  langueberschrift?: string
  fundstellen: string[]
}

export interface PaginatedAdmDocUnitListResponse {
  documentationUnitsOverview: AdmDocUnitListItem[]
  page: Page
}

export interface AdmDocumentUnitResponse {
  id: string
  documentNumber: string
  json: AdmDocumentationUnit
}

export const requiredAdmDocUnitFields = [
  'langueberschrift',
  'inkrafttretedatum',
  'dokumenttyp',
  'normgeberList',
  'zitierdaten',
] as const
