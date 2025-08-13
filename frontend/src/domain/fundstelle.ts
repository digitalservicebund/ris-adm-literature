export interface Periodikum {
  readonly id: string
  abbreviation: string
  title: string
  subtitle?: string
  citationStyle?: string
}

export interface Fundstelle {
  readonly id: string
  zitatstelle: string
  periodikum?: Periodikum
  ambiguousPeriodikum?: string
}
