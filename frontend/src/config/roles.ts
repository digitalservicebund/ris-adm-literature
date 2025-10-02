import { ROUTE_NAMES } from '@/constants/routes'

export const USER_ROLES = {
  ADM_USER: 'adm_user',
  LITERATURE_USER: 'literature_user',
} as const

export const roleToHomeRouteMap: Record<string, string> = {
  [USER_ROLES.ADM_USER]: ROUTE_NAMES.VWV.START_PAGE,
  [USER_ROLES.LITERATURE_USER]: ROUTE_NAMES.ULI.START_PAGE,
}
