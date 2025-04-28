export enum OrganType {
  Institution = 'Institution',
  LegalEntity = 'LegalEntity',
}

export interface Organ {
  id: string
  label: string
  type: OrganType
}

export interface NormgeberRegion {
  id: string
  label: string
}

export interface Normgeber {
  id: string
  organ?: Organ
  region?: NormgeberRegion
}

export function createEmptyNormgeber(): Normgeber {
  return {
    id: crypto.randomUUID(),
    organ: undefined,
    region: undefined,
  }
}
