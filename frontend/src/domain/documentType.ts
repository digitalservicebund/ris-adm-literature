export enum DocumentCategory {
  VERWALTUNGSVORSCHRIFTEN = 'VERWALTUNGSVORSCHRIFTEN',
  LITERATUR_UNSELBSTAENDIG = 'LITERATUR_UNSELBSTAENDIG',
  LITERATUR_SELBSTAENDIG = 'LITERATUR_SELBSTAENDIG',
}

export type DocumentType = {
  uuid?: string
  abbreviation: string
  name: string
}
