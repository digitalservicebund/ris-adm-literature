import type { AdmDocumentationUnit, AdmDocUnitSearchParams } from '@/domain/adm/admDocumentUnit'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'
import type { SliDocUnitSearchParams } from '@/domain/sli/sliDocumentUnit'

export type DocumentationUnit = AdmDocumentationUnit | UliDocumentationUnit

export type AktivzitierungSearchParams = AdmDocUnitSearchParams | SliDocUnitSearchParams
