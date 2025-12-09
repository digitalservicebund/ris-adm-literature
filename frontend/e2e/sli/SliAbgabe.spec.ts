import { expect, Page, test } from '@playwright/test'

test.describe('SLI AbgabePage', () => {
  test.beforeEach(async ({ page }) => {
    // given
    await page.goto('/literatur-selbstaendig')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
  })

  async function fillMandatoryFields(page: Page, missing: string[] = []) {
    const form = page.getByRole('region', { name: 'Formaldaten' })

    if (!missing.includes('Dokumenttyp')) {
      const input = form.getByRole('combobox', { name: /^Dokumenttyp/ })
      await input.fill('Bi')
      await page
        .getByRole('listbox', { name: 'Optionsliste' })
        .getByRole('option', { name: 'Bib' })
        .click()
    }

    if (!missing.includes('Hauptsachtitel')) {
      await form
        .getByRole('textbox', { name: /^Hauptsachtitel/ })
        .fill('Die unendliche Verhandlung')
    }

    if (!missing.includes('Veröffentlichungsjahr')) {
      await form.getByRole('textbox', { name: /^Veröffentlichungsjahr/ }).fill('2025')
    }

    await page.getByRole('button', { name: 'Speichern' }).click()
  }

  const missingFields = [
    ['Dokumenttyp'],
    ['Hauptsachtitel'],
    ['Veröffentlichungsjahr'],
    ['Dokumenttyp', 'Veröffentlichungsjahr'],
    ['Dokumenttyp', 'Hauptsachtitel'],
    ['Hauptsachtitel', 'Veröffentlichungsjahr'],
    ['Dokumenttyp', 'Hauptsachtitel', 'Veröffentlichungsjahr'],
  ]

  for (const fields of missingFields) {
    test(
      `Should show validation error when mandatory ${fields.join()} are missing`,
      { tag: ['@RISDEV-10125'] },
      async ({ page }) => {
        await fillMandatoryFields(page, fields)

        await page.getByText('Abgabe').click()

        await expect(page.getByText('Folgende Pflichtfelder sind nicht befüllt:')).toBeVisible()
        for (const field of fields) {
          await expect(page.getByText(field)).toBeVisible()
        }
        const publishButton = page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' })
        await expect(publishButton).toBeDisabled()
      },
    )
  }

  test(
    'Should show validation error when mandatory fields contain whitespaces only',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })

      const input = formaldaten.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()
      await formaldaten.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill(' ')
      await formaldaten
        .getByRole('textbox', { name: 'Hauptsachtitel *', exact: true })
        .fill('     ')

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
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })
      const input = formaldaten.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()

      await formaldaten
        .getByRole('textbox', { name: 'Hauptsachtitel *', exact: true })
        .fill('Die unendliche Verhandlung')
      await formaldaten.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')
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
    'Should publish successfully when all mandatory fields are filled',
    { tag: ['@RISDEV-10125', '@RISDEV-10126'] },
    async ({ page }) => {
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })

      const input = formaldaten.getByRole('combobox', { name: /^Dokumenttyp/ })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()

      await formaldaten
        .getByRole('textbox', { name: /^Hauptsachtitel/ })
        .fill('Die unendliche Verhandlung')
      await formaldaten.getByRole('textbox', { name: /^Veröffentlichungsjahr/ }).fill('2025')
      await page.getByRole('button', { name: 'Speichern' }).click()

      await page.getByText('Abgabe').click()
      await expect(page.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeVisible()

      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      await expect(page.getByText('Freigabe ist abgeschlossen.')).toBeVisible()
    },
  )

  test(
    'Should show a publication error on backend error 500',
    { tag: ['@RISDEV-10126'] },
    async ({ page }) => {
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })
      const input = formaldaten.getByRole('combobox', { name: /^Dokumenttyp/ })
      await input.fill('Bi')
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await overlay.getByRole('option', { name: 'Bib' }).click()
      await formaldaten.getByRole('textbox', { name: /^Hauptsachtitel/ }).fill('Testtitel')
      await formaldaten.getByRole('textbox', { name: /^Veröffentlichungsjahr/ }).fill('2025')
      await page.getByRole('button', { name: 'Speichern' }).click()
      await page.getByText('Abgabe').click()

      await page.route('*/**/api/literature/sli/documentation-units/*/publish', async (route) => {
        const response = await route.fetch()
        await route.fulfill({ status: 500, response })
      })

      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      await expect(
        page.getByText('Die Freigabe ist aus technischen Gründen nicht durchgeführt worden.'),
      ).toBeVisible()
    },
  )

  test(
    'Should show links to the mandatory fields; when clicking on a link, navigates to the corresponding field in Rubriken page',
    { tag: ['@RISDEV-10125'] },
    async ({ page }) => {
      // when
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Dokumenttyp' }).click()
      // then

      await expect(formaldaten.getByText('Dokumenttyp')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Veröffentlichungsjahr' }).click()
      // then
      await expect(formaldaten.getByText('Veröffentlichungsjahr')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Hauptsachtitel / Dokumentarischer Titel' }).click()
      // then
      await expect(formaldaten.getByText('Hauptsachtitel *', { exact: true })).toBeInViewport()
    },
  )
})
