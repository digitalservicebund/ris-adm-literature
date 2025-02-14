import type DocumentationOffice from './documentationOffice'
import Reference from './reference'
import { type FieldOfLaw } from './fieldOfLaw'

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
  readonly id: string
  readonly documentNumber: string
  public references?: Reference[]
  public fieldsOfLaw: FieldOfLaw[] = []

  constructor(data: DocumentUnit) {
    this.id = data.id
    this.documentNumber = data.documentNumber
    if (data.references)
      this.references = data.references.map((reference) => new Reference({ ...reference }))
    if (data.fieldsOfLaw) this.fieldsOfLaw = data.fieldsOfLaw
  }
}
