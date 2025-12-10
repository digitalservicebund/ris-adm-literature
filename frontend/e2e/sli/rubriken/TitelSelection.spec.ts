import { test, expect, Page } from '@playwright/test'

const getFormaldatenSection = (page: Page) => page.getByRole('region', { name: 'Formaldaten' })

test.describe('SLI Rubriken - Titelauswahl', () => {
  test(
    'Hauptsachtitel/Dokumentarischer Titel toggle and persistence',
    { tag: ['@RISDEV-10121', '@RISDEV-10122'] },
    async ({ page }) => {
      // given – new SLI document edit view
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)
      const formaldaten = getFormaldatenSection(page)

      const hauptInput = formaldaten.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      const zusatzInput = formaldaten.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })

      await expect(hauptInput).toBeEnabled()
      await expect(zusatzInput).toBeEnabled()

      await expect(
        formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' }),
      ).toBeVisible()

      // when – user opens documentary title and fills it
      await formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' }).click()
      const dokumentarischerInput = formaldaten.getByRole('textbox', {
        name: 'Dokumentarischer Titel',
      })
      await expect(dokumentarischerInput).toBeVisible()
      await formaldaten
        .getByRole('textbox', { name: 'Dokumentarischer Titel' })
        .fill('Beispieltitel')
      await expect(hauptInput).toBeDisabled()

      const docValue = 'Dokumentarischer Titel 123$%'
      await dokumentarischerInput.fill(docValue)
      await expect(hauptInput).toBeDisabled()
      await expect(zusatzInput).toBeDisabled()
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – value persists and Hauptsachtitel stays disabled
      await page.reload()
      await expect(
        formaldaten.getByRole('textbox', { name: 'Dokumentarischer Titel' }),
      ).toHaveValue(docValue)
      await expect(formaldaten.getByRole('textbox', { name: /^Hauptsachtitel\b/ })).toBeDisabled()
      await expect(
        formaldaten.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' }),
      ).toBeDisabled()

      // when – user clears documentary title and saves again
      const dokumentarischerInputAfterReload = formaldaten.getByRole('textbox', {
        name: 'Dokumentarischer Titel',
      })
      await dokumentarischerInputAfterReload.clear()
      await dokumentarischerInputAfterReload.blur()
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – button returns and Hauptsachtitel is enabled
      await page.reload()
      await expect(
        formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' }),
      ).toBeVisible()
      await expect(formaldaten.getByRole('textbox', { name: /^Hauptsachtitel\b/ })).toBeEnabled()
      await expect(
        formaldaten.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' }),
      ).toBeEnabled()

      await expect(
        formaldaten.getByRole('textbox', { name: 'Dokumentarischer Titel' }),
      ).toHaveCount(0)
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
      const formaldaten = getFormaldatenSection(page)

      const button = formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' })
      await expect(button).toBeEnabled()

      // when
      const hauptInput = formaldaten.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      await hauptInput.fill('Main Title')

      // then
      await expect(button).toBeDisabled()
    },
  )

  test(
    'disables button when typing in Zusatz zum Hauptsachtitel',
    { tag: ['@RISDEV-10121', '@RISDEV-10122'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)
      const formaldaten = getFormaldatenSection(page)

      const button = formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' })
      await expect(button).toBeEnabled()

      // when
      const zusatzInput = formaldaten.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })
      await zusatzInput.fill('Zusatz')

      // then
      await expect(button).toBeDisabled()
    },
  )

  test(
    're-enables button when both fields are cleared',
    { tag: ['@RISDEV-10121', '@RISDEV-10122'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)
      const formaldaten = getFormaldatenSection(page)

      const hauptInput = formaldaten.getByRole('textbox', { name: /^Hauptsachtitel\b/ })
      const zusatzInput = formaldaten.getByRole('textbox', { name: 'Zusatz zum Hauptsachtitel' })
      const button = formaldaten.getByRole('button', { name: 'Dokumentarischer Titel' })

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
