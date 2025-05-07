import LegalPeriodical from '@/domain/legalPeriodical'
import type EditableListItem from '@/domain/editableListItem.ts'

export default class Reference implements EditableListItem {
  id?: string
  citation?: string
  legalPeriodical?: LegalPeriodical
  legalPeriodicalRawValue?: string
  primaryReference?: boolean

  static readonly requiredFields = ['legalPeriodical', 'citation'] as const

  static readonly requiredFieldsForDocunit = ['legalPeriodical', 'citation'] as const

  static readonly requiredLiteratureFields = ['legalPeriodical', 'citation'] as const

  static readonly fields = ['legalPeriodical', 'citation'] as const

  constructor(data: Partial<Reference> = {}) {
    Object.assign(this, data)

    this.id ??= crypto.randomUUID()
  }

  get renderSummary(): string {
    return [
      this.legalPeriodical?.abbreviation ?? this.legalPeriodicalRawValue,
      this.citation ? `${this.citation}` : '',
    ]
      .filter(Boolean)
      .join(' ')
  }

  get hasMissingRequiredFields(): boolean {
    return this.missingRequiredFields.length > 0
  }

  get missingRequiredFields() {
    return Reference.requiredFields.filter((field) => this.fieldIsEmpty(this[field]))
  }

  get hasMissingRequiredFieldsForDocunit(): boolean {
    return this.missingRequiredFieldsForDocunit.length > 0
  }

  get missingRequiredFieldsForDocunit() {
    return Reference.requiredFieldsForDocunit.filter((field) => this.fieldIsEmpty(this[field]))
  }

  get hasMissingRequiredLiteratureFields(): boolean {
    return this.missingRequiredLiteratureFields.length > 0
  }

  get missingRequiredLiteratureFields() {
    return Reference.requiredLiteratureFields.filter((field) => this.fieldIsEmpty(this[field]))
  }

  equals(entry: Reference): boolean {
    return this.id === entry.id
  }

  get isEmpty(): boolean {
    let isEmpty = true

    Reference.fields.map((key) => {
      if (!this.fieldIsEmpty(this[key])) {
        isEmpty = false
      }
    })
    return isEmpty
  }

  fieldIsEmpty(value: Reference[(typeof Reference.fields)[number]]) {
    return value === undefined || !value || Object.keys(value).length === 0
  }

  get hasForeignSource(): boolean {
    return true
  }
}
