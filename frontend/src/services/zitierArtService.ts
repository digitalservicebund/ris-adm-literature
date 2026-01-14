import { useApiFetch } from "@/services/apiService";
import type { UseFetchReturn } from "@vueuse/core";
import type { ZitierArt } from "@/domain/zitierArt.ts";
import type { DocumentCategory } from "@/domain/documentType.ts";
import { computed } from "vue";
import { buildUrlWithParams } from "@/utils/urlHelpers.ts";

const ZITIER_ARTEN_URL = "/lookup-tables/zitier-arten";

export function useFetchZitierArten(
  sourceDocumentCategory: DocumentCategory,
  targetDocumentCategory: DocumentCategory,
): UseFetchReturn<{
  zitierArten: ZitierArt[];
}> {
  const urlWithParams = computed(() =>
    buildUrlWithParams(`${ZITIER_ARTEN_URL}`, {
      usePagination: false,
      sourceDocumentCategory,
      targetDocumentCategory,
    }),
  );
  return useApiFetch(urlWithParams).json();
}
