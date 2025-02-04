import { test, expect } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test(
  'Visiting the app root url, it shows the title "Rechtsinformationen [...]", an icon and user data',
  { tag: ['@RISDEV-6041'] },
  async ({ page }) => {
    await page.goto('/')
    await expect(page.getByText('Rechtsinformationen des Bundes')).toBeVisible()
    // user icon
    await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
    await expect(page.getByText('Vorname Nachname')).toBeVisible()
    await expect(page.getByText('BSG')).toHaveCount(1)
    await expect(page.getByText('Übersicht Verwaltungsvorschriften')).toHaveCount(1)
    await expect(page.getByText('Neue Dokumentationseinheit')).toHaveCount(1)
    await page.getByText('Neue Dokumentationseinheit').click()

    // this needs to change when KSNR are generated dynamically
    await expect(page).toHaveURL('/documentUnit/KSNR054920707/fundstellen')
  },
)
