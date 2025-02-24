import type { DocumentUnit } from '@/domain/documentUnit.ts'
export default class DocumentUnitResponseDeprecated {
  readonly id: string
  readonly documentNumber: string
  public json: DocumentUnit

  constructor(data: DocumentUnitResponseDeprecated) {
    this.id = data.id
    this.documentNumber = data.documentNumber
    this.json = data.json
  }
}

export interface DocumentUnitResponse {
  id: string
  documentNumber: string
  json: DocumentUnit
}
