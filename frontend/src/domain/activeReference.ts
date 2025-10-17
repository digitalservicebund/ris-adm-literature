import NormReference from '@/domain/normReference.ts'
import type EditableListItem from '@/domain/editableListItem.ts'

export enum VerweisTypEnum {
  ANWENDUNG = 'anwendung',
  NEUREGELUNG = 'neuregelung',
  RECHTSGRUNDLAGE = 'rechtsgrundlage',
}

export enum ActiveReferenceDocumentType {
  NORM = 'norm',
  ADMINISTRATIVE_REGULATION = 'administrative_regulation',
}

export const verweisTypToLabel = {
  [VerweisTypEnum.ANWENDUNG]: 'Anwendung',
  [VerweisTypEnum.NEUREGELUNG]: 'Neuregelung',
  [VerweisTypEnum.RECHTSGRUNDLAGE]: 'Rechtsgrundlage',
}

export default class ActiveReference extends NormReference {
  /**
   * Reference document type is either NORM or ADMINISTRATIVE_REGULATION
   */
  public referenceDocumentType: ActiveReferenceDocumentType = ActiveReferenceDocumentType.NORM
  public verweisTyp?: VerweisTypEnum

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
      return verweisTypToLabel[this.verweisTyp] ?? ''
    }
    return ''
  }
}
