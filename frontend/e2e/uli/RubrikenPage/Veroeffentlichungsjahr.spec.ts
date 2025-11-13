import { expect, test } from '@playwright/test'

test.describe('ULI Rubriken - Veroeffentlichungsjahr', () => {
  test(
    'Veroeffentlichungsjahr is a mandatory field and its value persists after saving and reloading',
    { tag: ['@RISDEV-9372', '@RISDEV-9373', '@RISDEV-9998'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('Veröffentlichungsjahr *')).toBeVisible()

      // when
      const veroeffentlichungsjahrInput = page.getByRole('textbox', {
        name: 'Veröffentlichungsjahr',
      })
      await veroeffentlichungsjahrInput.fill('2020 bis 2025 $%&')
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(veroeffentlichungsjahrInput).toHaveValue('2020 bis 2025 $%&')
    },
  )
})
