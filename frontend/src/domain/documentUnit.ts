import type DocumentationOffice from './documentationOffice'

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

export default class DocumentUnit {
  readonly uuid: string
  readonly documentNumber: string = ''

  constructor(uuid: string, data: Partial<DocumentUnit> = {}) {
    this.uuid = String(uuid)
    Object.assign(this, data)
  }
}
