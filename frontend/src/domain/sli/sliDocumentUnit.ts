export interface SliDocumentationUnit {
  readonly id: string
  readonly documentNumber: string
  veroeffentlichungsjahr?: string
  hauptsachtitel?: string
  dokumentarischerTitel?: string
  hauptsachtitelZusatz?: string
  note: string
}

export interface SliDocumentUnitResponse {
  id: string
  documentNumber: string
  json: SliDocumentationUnit
}
