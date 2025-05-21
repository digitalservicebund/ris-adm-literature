import { useFetch } from '@vueuse/core'
import type { InstitutionResponse } from '@/domain/normgeber.ts'
import { API_PREFIX } from '@/services/httpClient.ts'

const INSTITUTION_URL = `${API_PREFIX}lookup-tables/institutions`

export function fetchInstitutions() {
  return useFetch<InstitutionResponse>(`${INSTITUTION_URL}?usePagination=false`, {}).json()
}
