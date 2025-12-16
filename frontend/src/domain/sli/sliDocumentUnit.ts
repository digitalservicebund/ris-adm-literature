import type { DocumentType } from '@/domain/documentType'
import type { AktivzitierungSli } from '../AktivzitierungSli.ts'
import type { Page } from '@/domain/pagination'
import type { AktivzitierungAdm } from '../AktivzitierungAdm.ts'

export interface SliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: DocumentType[]
  hauptsachtitel?: string
  dokumentarischerTitel?: string
  hauptsachtitelZusatz?: string
  note: string
  aktivzitierungenSli?: AktivzitierungSli[]
  aktivzitierungenAdm?: AktivzitierungAdm[]
}

export interface SliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: SliDocumentationUnit
  note?: string
}

export interface SliDocUnitListItem {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  dokumenttypen?: string[]
  titel?: string
  verfasser?: string[]
}

export interface PaginatedSliDocUnitListResponse {
  documentationUnitsOverview: SliDocUnitListItem[]
  page: Page
}

export interface SliDocUnitSearchParams {
  veroeffentlichungsJahr?: string
  titel?: string
  dokumenttypen?: string[]
  verfasser?: string[]
}
