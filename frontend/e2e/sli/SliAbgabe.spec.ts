import { expect, test } from '@playwright/test'

test.describe('SLI AbgabePage', () => {
  test.beforeEach(async ({ page }) => {
    // given
    await page.goto('/literatur-selbstaendig')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
  })

  test(
    'Should show validation error when mandatory fields are missing',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      // when
      await page.getByText('Abgabe').click()
      // then
      await expect(page.getByText('Folgende Pflichtfelder sind nicht befüllt:')).toBeVisible()
      await expect(page.getByText('Dokumenttyp')).toBeVisible()
      await expect(page.getByText('Veröffentlichungsjahr')).toBeVisible()
      await expect(page.getByText('Hauptsachtitel / Dokumentarischer Titel')).toBeVisible()
      await expect(
        page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }),
      ).toBeDisabled()
    },
  )

  test(
    'Should show validation error when mandatory fields contain whitespaces only',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill(' ')
      await page.getByRole('textbox', { name: 'Hauptsachtitel *', exact: true }).fill('     ')

      // when
      await page.getByText('Abgabe').click()

      // then
      await expect(page.getByText('Folgende Pflichtfelder sind nicht befüllt:')).toBeVisible()
      await expect(page.getByText('Veröffentlichungsjahr')).toBeVisible()
      await expect(page.getByText('Hauptsachtitel / Dokumentarischer Titel')).toBeVisible()
      await expect(
        page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }),
      ).toBeDisabled()
    },
  )

  test(
    'Should show success message and enable publish button when all mandatory fields are filled',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()

      await page
        .getByRole('textbox', { name: 'Hauptsachtitel *', exact: true })
        .fill('Die unendliche Verhandlung')
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')
      await page.getByRole('button', { name: 'Speichern' }).click()

      // when
      await page.getByText('Abgabe').click()
      // then
      await expect(page.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeVisible()
      await expect(
        page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }),
      ).toBeEnabled()
    },
  )

  test(
    'Should show links to the mandatory fields; when clicking on a link, navigates to the corresponding field in Rubriken page',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Dokumenttyp' }).click()
      // then
      await expect(page.getByText('Dokumenttyp')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Veröffentlichungsjahr' }).click()
      // then
      await expect(page.getByText('Veröffentlichungsjahr')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Hauptsachtitel / Dokumentarischer Titel' }).click()
      // then
      await expect(page.getByText('Hauptsachtitel *', { exact: true })).toBeInViewport()
    },
  )
})
