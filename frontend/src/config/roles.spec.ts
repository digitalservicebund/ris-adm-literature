import { describe, it, expect } from 'vitest'
import { USER_ROLES, roleToHomeRouteMap } from './roles'

describe('Role Configuration', () => {
  it('should define the correct role strings', () => {
    expect(USER_ROLES.VWV_USER).toBe('adm_vwv_user')
    expect(USER_ROLES.LIT_BAG_USER).toBe('adm_lit_bag_user')
  })

  it('should map roles to the correct home route names', () => {
    expect(roleToHomeRouteMap[USER_ROLES.VWV_USER]).toBe('StartPageVwv')
    expect(roleToHomeRouteMap[USER_ROLES.LIT_BAG_USER]).toBe('StartPageUli')
  })

  it('should have a home route mapping for every defined user role', () => {
    const definedRoles = Object.values(USER_ROLES)
    const mappedRoles = Object.keys(roleToHomeRouteMap)

    expect(mappedRoles).toEqual(expect.arrayContaining(definedRoles))
    expect(mappedRoles.length).toBe(definedRoles.length)
  })
})
