export enum OrganType {
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
  type: OrganType
  regions: RegionApiResponse[]
}

export interface Institution {
  label: string
  officialName?: string
  type: OrganType
  regions?: Region[]
}

export interface Normgeber {
  institution?: Institution
  region?: Region
}

export function createEmptyNormgeber(): Normgeber {
  return {
    institution: undefined,
    region: undefined,
  }
}
