import { type AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'

export interface DocumentUnitResponse {
  id: string
  documentNumber: string
  json: AdmDocumentationUnit
}
