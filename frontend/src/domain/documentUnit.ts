import type DocumentationOffice from './documentationOffice'
import Reference from './reference'

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
  readonly documentNumber: string
  public references?: Reference[]

  constructor(data: DocumentUnit) {
    this.uuid = data.uuid
    this.documentNumber = data.documentNumber
    if (data.references)
      this.references = data.references.map((reference) => new Reference({ ...reference }))
  }
}
