import { test as setup, expect, Page } from '@playwright/test'

const admAuthFile = '../frontend/e2e/.auth/adm.json'
const uliAuthFile = '../frontend/e2e/.auth/uli.json'
const baseURL = 'http://localhost:5173'

type DocumentTypeCode =
  | 'VERWALTUNGSVORSCHRIFTEN'
  | 'LITERATUR_SELBSTSTAENDIG'
  | 'LITERATUR_UNSELBSTSTAENDIG'

async function performLogin(
  page: Page,
  username: string,
  password: string,
  documentType: DocumentTypeCode,
) {
  await page.route('/api/**', (route) => {
    const headers = route.request().headers()
    headers['x-document-type'] = documentType
    route.continue({ headers })
  })
  await page.goto(baseURL)

  const loginFormLocator = page.getByLabel('Username or email')
  const mainPageLocator = page.getByText('Neue Dokumentationseinheit')

  await expect(mainPageLocator.or(loginFormLocator)).toBeVisible()

  if (await loginFormLocator.isVisible()) {
    console.log('Login form detected. Performing UI login...')
    await page.getByRole('textbox', { name: 'Username or email' }).fill(username)
    await page.getByRole('textbox', { name: 'Password' }).fill(password)
    await page.getByRole('button', { name: 'Sign In' }).click()
  } else {
    console.log('Already logged in. Skipping UI login.')
  }

  await expect(mainPageLocator).toBeVisible()
}

// Setup for adm user
setup('authenticate as adm user', async ({ page }) => {
  console.info('--- Starting Setup: Authentication adm user ---')
  await performLogin(page, 'test', 'test', 'VERWALTUNGSVORSCHRIFTEN')
  await page.context().storageState({ path: admAuthFile })
  console.info('--- Authentication successful. State saved. ---')
})

// Setup for uli user
setup('authenticate as uli user', async ({ page }) => {
  console.info('--- Starting Setup: Authentication uli user ---')
  await performLogin(page, 'testbag', 'test', 'LITERATUR_UNSELBSTSTAENDIG')
  await page.context().storageState({ path: uliAuthFile })
  console.info('--- Authentication successful. State saved. ---')
})
