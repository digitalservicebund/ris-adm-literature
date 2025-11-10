import { expect, test } from '@playwright/test'

test.describe('ULI Publishing', () => {
  test(
    'DokumentTyp is a mandatory field and its value persists after saving and reloading',
    { tag: ['@RISDEV-9866'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      await page.getByText('Aufsatz').click()
      await input.fill('Kon')
      await page.getByText('Kongressvortrag').click()
      await page.getByText('Speichern').click()

      // goto publishing

      // publish

      // expect confirmation
    },
  )
})
