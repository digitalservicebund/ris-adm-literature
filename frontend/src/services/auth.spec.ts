import * as Keycloak from 'keycloak-js'
import { afterEach, describe, expect, it, vi } from 'vitest'

// Unmock the globally mocked auth module
vi.unmock('@/services/auth')

vi.mock('@/router.ts', () => ({
  default: {
    push: vi.fn(),
  },
}))

vi.mock('keycloak-js', () => {
  const MockKeycloak = vi.fn()
  MockKeycloak.prototype.init = vi.fn().mockResolvedValue(true)
  MockKeycloak.prototype.didInitialize = false
  MockKeycloak.prototype.token = undefined
  MockKeycloak.prototype.idTokenParsed = undefined
  MockKeycloak.prototype.accountManagement = vi.fn().mockResolvedValue(undefined)
  MockKeycloak.prototype.createLogoutUrl = vi.fn().mockReturnValue(undefined)
  MockKeycloak.prototype.updateToken = vi.fn().mockResolvedValue(true)
  MockKeycloak.prototype.logout = vi.fn()
  MockKeycloak.prototype.hasRealmRole = vi.fn().mockReturnValue(false)
  MockKeycloak.prototype.authenticated = undefined
  MockKeycloak.prototype.realmAccess = undefined

  return { default: MockKeycloak }
})

describe('auth', () => {
  afterEach(() => {
    vi.resetAllMocks()
    vi.resetModules()
  })

  it('configures a new instance', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(Keycloak.default).toHaveBeenCalledWith({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(Keycloak.default.prototype.init).toHaveBeenCalled()
  })

  it('replaces an existing instance when configuring again', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { configure } = useAuthentication()

    await configure({
      clientId: 'test-client-1',
      realm: 'test-realm-1',
      url: 'http://test.url/1',
    })

    await configure({
      clientId: 'test-client-2',
      realm: 'test-realm-2',
      url: 'http://test.url/2',
    })

    expect(Keycloak.default).toHaveBeenCalledWith(
      expect.objectContaining({ clientId: 'test-client-1' }),
    )
    expect(Keycloak.default).toHaveBeenCalledWith(
      expect.objectContaining({ clientId: 'test-client-2' }),
    )

    expect(Keycloak.default.prototype.init).toHaveBeenCalledTimes(2)
  })

  it('throws an error when configuration fails', async () => {
    vi.spyOn(Keycloak.default.prototype, 'init').mockRejectedValueOnce('Error')
    const { useAuthentication } = await import('./auth.ts')
    const { configure } = useAuthentication()

    await expect(() =>
      configure({
        clientId: 'test-client',
        realm: 'test-realm',
        url: 'http://test.url',
      }),
    ).rejects.toThrow(expect.objectContaining({ cause: 'Error' }))
  })

  it('adds an authorization header when a token is available', async () => {
    vi.spyOn(Keycloak.default.prototype, 'token', 'get').mockReturnValue('1234')
    const { useAuthentication } = await import('./auth.ts')
    const { configure, addAuthorizationHeader } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const headers = addAuthorizationHeader()

    // @ts-expect-error TypeScript is not sure it's there, but that's what we're testing
    expect(headers.Authorization).toBe('Bearer 1234')
  })

  it("doesn't add an authorization header when no token is available", async () => {
    vi.spyOn(Keycloak.default.prototype, 'token', 'get').mockReturnValue(undefined)
    const { useAuthentication } = await import('./auth.ts')
    const { addAuthorizationHeader } = useAuthentication()

    const headers = addAuthorizationHeader()

    // @ts-expect-error TypeScript is not sure it's there, but that's what we're testing
    expect(headers.Authorization).toBeFalsy()
  })

  it('includes the original headers when adding an authorization header', async () => {
    vi.spyOn(Keycloak.default.prototype, 'token', 'get').mockReturnValue('1234')
    const { useAuthentication } = await import('./auth.ts')
    const { configure, addAuthorizationHeader } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const headers = addAuthorizationHeader({ test: 'true' })

    // @ts-expect-error TypeScript is not sure it's there, but that's what we're testing
    expect(headers.test).toBe('true')
  })

  it('returns the username', async () => {
    vi.spyOn(Keycloak.default.prototype, 'idTokenParsed', 'get').mockReturnValue({
      name: 'Jane Doe',
    })
    const { useAuthentication } = await import('./auth.ts')
    const { configure, getUsername } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const username = getUsername()

    expect(username).toBe('Jane Doe')
  })

  it('returns undefined as the username if no token exists', async () => {
    vi.spyOn(Keycloak.default.prototype, 'idTokenParsed', 'get').mockReturnValue(undefined)
    const { useAuthentication } = await import('./auth.ts')
    const { configure, getUsername } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const username = getUsername()

    expect(username).toBeUndefined()
  })

  it('returns false when attempting to refresh a token before configuring auth', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh } = useAuthentication()

    const result = await tryRefresh()

    expect(result).toBe(false)
  })

  it('returns false when the token refresh throws', async () => {
    vi.spyOn(Keycloak.default.prototype, 'updateToken').mockImplementation(function () {
      throw new Error()
    })

    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh, configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const result = await tryRefresh()

    expect(result).toBe(false)
  })

  it('returns true when the token refresh succeeds', async () => {
    vi.spyOn(Keycloak.default.prototype, 'updateToken').mockResolvedValue(true)
    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh, configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const result = await tryRefresh()

    expect(result).toBe(true)
  })

  it("returns true when the token doesn't need refreshing", async () => {
    vi.spyOn(Keycloak.default.prototype, 'updateToken').mockResolvedValue(false)
    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh, configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const result = await tryRefresh()

    expect(result).toBe(true)
  })

  it("doesn't run multiple token refresh requests at the same time", async () => {
    vi.spyOn(Keycloak.default.prototype, 'updateToken').mockResolvedValue(true)

    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh, configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const result1 = tryRefresh()
    const result2 = tryRefresh()

    await expect(result1).resolves.toBe(true)
    await expect(result2).resolves.toBe(true)
    expect(Keycloak.default.prototype.updateToken).toHaveBeenCalledTimes(1)
  })

  it('runs multiple token refresh requests sequentially', async () => {
    vi.spyOn(Keycloak.default.prototype, 'updateToken').mockResolvedValue(true)

    const { useAuthentication } = await import('./auth.ts')
    const { tryRefresh, configure } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    const result1 = tryRefresh()
    await expect(result1).resolves.toBe(true)
    expect(Keycloak.default.prototype.updateToken).toHaveBeenCalledTimes(1)

    const result2 = tryRefresh()
    await expect(result2).resolves.toBe(true)
    expect(Keycloak.default.prototype.updateToken).toHaveBeenCalledTimes(2)
  })

  it('logs out with the correct redirect URI', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { configure, logout } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    logout()

    expect(Keycloak.default.prototype.logout).toHaveBeenCalledTimes(1)
    expect(Keycloak.default.prototype.logout).toHaveBeenCalledWith({
      redirectUri: window.location.origin,
    })
  })

  it('returns true when the user is authenticated', async () => {
    vi.spyOn(Keycloak.default.prototype, 'authenticated', 'get').mockReturnValue(true)
    const { useAuthentication } = await import('./auth.ts')
    const { configure, isAuthenticated } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(isAuthenticated()).toBe(true)
  })

  it('returns false when the user is not authenticated', async () => {
    vi.spyOn(Keycloak.default.prototype, 'authenticated', 'get').mockReturnValue(false)
    const { useAuthentication } = await import('./auth.ts')
    const { configure, isAuthenticated } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(isAuthenticated()).toBe(false)
  })

  it('returns false for isAuthenticated when not configured', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { isAuthenticated } = useAuthentication()
    expect(isAuthenticated()).toBe(false)
  })

  it('returns true if the user has the specified realm role', async () => {
    vi.spyOn(Keycloak.default.prototype, 'hasRealmRole').mockImplementation(function (
      role: string,
    ) {
      return role === 'admin'
    })
    const { useAuthentication } = await import('./auth.ts')
    const { configure, hasRealmRole } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(hasRealmRole('admin')).toBe(true)
    expect(hasRealmRole('user')).toBe(false)
  })

  it('returns false for hasRealmRole when not configured', async () => {
    const { useAuthentication } = await import('./auth.ts')
    const { hasRealmRole } = useAuthentication()
    expect(hasRealmRole('any-role')).toBe(false)
  })

  it('returns a list of realm roles', async () => {
    vi.spyOn(Keycloak.default.prototype, 'realmAccess', 'get').mockReturnValue({
      roles: ['admin', 'user'],
    })
    const { useAuthentication } = await import('./auth.ts')
    const { configure, getRealmRoles } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(getRealmRoles()).toEqual(['admin', 'user'])
  })

  it('returns an empty array for getRealmRoles when not configured or no roles exist', async () => {
    const { useAuthentication: useAuth1 } = await import('./auth.ts')
    const { getRealmRoles: getRealmRolesUnconfigured } = useAuth1()
    expect(getRealmRolesUnconfigured()).toEqual([])

    vi.spyOn(Keycloak.default.prototype, 'realmAccess', 'get').mockReturnValue(undefined)
    const { useAuthentication: useAuth2 } = await import('./auth.ts')
    const { configure, getRealmRoles } = useAuth2()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })
    expect(getRealmRoles()).toEqual([])
  })

  it('returns the first group name from the token', async () => {
    vi.spyOn(Keycloak.default.prototype, 'idTokenParsed', 'get').mockReturnValue({
      groups: ['/literature/BAG', '/other/GROUP'],
    })

    const { useAuthentication } = await import('./auth.ts')
    const { configure, getGroup } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(getGroup()).toBe('BAG')
  })

  it('returns an empty string if no groups exist', async () => {
    vi.spyOn(Keycloak.default.prototype, 'idTokenParsed', 'get').mockReturnValue({
      groups: [],
    })

    const { useAuthentication } = await import('./auth.ts')
    const { configure, getGroup } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(getGroup()).toBe('')
  })

  it('returns an empty string if idTokenParsed is undefined', async () => {
    vi.spyOn(Keycloak.default.prototype, 'idTokenParsed', 'get').mockReturnValue(undefined)

    const { useAuthentication } = await import('./auth.ts')
    const { configure, getGroup } = useAuthentication()

    await configure({
      clientId: 'test-client',
      realm: 'test-realm',
      url: 'http://test.url',
    })

    expect(getGroup()).toBe('')
  })
})
