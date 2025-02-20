import NormReference from '@/domain/normReference.ts'
import type EditableListItem from '@/domain/editableListItem.ts'

export enum ActiveReferenceType {
  ANWENDUNG = 'anwendung',
  NEUREGELUNG = 'neuregelung',
  RECHTSGRUNDLAGE = 'rechtsgrundlage',
}

export enum ActiveReferenceDocumentType {
  NORM = 'norm',
  ADMINISTRATIVE_REGULATION = 'administrative_regulation',
}

export default class ActiveReference extends NormReference {
  /**
   * Reference document type is either NORM or ADMINISTRATIVE_REGULATION
   */
  public referenceDocumentType: ActiveReferenceDocumentType = ActiveReferenceDocumentType.NORM
  public referenceType?: ActiveReferenceType

  static readonly referenceTypeLabels = new Map<ActiveReferenceType, string>([
    [ActiveReferenceType.ANWENDUNG, 'Anwendung'],
    [ActiveReferenceType.NEUREGELUNG, 'Neuregelung'],
    [ActiveReferenceType.RECHTSGRUNDLAGE, 'Rechtsgrundlage'],
  ])

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

  get renderReferenceType(): string {
    if (this.referenceType) {
      return ActiveReference.referenceTypeLabels.get(this.referenceType) ?? ''
    }
    return ''
  }
}
