import type { VerweisTyp } from "@/domain/verweisTyp";
import { useApiFetch } from "@/services/apiService";
import type { UseFetchReturn } from "@vueuse/core";

const REFERENCE_TYPE_URL = "/lookup-tables/verweis-typen";

export function useFetchVerweisTypen(): UseFetchReturn<{
  verweisTypen: VerweisTyp[];
}> {
  return useApiFetch(`${REFERENCE_TYPE_URL}?usePagination=false`, {}).json();
}
