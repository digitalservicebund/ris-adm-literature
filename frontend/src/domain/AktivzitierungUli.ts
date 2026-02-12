import type { DocumentType } from "./documentType";

export interface AktivzitierungUli {
  id: string;
  documentNumber?: string;
  titel?: string;
  periodikum?: string;
  zitatstelle?: string;
  verfasser?: string[];
  dokumenttypen?: DocumentType[];
}
