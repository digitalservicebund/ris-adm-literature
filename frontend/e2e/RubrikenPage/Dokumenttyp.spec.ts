import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Dokumenttyp', () => {
  test(
    'Dokumenttyp: Can be filtered, entered and persists through a reload',
    { tag: ['@RISDEV-6299', '@RISDEV-6314'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/documentUnit/)
      await page.getByText('Rubriken').click()

      // when
      const dokumenttypElement = page.getByText('Dokumenttyp *')
      await dokumenttypElement.click()
      await dokumenttypElement.fill('VV')
      await page.getByText('VV').click()
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.reload()

      // then
      await expect(dokumenttypElement).toHaveValue('Verwaltungsvorschrift')
    },
  )

  test(
    'Dokumenttyp: Can be filtered by VV, entered and persists, removed and persists',
    { tag: ['@RISDEV-6314'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/documentUnit/)
      await page.getByText('Rubriken').click()

      // when
      const dokumenttypElement = page.getByText('Dokumenttyp *')
      await dokumenttypElement.click()
      await dokumenttypElement.fill('VV')
      await page.getByText('VV').click()
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.reload()
      await page.getByRole('button', { name: 'Auswahl zurücksetzen', exact: true}).click()
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.reload()

      // then
      const dokumentTypTextbox = page.getByRole('textbox', { name: 'Dokumenttyp', exact: true })
      await expect(dokumentTypTextbox.getByText('Verwaltungsvorschrift')).toHaveCount(0)
    })
})
