import { expect, test } from '@playwright/test'

test.describe('ULI AbgabePage', () => {
  test(
    'Should enter mandatory data, validate fine and publish successfully',
    { tag: ['@RISDEV-9375', '@RISDEV-9374'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      await page.getByText('Aufsatz').click()
      await input.fill('Kon')
      await page.getByText('Kongressvortrag').click()
      await page
        .getByRole('textbox', { name: 'Hauptsachtitel', exact: true })
        .fill('Die unendliche Verhandlung 123äöüß$%&')
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')
      await page.getByText('Speichern').click()

      // when
      await page.getByText('Abgabe').click()
      // then
      await expect(page.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeVisible()

      // when
      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      // then
      await expect(page.getByText('Freigabe ist abgeschlossen.')).toBeVisible()
    },
  )

  test(
    'Should show validation error when mandatory fields are missing',
    { tag: ['@RISDEV-9374'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()

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
    { tag: ['@RISDEV-10123'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      await page.getByText('Aufsatz').click()
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill(' ')
      await page.getByRole('textbox', { name: 'Hauptsachtitel', exact: true }).fill('     ')

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
    'Should show a publication error on backend error 500',
    { tag: ['@RISDEV-9375'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Auf')
      await page.getByText('Aufsatz').click()
      await page
        .getByRole('textbox', { name: 'Hauptsachtitel', exact: true })
        .fill(
          'Dieser Test simuliert einen Fehler im Backend; unabhängig davon, was wir hier eingeben.',
        )
      await page.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')
      await page.getByText('Speichern').click()
      await page.getByText('Abgabe').click()

      // mock an error 500 response on publishing
      await page.route('*/**/api/literature/documentation-units/*/publish', async (route) => {
        const response = await route.fetch()
        await route.fulfill({ status: 500, response })
      })

      // when
      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      // then
      await expect(
        page.getByText('Die Freigabe ist aus technischen Gründen nicht durchgeführt worden.'),
      ).toBeVisible()
    },
  )
})
