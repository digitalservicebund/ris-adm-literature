import { computed, ref } from "vue";
import type { UseFetchReturn } from "@vueuse/core";

/**
 * Generic factory function for creating a document-unit store.
 *
 * This abstracts common CRUD logic (load, update, publish, unload) so it can be reused
 * for different document types (e.g. ADM, ULI, SLI).
 *
 * @template DocumentationUnit - The document model (e.g. AdmDocumentationUnit).
 *
 * @param options - An object containing:
 *   - `getDocument`: Function to fetch a document by number.
 *   - `putDocument`: Function to update a document.
 *   - `publishDocument`: Function to publish a document.
 *   - `missingFields`: Function to check for missing fields in the document.
 *
 * @returns An object containing reactive state and CRUD operations:
 *  - `documentUnit`: the currently loaded document (or `null`)
 *  - `isLoading`: boolean flag indicating loading state
 *  - `error`: holds any error that occurred during load or update
 *  - `load(documentNumber)`: loads a document by number
 *  - `update()`: updates the current document and returns `true`/`false`
 *  - `publish()`: publish the current document and returns `true`/`false`
 *  - `unload()`: clears the loaded document from memory
 *  - `missingRequiredFields`: string[], a list of missing required fields in the document
 */
export function defineDocumentUnitStore<DocumentationUnit>({
  getDocument,
  putDocument,
  publishDocument,
  missingFields,
}: {
  getDocument: (documentNumber: string) => UseFetchReturn<DocumentationUnit>;
  putDocument: (doc: DocumentationUnit) => UseFetchReturn<DocumentationUnit>;
  publishDocument: (doc: DocumentationUnit) => UseFetchReturn<DocumentationUnit>;
  missingFields: (doc: DocumentationUnit) => string[];
}) {
  const documentUnit = ref<DocumentationUnit | null>(null);
  const isLoading = ref(false);
  const error = ref<Error | null>(null);

  async function load(documentNumber: string): Promise<void> {
    isLoading.value = true;
    error.value = null;

    const { data, error: fetchError, execute } = getDocument(documentNumber);
    await execute();

    if (fetchError.value) {
      error.value = fetchError.value;
      documentUnit.value = null;
    } else {
      documentUnit.value = data.value ?? null;
    }

    isLoading.value = false;
  }

  async function update(): Promise<boolean> {
    if (!documentUnit.value) return false;

    isLoading.value = true;
    error.value = null;

    const { data, error: putError, statusCode, execute } = putDocument(documentUnit.value);
    await execute();

    if (statusCode.value && statusCode.value >= 200 && statusCode.value < 300 && data.value) {
      documentUnit.value = data.value;
      isLoading.value = false;
      return true;
    }

    error.value = putError.value || new Error("Update failed");
    isLoading.value = false;
    return false;
  }

  async function publish(): Promise<boolean> {
    if (!documentUnit.value) return false;

    isLoading.value = true;
    error.value = null;

    const { error: putError, statusCode, execute } = publishDocument(documentUnit.value);
    await execute();

    if (statusCode.value && statusCode.value >= 200 && statusCode.value < 300) {
      isLoading.value = false;
      return true;
    }

    error.value = putError.value || new Error("Publish failed");
    isLoading.value = false;
    return false;
  }

  function unload() {
    documentUnit.value = null;
  }

  const missingRequiredFields = computed<string[]>(() => {
    const docUnit = documentUnit.value;
    if (!docUnit) return [];

    return missingFields(docUnit);
  });

  return {
    documentUnit,
    isLoading,
    error,
    load,
    update,
    publish,
    unload,
    missingRequiredFields,
  };
}
