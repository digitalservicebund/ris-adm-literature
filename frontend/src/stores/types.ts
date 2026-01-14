import { useAdmDocUnitStore } from "./admDocumentUnitStore";
import { useUliDocumentUnitStore } from "./uliDocStore";

// Define the return type of each store
export type AdmDocUnitStore = ReturnType<typeof useAdmDocUnitStore>;
export type UliDocUnitStore = ReturnType<typeof useUliDocumentUnitStore>;

// Union type for all document unit stores
export type DocumentUnitStore = AdmDocUnitStore | UliDocUnitStore;
