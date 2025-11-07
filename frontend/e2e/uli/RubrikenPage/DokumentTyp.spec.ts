import { expect, test } from '@playwright/test'

test.describe('ULI Rubriken - DokumentTyp', () => {
  test(
    'DokumentTyp is a mandatory field and its value persists after saving and reloading',
    { tag: ['@RISDEV-9866'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('Dokumenttyp *')).toBeVisible()

      // when
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      // then
      await expect(page.getByText('Aufsatz')).toBeVisible()

      // when
      await page.getByText('Aufsatz').click()
      // then
      await expect(page.getByRole('option', { name: 'Auf' }).getByLabel('Auf')).toBeVisible()

      // when
      await input.fill('Kon')
      await page.getByText('Kongressvortrag').click()
      // then
      await expect(page.getByRole('option', { name: 'Kon' }).getByLabel('Kon')).toBeVisible()

      // when
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(page.getByText('Auf')).toBeVisible()
      await expect(page.getByText('Kon')).toBeVisible()
    },
  )

  test(
    'DokumentTyp: an existing value can be deleted',
    { tag: ['@RISDEV-9866'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Ebs')
      await page.getByText('Entscheidungsbesprechung').click()
      await page.getByText('Speichern').click()
      await page.reload()
      const chipItem = page.getByRole('option', { name: 'Ebs' }).getByLabel('Ebs')
      await expect(chipItem).toBeVisible()

      // when
      // eslint-disable-next-line playwright/no-raw-locators
      const removeIcon = page.locator('svg[data-pc-section="removeicon"]')
      await expect(removeIcon).toBeVisible()
      await removeIcon.click()

      // then
      await expect(chipItem).toBeHidden()
    },
  )
})
