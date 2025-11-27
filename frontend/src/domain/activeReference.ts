import NormReference from '@/domain/normReference.ts'
import type EditableListItem from '@/domain/editableListItem.ts'
import type { VerweisTyp } from '@/domain/verweisTyp.ts'

export enum ActiveReferenceDocumentType {
  NORM = 'norm',
  ADMINISTRATIVE_REGULATION = 'administrative_regulation',
}

export default class ActiveReference extends NormReference {
  /**
   * Reference document type is either NORM or ADMINISTRATIVE_REGULATION
   */
  public referenceDocumentType: ActiveReferenceDocumentType = ActiveReferenceDocumentType.NORM
  public verweisTyp?: VerweisTyp

  constructor(data: Partial<ActiveReference> = {}) {
    super(data)
    Object.assign(this, data)
  }

  equals(entry: EditableListItem): boolean {
    const activeReferenceEntry = entry as ActiveReference
    if (activeReferenceEntry.isEmpty) return true

    return (
      super.equals(entry as NormReference) &&
      activeReferenceEntry.referenceDocumentType == this.referenceDocumentType
    )
  }

  get renderVerweisTyp(): string {
    if (this.verweisTyp) {
      return this.verweisTyp.name ?? ''
    }
    return ''
  }
}
