import { expect, test } from '@playwright/test'

test.describe('ULI AbgabePage', () => {
  test(
    'Should enter mandatory data, validate fine and publish successfully',
    { tag: ['@RISDEV-9375'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      await page.getByText('Aufsatz').click()
      await input.fill('Kon')
      await page.getByText('Kongressvortrag').click()
      await page
        .getByRole('textbox', { name: 'Hauptsachtitel', exact: true })
        .fill('Die unendliche Verhandlung 123äöüß$%&')
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')
      await page.getByText('Speichern').click()

      // when
      await page.getByText('Abgabe').click()
      // then
      await expect(page.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeVisible()

      // when
      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      // then
      await expect(page.getByText('Freigabe ist abgeschlossen.')).toBeVisible()
    },
  )
})
