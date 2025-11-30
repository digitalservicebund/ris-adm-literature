import type EditableListItem from './editableListItem'
import type { DocumentType } from './documentType'

export default class ActiveReferenceLiterature implements EditableListItem {
  public uuid?: string
  public newEntry: boolean
  public hauptsachtitel?: string
  public veroeffentlichungsjahr?: string
  public dokumenttypen?: DocumentType[]
  public verfasser?: string[]

  static readonly fields = [
    'hauptsachtitel',
    'veroeffentlichungsjahr',
    'dokumenttypen',
    'verfasser',
  ] as const

  constructor(data: Partial<ActiveReferenceLiterature> = {}) {
    Object.assign(this, data)
    this.newEntry = data.uuid == undefined
    if (this.uuid == undefined) {
      this.uuid = crypto.randomUUID()
    }
  }

  get renderSummary(): string {
    const parts: string[] = []

    // Year
    if (this.veroeffentlichungsjahr) {
      parts.push(this.veroeffentlichungsjahr)
    }

    // Authors (joined with "/")
    if (this.verfasser && this.verfasser.length > 0) {
      parts.push(this.verfasser.join('/ '))
    }

    // Document types (abbreviations in parentheses)
    if (this.dokumenttypen && this.dokumenttypen.length > 0) {
      const abbreviations = this.dokumenttypen
        .map((dt) => dt.abbreviation)
        .filter((abbr) => abbr)
        .join(', ')
      if (abbreviations) {
        parts.push(`(${abbreviations})`)
      }
    }

    // Title (hauptsachtitel)
    const title = this.hauptsachtitel
    if (title) {
      parts.push(title)
    }

    return parts.join(', ')
  }

  get id(): string | undefined {
    return this.uuid
  }

  get isEmpty(): boolean {
    return ActiveReferenceLiterature.fields.every((field) => this.fieldIsEmpty(this[field]))
  }

  get isReadOnly(): boolean {
    return false
  }

  equals(entry: EditableListItem): boolean {
    return this.id === entry.id
  }

  get showSummaryOnEdit(): boolean {
    return false
  }

  private fieldIsEmpty(
    value: ActiveReferenceLiterature[(typeof ActiveReferenceLiterature.fields)[number]],
  ): boolean {
    if (value === undefined || value === null) return true
    if (typeof value === 'string') return value.trim() === ''
    if (Array.isArray(value)) return value.length === 0
    return false
  }
}

export const activeReferenceLiteratureLabels: { [name: string]: string } = {
  hauptsachtitel: 'Hauptsachtitel / Dokumentarischer Titel',
  veroeffentlichungsjahr: 'Veröffentlichungsjahr',
  dokumenttypen: 'Dokumenttyp',
  verfasser: 'Verfasser/in',
}
