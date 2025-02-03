import type EditableListItem from './editableListItem'
import { type NormAbbreviation } from './normAbbreviation'
import SingleNorm from './singleNorm'

export default class NormReference implements EditableListItem {
  public normAbbreviation?: NormAbbreviation
  public singleNorms?: SingleNorm[]
  public normAbbreviationRawValue?: string

  static readonly fields = ['normAbbreviation', 'normAbbreviationRawValue'] as const

  constructor(data: Partial<NormReference> = {}) {
    Object.assign(this, data)
  }

  get id() {
    return this.normAbbreviation ? this.normAbbreviation.id : this.normAbbreviationRawValue
  }

  get hasAmbiguousNormReference(): boolean {
    return !this.normAbbreviation && !!this.normAbbreviationRawValue
  }

  equals(entry: NormReference): boolean {
    if (entry.isEmpty) return true
    let isEquals = false
    if (this.normAbbreviation) {
      isEquals = entry.normAbbreviation
        ? this.normAbbreviation?.abbreviation == entry.normAbbreviation.abbreviation
        : false
    } else if (this.normAbbreviationRawValue) {
      isEquals = this.normAbbreviationRawValue == entry.normAbbreviationRawValue
    }
    return isEquals
  }

  get renderSummary(): string {
    let result: string[]
    if (this.normAbbreviation?.abbreviation) {
      result = [`${this.normAbbreviation?.abbreviation}`]
    } else if (this.normAbbreviationRawValue) {
      result = [`${this.normAbbreviationRawValue}`]
    } else {
      result = []
    }
    return [...result].join(', ')
  }

  get isEmpty(): boolean {
    let isEmpty = true

    NormReference.fields.map((key) => {
      if (!this.fieldIsEmpty(this[key])) {
        isEmpty = false
      }
    })
    return isEmpty
  }

  private fieldIsEmpty(value: NormReference[(typeof NormReference.fields)[number]]) {
    return value === undefined || !value || Object.keys(value).length === 0
  }
}
