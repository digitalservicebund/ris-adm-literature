export interface Periodikum {
  readonly id: string
  title?: string
  subtitle?: string
  abbreviation?: string
}

export interface Fundstelle {
  readonly id: string
  zitatstelle: string
  periodika: Periodikum[]
}
