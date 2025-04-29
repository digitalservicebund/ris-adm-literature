export enum InstitutionType {
  Institution = 'INSTITUTION',
  LegalEntity = 'LEGAL_ENTITY',
}

export interface RegionApiResponse {
  code: string
  longText?: string
}

export interface Region {
  label: string
  longText?: string
}

export interface InstitutionApiResponse {
  name: string
  officialName?: string
  type: InstitutionType
  regions: RegionApiResponse[]
}

export interface Institution {
  label: string
  officialName?: string
  type: InstitutionType
  regions?: Region[]
}

export interface Normgeber {
  institution?: Institution
  regions?: Region[]
}

export function createEmptyNormgeber(): Normgeber {
  return {
    institution: undefined,
    regions: undefined,
  }
}
