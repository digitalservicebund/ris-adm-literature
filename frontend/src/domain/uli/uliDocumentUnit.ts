import type { DocumentType } from "../documentType";

export interface UliDocumentationUnit {
  readonly id: string;
  readonly documentNumber: string;
  veroeffentlichungsjahr?: string;
  dokumenttypen?: DocumentType[];
  hauptsachtitel?: string;
  hauptsachtitelZusatz?: string;
  dokumentarischerTitel?: string;
  note: string;
}

export interface UliDocumentUnitResponse {
  id: string;
  documentNumber: string;
  json: UliDocumentationUnit;
  administrativeData: { note?: string };
}
