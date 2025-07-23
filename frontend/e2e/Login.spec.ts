import { test, expect } from '@playwright/test'

test(
  'redirects to the original sub-URL after logging in',
  { tag: ['@RISDEV-8587'] },
  async ({ page }) => {
    const protectedUrl = 'http://localhost:5173/documentUnit/KSNR999999999/fundstellen'

    await page.goto(protectedUrl)
    const loginFormLocator = page.getByLabel('Username or email')
    await expect(loginFormLocator).toBeVisible()

    const usernameInput = page.getByRole('textbox', { name: 'Username or email' })
    await expect(usernameInput).toBeVisible()

    await usernameInput.fill('test')
    await page.getByRole('textbox', { name: 'Password' }).fill('test')
    await page.getByRole('button', { name: 'Sign In' }).click()

    await expect(page).toHaveURL(new RegExp(protectedUrl))

    const fundstellenLocator = page.getByTestId('documentUnit-documentNumber-fundstellen')
    await expect(fundstellenLocator).toBeVisible()
  },
)
