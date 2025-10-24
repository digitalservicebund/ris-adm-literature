import { test, expect, Page } from '@playwright/test'

async function login(page: Page, username: string, password = 'test') {
  await page.waitForURL('**/localhost:8443/realms/ris/**')

  const usernameInput = page.getByRole('textbox', { name: 'Username or email' })
  await expect(usernameInput).toBeVisible()

  await usernameInput.fill(username)
  await page.getByRole('textbox', { name: 'Password' }).fill(password)
  await page.getByRole('button', { name: 'Sign In' }).click()
}

/**
 * Test users (based on realm.json):
 * - 'testboth': Has 'adm_user' (VwV) AND 'literature_user' (Lit).
 * - 'test': Has 'adm_user' ONLY (VwV Only).
 * - 'testbag': Has 'literature_user' ONLY (Lit Only).
 * - 'testnoaccess': Has no relevant roles/groups (No Access).
 */

test.slow()

test.describe('Login Flow', () => {
  // --- Tests for user with BOTH permissions ('testboth') ---
  test(
    'User with both permissions is redirected to VwV deep-link after login',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      const protectedVwvUrl =
        '/verwaltungsvorschriften/dokumentationseinheit/KSNR999999999/fundstellen'
      const fullProtectedUrl = new RegExp(protectedVwvUrl)

      await page.goto(protectedVwvUrl)

      await login(page, 'testboth')

      await expect(page.getByRole('heading', { name: 'Fundstellen' })).toBeVisible()
      await expect(page).toHaveURL(fullProtectedUrl)
    },
  )

  test(
    'User with both permissions is redirected to Lit deep-link after login',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      const protectedLitUrl = '/literatur-unselbststaendig/dokumentationseinheit/LIT123456'
      const fullProtectedUrl = new RegExp(protectedLitUrl)

      await page.goto(protectedLitUrl)
      await login(page, 'testboth')
      await expect(page).toHaveURL(fullProtectedUrl)
    },
  )

  test(
    'User with both permissions can switch between apps (Single Sign-On)',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/')
      await login(page, 'testboth')

      await expect(page.getByRole('link', { name: 'Verwaltungsvorschriften' })).toBeVisible()
      await expect(page).toHaveURL(/\/(.*)?$/)
      await page.goto('/verwaltungsvorschriften')
      await expect(page.getByRole('heading', { name: 'Verwaltungsvorschriften' })).toBeVisible()
      await expect(page.getByLabel('Username or email')).toBeHidden()
      await page.goto('/literatur-unselbststaendig')
      await expect(page).toHaveURL(/\/literatur-unselbststaendig/)
      await expect(page.getByLabel('Username or email')).toBeHidden()
    },
  )

  test(
    'User with both permissions sees both app links on root page',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/')

      await login(page, 'testboth')

      await expect(page.getByRole('link', { name: 'Verwaltungsvorschriften' })).toBeVisible()
      await expect(page).toHaveURL(/\/(#.*)?$/)
    },
  )

  test(
    'User with VwV-only permission is redirected to VwV overview on root login',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/')

      await login(page, 'test')

      await expect(page.getByRole('heading', { name: 'Verwaltungsvorschriften' })).toBeVisible()
      await expect(page).toHaveURL('/verwaltungsvorschriften')
    },
  )

  test(
    'User with VwV-only permission gets 403 on accessing Literature app',
    { tag: ['@RISDEV-9370', '@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/literatur-unselbststaendig')

      await login(page, 'test')
      await expect(
        page.getByRole('heading', {
          name: 'Diese Dokumentationseinheit existiert nicht oder Sie haben keine Berechtigung',
        }),
      ).toBeVisible()
      await expect(page).toHaveURL('/forbidden')
    },
  )

  // --- Tests for user with ONLY Literature permission ('testbag') ---
  test(
    'User with Lit-only permission is redirected to Lit overview on root login',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/')
      await login(page, 'testbag')
      await expect(page).toHaveURL('/literatur-unselbststaendig')
    },
  )

  test(
    'User with Lit-only permission gets 403 on accessing VwV app',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/verwaltungsvorschriften')
      await login(page, 'testbag')
      await expect(
        page.getByRole('heading', {
          name: 'Diese Dokumentationseinheit existiert nicht oder Sie haben keine Berechtigung',
        }),
      ).toBeVisible()
      await expect(page).toHaveURL('/forbidden')
    },
  )

  // --- Tests for user WITHOUT permissions ('testnoaccess') ---

  test(
    'User without permissions gets 403 on accessing VwV app',
    { tag: ['@RISDEV-9370', '@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/verwaltungsvorschriften')
      await login(page, 'testnoaccess')
      await expect(
        page.getByRole('heading', {
          name: 'Diese Dokumentationseinheit existiert nicht oder Sie haben keine Berechtigung',
        }),
      ).toBeVisible()
      await expect(page).toHaveURL('/forbidden')
    },
  )

  test(
    'User without permissions gets 403 on accessing Lit app',
    { tag: ['@RISDEV-9871'] },
    async ({ page }) => {
      await page.goto('/literatur-unselbststaendig')
      await login(page, 'testnoaccess')
      await expect(
        page.getByRole('heading', {
          name: 'Diese Dokumentationseinheit existiert nicht oder Sie haben keine Berechtigung',
        }),
      ).toBeVisible()
      await expect(page).toHaveURL('/forbidden')
    },
  )

  // --- General Test (Logout) ---

  test(
    'redirects to the login page when logging out',
    { tag: ['@RISDEV-9370'] },
    async ({ page }) => {
      await page.goto('/')
      await login(page, 'test')
      await expect(page.getByRole('heading', { name: 'Verwaltungsvorschriften' })).toBeVisible()
      await expect(page).toHaveURL('/verwaltungsvorschriften')
      await page.getByRole('button', { name: 'Log out' }).click()
      await page.waitForURL('**/localhost:8443/realms/ris/**')
      await expect(page.getByRole('textbox', { name: 'Username or email' })).toBeVisible()
    },
  )
})
