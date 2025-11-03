import { expect, test } from '@playwright/test'

test.describe('ULI Rubriken - titles', () => {
  test(
    'Hauptsachtitel: its value persists after saving and reloading',
    { tag: ['@RISDEV-9867'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('Hauptsachtitel *')).toBeVisible()

      // when
      const input = page.getByRole('textbox', { name: 'Hauptsachtitel' })
      await input.fill('Die unendliche Verhandlung')
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(input).toHaveValue('Die unendliche Verhandlung')
    },
  )

  test.skip(
    'Dokumentarischer Titel: its value persists after saving and reloading',
    { tag: ['@RISDEV-9867'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('Dokumentarischer Titel *')).toBeVisible()

      // when
      const input = page.getByRole('textbox', { name: 'Dokumentarischer Titel' })
      await input.fill('Die Rückkehr der Akten')
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(input).toHaveValue('Die Rückkehr der Akten')
    },
  )
})
