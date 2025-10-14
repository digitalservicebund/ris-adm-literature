import type { KeycloakConfig } from 'keycloak-js'
import Keycloak from 'keycloak-js'

export type AuthenticationConfig = KeycloakConfig

function createAuthentication() {
  let keycloak: Keycloak | undefined = undefined

  /**
   * Initializes the authentication with the specified configuration. Once
   * configured, login page redirects, token refresh, etc. will automatically
   * be managed, and you can use `addAuthorizationHeader` for authenticating
   * backend requests.
   *
   * Calling this function repeatedly will replace any existing instance with
   * the new configuration.
   *
   * @param config Configuration for authentication
   * @throws Error if the authentication fails to initialize, e.g. due to an
   *  invalid configuration.
   */
  async function configure(config: AuthenticationConfig): Promise<void> {
    keycloak = new Keycloak(config)

    try {
      await keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: false,
        pkceMethod: 'S256',
        scope: 'profile email',
      })
    } catch (e) {
      keycloak = undefined
      throw new Error('Failed to initialize authentication', { cause: e })
    }
  }

  /**
   * Checks if the authenticated user has a specific realm role.
   *
   * @param role The name of the role to check.
   * @returns True if the user has the role, otherwise false.
   */
  function hasRealmRole(role: string): boolean {
    return keycloak?.hasRealmRole(role) ?? false
  }

  /**
   * Returns the realm roles of the currently active user.
   *
   * @returns An array of role strings, or an empty array if not available.
   */
  function getRealmRoles(): string[] {
    return keycloak?.realmAccess?.roles ?? []
  }

  /**
   * Returns the group of the currently active user.
   *
   * @returns A string, or an empty string if not available.
   */
  function getGroup(): string {
    const groups = keycloak?.idTokenParsed?.groups || []
    const groupNames = groups.map((path: string) => path.split('/').pop())
    return groupNames[0] ?? ''
  }

  /**
   * Checks if the Keycloak instance has been initialized and the user is authenticated.
   * @returns True if authenticated, otherwise false.
   */
  function isAuthenticated(): boolean {
    return keycloak?.authenticated ?? false
  }

  /**
   * Adds an Authorization header with the current token to a set of headers.
   * If authentication hasn't been configured yet, this method will do nothing.
   *
   * @param init If you already have some headers, provide them as `init` and
   *  this method will add the Authorization header to the list.
   * @returns New headers
   */
  function addAuthorizationHeader(init?: HeadersInit): HeadersInit {
    init ??= {}
    if (!keycloak?.token) return init
    return { ...init, Authorization: `Bearer ${keycloak?.token}` }
  }

  /**
   * Returns the name of the currently active user, if they have a name and
   * authentication has been configured. Otherwise `undefined` is returned.
   *
   * @returns Name of the logged in user if known
   */
  function getUsername(): string | undefined {
    return keycloak?.idTokenParsed?.name
  }

  function logout(): Promise<void> | undefined {
    const redirectUri = globalThis.location.origin

    return keycloak?.logout({ redirectUri: redirectUri })
  }

  let pendingRefresh: Promise<boolean> | null = null

  /**
   * Attempts to refresh the token. If the refresh fails (e.g. because the user
   * has logged out in a different tab), the user is automatically redirected to
   * the login page.
   *
   * Repeated calls to refresh the token will automatically be de-duplicated,
   * so consumers of this method don't need to worry about triggering multiple
   * token refreshes at the same time.
   *
   * @returns A boolean indicating whether a valid token exists after the
   *  attempt. This will be true if the current token can still be used,
   *  or if it has been refreshed successfully. It will be false if keycloak
   *  hasn't been initialized, or the refresh has failed.
   */
  async function tryRefresh(): Promise<boolean> {
    if (!keycloak) return false

    try {
      pendingRefresh ??= keycloak.updateToken()
      await pendingRefresh
      return true
    } catch {
      return false
    } finally {
      pendingRefresh = null
    }
  }

  return () => ({
    addAuthorizationHeader,
    configure,
    logout,
    getUsername,
    tryRefresh,
    hasRealmRole,
    isAuthenticated,
    getRealmRoles,
    getGroup,
  })
}

/**
 * Exposes utilities related to authenticating users and requests in the
 * frontend.
 */
export const useAuthentication = createAuthentication()
