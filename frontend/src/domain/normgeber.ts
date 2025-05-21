export enum InstitutionType {
  Institution = 'INSTITUTION',
  LegalEntity = 'LEGAL_ENTITY',
}

export interface RegionApiResponse {
  id: string
  code: string
  longText?: string
}

export interface InstitutionApiResponse {
  id: string
  name: string
  officialName?: string
  type: InstitutionType
  regions: RegionApiResponse[]
}

export interface Region {
  id: string
  code: string
  longText?: string
}

export interface Institution {
  id: string
  name: string
  officialName?: string
  type: InstitutionType
  regions?: Region[]
}

export interface Normgeber {
  id: string
  institution: Institution
  regions: Region[]
}
