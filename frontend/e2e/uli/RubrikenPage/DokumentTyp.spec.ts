import { expect, test } from '@playwright/test'

test.describe('ULI Rubriken - DokumentTyp', () => {
  test(
    'DokumentTyp is a mandatory field and its value persists after saving and reloading',
    { tag: ['@RISDEV-9366'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('DokumentTyp *')).toBeVisible()

      // when
      const input = page.getByRole('combobox', { name: 'DokumentTyp' })
      await input.fill('VR')
      // then
      await expect(page.getByText('Verwaltungsregelung')).toBeVisible()

      // when
      await page.getByText('Verwaltungsregelung').click()
      // then
      await expect(page.getByRole('option', { name: 'VR' }).getByLabel('VR')).toBeVisible()

      // when
      await input.fill('VV')
      await page.getByText('Verwaltungsvorschrift').click()
      // then
      await expect(page.getByRole('option', { name: 'VV' }).getByLabel('VV')).toBeVisible()

      // when
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(page.getByText('VR')).toBeVisible()
      await expect(page.getByText('VV')).toBeVisible()
    },
  )

  test(
    'DokumentTyp: an existing value can be deleted',
    { tag: ['@RISDEV-9366'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)
      const input = page.getByRole('combobox', { name: 'DokumentTyp' })
      await input.fill('VR')
      await page.getByText('Verwaltungsregelung').click()
      await page.getByText('Speichern').click()
      await page.reload()
      await expect(page.getByText('VR')).toBeVisible()

      // when
      // eslint-disable-next-line playwright/no-raw-locators
      const removeIcon = page.locator('svg[data-pc-section="removeicon"]')
      await expect(removeIcon).toBeVisible()
      await removeIcon.click()

      // then
      await expect(page.getByText('VR')).toBeHidden()
    },
  )
})
