import type { Court } from './documentUnit'

export interface AuthorityRegion {
  label: string
}

export interface NormSettingAuthority {
  id: string
  court?: Court
  region?: AuthorityRegion
}

export function createEmptyNormSettingAuthority(): NormSettingAuthority {
  return {
    id: crypto.randomUUID(),
    court: undefined,
    region: undefined,
  }
}
