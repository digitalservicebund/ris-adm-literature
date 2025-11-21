import { test, expect } from '@playwright/test'

test.describe('SLI Rubriken - Titelauswahl', () => {
  test(
    'Hauptsachtitel/Dokumentarischer Titel toggle and persistence',
    { tag: ['@RISDEV-10121'] },
    async ({ page }) => {
      // given – new SLI document edit view
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      const hauptInput = page.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      const zusatzInput = page.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })

      await expect(hauptInput).toBeEnabled()
      await expect(zusatzInput).toBeEnabled()

      await expect(page.getByRole('button', { name: 'Dokumentarischer Titel' })).toBeVisible()

      // when – user opens documentary title and fills it
      await page.getByRole('button', { name: 'Dokumentarischer Titel' }).click()
      const dokumentarischerInput = page.getByRole('textbox', { name: 'Dokumentarischer Titel' })
      await expect(dokumentarischerInput).toBeVisible()
      await page.getByRole('textbox', { name: 'Dokumentarischer Titel' }).fill('Beispieltitel')
      await expect(hauptInput).toBeDisabled()

      const docValue = 'Dokumentarischer Titel 123$%'
      await dokumentarischerInput.fill(docValue)
      await expect(hauptInput).toBeDisabled()
      await expect(zusatzInput).toBeDisabled()
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – value persists and Hauptsachtitel stays disabled
      await page.reload()
      await expect(page.getByRole('textbox', { name: 'Dokumentarischer Titel' })).toHaveValue(
        docValue,
      )
      await expect(page.getByRole('textbox', { name: /^Hauptsachtitel\b/ })).toBeDisabled()
      await expect(page.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })).toBeDisabled()

      // when – user clears documentary title and saves again
      const dokumentarischerInputAfterReload = page.getByRole('textbox', {
        name: 'Dokumentarischer Titel',
      })
      await dokumentarischerInputAfterReload.clear()
      await dokumentarischerInputAfterReload.blur()
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – button returns and Hauptsachtitel is enabled

      await page.reload()
      await expect(page.getByRole('button', { name: 'Dokumentarischer Titel' })).toBeVisible()
      await expect(page.getByRole('textbox', { name: /^Hauptsachtitel\b/ })).toBeEnabled()
      await expect(page.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })).toBeEnabled()

      await expect(page.getByRole('textbox', { name: 'Dokumentarischer Titel' })).toHaveCount(0)
    },
  )

  test(
    'disables button when typing in Hauptsachtitel',
    { tag: ['@RISDEV-10121'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      const button = page.getByRole('button', { name: 'Dokumentarischer Titel' })
      await expect(button).toBeEnabled()

      // when
      const hauptInput = page.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      await hauptInput.fill('Main Title')

      // then
      await expect(button).toBeDisabled()
    },
  )

  test(
    'disables button when typing in Zusatz zum Hauptsachtitel',
    { tag: ['@RISDEV-10121'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      const button = page.getByRole('button', { name: 'Dokumentarischer Titel' })
      await expect(button).toBeEnabled()

      // when
      const zusatzInput = page.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })
      await zusatzInput.fill('Zusatz')

      // then
      await expect(button).toBeDisabled()
    },
  )

  test(
    're-enables button when both fields are cleared',
    { tag: ['@RISDEV-10121'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      const hauptInput = page.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      const zusatzInput = page.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })
      const button = page.getByRole('button', { name: 'Dokumentarischer Titel' })

      await hauptInput.fill('Main')
      await expect(button).toBeDisabled()

      // when
      await hauptInput.clear()
      await zusatzInput.clear()

      // then
      await expect(button).toBeEnabled()
    },
  )
})
