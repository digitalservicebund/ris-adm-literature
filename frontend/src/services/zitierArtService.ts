import { useApiFetch } from '@/services/apiService'
import type { UseFetchReturn } from '@vueuse/core'
import type { ZitierArt } from '@/domain/zitierArt.ts'

const ZITIER_ARTEN_URL = '/lookup-tables/zitier-arten'

export function useFetchZitierArten(): UseFetchReturn<{
  zitierArten: ZitierArt[]
}> {
  return useApiFetch(`${ZITIER_ARTEN_URL}?usePagination=false`, {}).json()
}
