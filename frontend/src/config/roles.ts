export const USER_ROLES = {
  ADM_USER: 'adm_user',
  LITERATURE_USER: 'literature_user',
} as const

export const roleToHomeRouteMap: Record<string, string> = {
  [USER_ROLES.ADM_USER]: 'StartPageVwv',
  [USER_ROLES.LITERATURE_USER]: 'StartPageUli',
}
