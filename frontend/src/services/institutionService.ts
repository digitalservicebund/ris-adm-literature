import type { Institution } from "@/domain/normgeber.ts";
import { useApiFetch } from "@/services/apiService";
import type { UseFetchReturn } from "@vueuse/core";

const INSTITUTION_URL = `/lookup-tables/institutions`;

export function useFetchInstitutions(): UseFetchReturn<{ institutions: Institution[] }> {
  return useApiFetch(`${INSTITUTION_URL}?usePagination=false`, {}).json();
}
