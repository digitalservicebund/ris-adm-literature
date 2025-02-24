import type { DocumentUnit } from '@/domain/documentUnit'

export interface DocumentUnitResponse {
  id: string
  documentNumber: string
  json: DocumentUnit
}
