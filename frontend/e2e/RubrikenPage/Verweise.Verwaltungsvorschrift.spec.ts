import { test, expect } from '@playwright/test'

test.describe('Verweise: Verwaltungsvorschrift', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/documentation-units/KSNR054920707', async (route) => {
      const json = {
        documentNumber: 'KSNR054920707',
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        json: null,
      }
      await route.fulfill({ json })
    })
  })

  test('Do not show the button "Weitere Einzelnorm"', async ({ page }) => {
    // given
    await page.goto('/documentUnit/KSNR054920707/rubriken')
    await page.getByRole('radio', { name: 'Verwaltungsvorschrift auswä' }).click()
    await page.getByRole('textbox', { name: 'Art der Verweisung' }).click()
    await page
      .getByRole('button', { name: 'dropdown-option' })
      .filter({ hasText: 'Anwendung' })
      .click()
    await page.getByRole('textbox', { name: 'Suche nach Verwaltungsvorschrift' }).click()
    await page
      .getByRole('button', { name: 'dropdown-option' })
      .filter({ hasText: 'SGB 5Sozialgesetzbuch (SGB) F' })
      .click()
    await page.getByRole('textbox', { name: 'Fassungsdatum der Norm' }).click()
    await page.getByRole('textbox', { name: 'Fassungsdatum der Norm' }).fill('12.12.2024')
    // then
    await expect(page.getByRole('button', { name: "Weitere Einzelnorm"})).toHaveCount(0) 
  })

  // TODO: Show the button when a norm is selected

  // TODO: remove the "x" when a ADM is selected, show the x when a norm is selected
})
