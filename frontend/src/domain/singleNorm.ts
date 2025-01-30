import dayjs from 'dayjs'
import { capitalize } from 'vue'
import LegalForce from './legalForce'

export default class SingleNorm {
  public singleNorm?: string
  public dateOfVersion?: string
  public dateOfRelevance?: string
  public legalForce?: LegalForce
  public id?: string

  static readonly fields = ['singleNorm', 'dateOfVersion', 'dateOfRelevance', 'legalForce'] as const

  constructor(data: Partial<SingleNorm> = {}) {
    Object.assign(this, data)
  }

  get renderSummary(): string {
    return [
      ...(this.singleNorm ? [this.singleNorm] : []),
      ...(this.dateOfVersion ? [dayjs(this.dateOfVersion).format('DD.MM.YYYY')] : []),
      ...(this.dateOfRelevance ? [this.dateOfRelevance] : []),
    ].join(', ')
  }

  get renderLegalForce(): string {
    return [
      ...(this.legalForce?.type ? [capitalize(this.legalForce.type.abbreviation)] : []),
      ...(this.legalForce?.region ? [`(${this.legalForce.region.longText})`] : []),
    ].join(' ')
  }

  get isEmpty(): boolean {
    let isEmpty = true

    SingleNorm.fields.map((key) => {
      if (!this.fieldIsEmpty(this[key])) {
        isEmpty = false
      }
    })
    return isEmpty
  }

  private fieldIsEmpty(value: SingleNorm[(typeof SingleNorm.fields)[number]]) {
    return value === undefined || !value || Object.keys(value).length === 0
  }
}

export type SingleNormValidationInfo = {
  singleNorm: string
  normAbbreviation?: string
}
