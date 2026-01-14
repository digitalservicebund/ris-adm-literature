import { describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { useFetchDocumentTypes } from "./documentTypeService";
import { DocumentCategory } from "@/domain/documentType";

vi.mock("@/services/apiService", () => {
  return {
    useApiFetch: vi.fn().mockReturnValue({
      json: () => ({
        data: ref({
          documentTypes: [
            {
              abbreviation: "ÄN",
              name: "Änderungsnorm",
            },
            {
              abbreviation: "ÜN",
              name: "Übergangsnorm",
            },
          ],
        }),
      }),
    }),
  };
});

describe("document types service", () => {
  it("calls useFetch with the correct URL and returns doctypes", async () => {
    const { data } = await useFetchDocumentTypes(DocumentCategory.LITERATUR_UNSELBSTAENDIG);

    expect(data.value?.documentTypes).toHaveLength(2);
    expect(data.value?.documentTypes[0]?.abbreviation).toBe("ÄN");
  });
});
