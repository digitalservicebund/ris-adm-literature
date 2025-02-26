import dayjs from 'dayjs'
import type { Court, DocumentType } from './documentUnit'

export default class RelatedDocumentation {
  public uuid?: string
  public newEntry: boolean
  public documentNumber?: string
  public court?: Court
  public decisionDate?: string
  public fileNumber?: string
  public documentType?: DocumentType

  get hasForeignSource(): boolean {
    return this.documentNumber != null
  }

  constructor(data: Partial<RelatedDocumentation> = {}) {
    Object.assign(this, data)
    this.newEntry = data.uuid == undefined
  }

  public isLinkedWith<Type extends RelatedDocumentation>(
    localDecisions: Type[] | undefined,
  ): boolean {
    if (!localDecisions) return false

    return localDecisions.some(
      (localDecision) => localDecision.documentNumber == this.documentNumber,
    )
  }

  get renderSummary(): string {
    return [
      ...(this.court ? [`${this.court?.label}`] : []),
      ...(this.decisionDate ? [dayjs(this.decisionDate).format('DD.MM.YYYY')] : []),
      ...(this.fileNumber ? [this.fileNumber] : []),
      ...(this.documentType?.label ? [this.documentType.label] : []),
    ].join(', ')
  }
}
