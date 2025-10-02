import { ROUTE_NAMES } from '@/constants/routes'

export const USER_ROLES = {
  VWV_USER: 'adm_vwv_user',
  LIT_BAG_USER: 'adm_lit_bag_user',
} as const

export const roleToHomeRouteMap: Record<string, string> = {
  [USER_ROLES.VWV_USER]: ROUTE_NAMES.VWV.START_PAGE,
  [USER_ROLES.LIT_BAG_USER]: ROUTE_NAMES.ULI.START_PAGE,
}
