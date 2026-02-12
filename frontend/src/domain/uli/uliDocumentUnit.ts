import type { DocumentType } from "../documentType";
import type { Page } from "../pagination";

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

export interface UliDocUnitListItem {
  readonly id: string;
  readonly documentNumber: string;
  titel?: string;
  fundstellen?: string[];
  dokumenttypen?: string[];
  verfasser?: string[];
}

export interface PaginatedUliDocUnitListResponse {
  documentationUnitsOverview: UliDocUnitListItem[];
  page: Page;
}

export interface UliDocUnitSearchParams {
  documentNumber?: string;
  periodikum?: string;
  zitatstelle?: string;
  dokumenttypen?: DocumentType[];
  verfasser?: string[];
}
