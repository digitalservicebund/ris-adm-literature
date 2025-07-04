import { chromium, expect } from '@playwright/test'
import * as dotenv from 'dotenv'

dotenv.config()

const authFile = 'playwright/.auth/user.json'

async function authSetup() {
  console.log('--- Starting Authentication Setup ---')

  const browser = await chromium.launch()
  const page = await browser.newPage()

  const loginUrl = new URL('http://localhost:8443/realms/ris/protocol/openid-connect/auth')
  loginUrl.searchParams.append('client_id', 'ris-vwv-local')
  loginUrl.searchParams.append('redirect_uri', 'http://localhost:5173/')
  loginUrl.searchParams.append('response_type', 'code')
  loginUrl.searchParams.append('scope', 'openid profile email')

  await page.goto(loginUrl.toString())

  await page.getByLabel('Username or email').fill(process.env.TEST_USERNAME!)
  await page.getByRole('textbox', { name: 'Password' }).fill(process.env.TEST_PASSWORD!)
  await page.getByRole('button', { name: 'Sign in' }).click()
  console.log('Final page URL after login:', page.url())

  await expect(page.getByLabel('Verwaltungsvorschriften')).toBeVisible()

  await page.context().storageState({ path: authFile })
  await browser.close()

  console.log('--- Authentication State Saved ---')
}

export default authSetup
