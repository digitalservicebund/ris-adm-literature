import type { Region } from "@/domain/normgeber.ts";
import { useApiFetch } from "@/services/apiService";
import type { UseFetchReturn } from "@vueuse/core";

const REGION_URL = "/lookup-tables/regions";

export function useFetchRegions(): UseFetchReturn<{ regions: Region[] }> {
  return useApiFetch(`${REGION_URL}?usePagination=false`, {}).json();
}
