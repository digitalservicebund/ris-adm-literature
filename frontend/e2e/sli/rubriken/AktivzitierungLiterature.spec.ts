import { test, expect, Page } from '@playwright/test'

const getAktivzitierungSection = (page: Page) =>
  page.getByRole('region', { name: 'Aktivzitierung Literatur' })

test.describe('SLI Rubriken – Aktivzitierung Literatur', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/literatur-selbstaendig')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
    await page.waitForURL(/dokumentationseinheit/)
  })

  test(
    'manual Aktivzitierung entry can be created and persists after save + reload',
    { tag: ['@RISDEV-10266'] },
    async ({ page }) => {
      const aktiv = getAktivzitierungSection(page)

      // then – inputs are visible
      await expect(aktiv.getByText('Hauptsachtitel *')).toBeVisible()
      await expect(aktiv.getByText('Veröffentlichungsjahr *')).toBeVisible()
      await expect(aktiv.getByText('Dokumenttyp *')).toBeVisible()
      await expect(aktiv.getByText('Verfasser/in *')).toBeVisible()

      // given – user fills all Aktivzitierung fields
      const titleInput = aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })
      await titleInput.fill('Mein SLI‑Buch')

      const yearInput = aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await yearInput.fill('2024')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()
      const optionsOverlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(optionsOverlay.getByRole('option', { name: 'Bib' })).toBeVisible()
      await optionsOverlay.getByRole('option', { name: 'Bib' }).click()

      const verfasserGroup = aktiv.getByLabel('Verfasser/in')
      const verfasserInput = verfasserGroup.getByRole('textbox')
      await verfasserInput.click()
      await verfasserInput.fill('Müller')
      await verfasserInput.press('Enter')

      // when – user transfers values to the list
      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      // then – entry appears in the Aktivzitierung list
      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Mein SLI‑Buch')).toBeVisible()
      await expect(aktivList.getByText('2024')).toBeVisible()
      await expect(aktivList.getByText('Müller')).toBeVisible()
      await expect(aktivList.getByText('Bib')).toBeVisible()

      // when – user saves the document
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when – user reloads the page
      await page.reload()

      const aktivAfterReload = getAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })

      // then – Aktivzitierung entry is still present
      await expect(aktivListAfterReload.getByText('Mein SLI‑Buch')).toBeVisible()
      await expect(aktivListAfterReload.getByText('2024')).toBeVisible()
      await expect(aktivListAfterReload.getByText('Müller')).toBeVisible()
      await expect(aktivListAfterReload.getByText('Bib')).toBeVisible()
    },
  )

  test(
    'deleted Aktivzitierung entry is removed and does not reappear after save + reload',
    { tag: ['@RISDEV-10266'] },
    async ({ page }) => {
      const aktiv = getAktivzitierungSection(page)

      // given – create one Aktivzitierung entry (same steps as in first test)
      const titleInput = aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })
      await titleInput.fill('Zu löschendes SLI‑Buch')

      const yearInput = aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await yearInput.fill('2025')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()
      const optionsOverlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await optionsOverlay.getByRole('option', { name: 'Bib' }).click()

      const verfasserGroup = aktiv.getByLabel('Verfasser/in')
      const verfasserInput = verfasserGroup.getByRole('textbox')
      await verfasserInput.click()
      await verfasserInput.fill('Schmidt')
      await verfasserInput.press('Enter')

      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Zu löschendes SLI‑Buch')).toBeVisible()

      // when – user opens the entry and deletes it
      await aktiv.getByRole('button', { name: 'Aktivzitierung Editieren' }).click()

      // There can be multiple "Eintrag löschen" buttons (chips etc.), so scope to Aktivzitierung section
      const deleteButtons = aktiv.getByRole('button', { name: 'Eintrag löschen' })
      await deleteButtons.last().click()

      // then – entry disappears from the list
      await expect(aktiv.getByText('Zu löschendes SLI‑Buch')).toHaveCount(0)

      // when – user saves the document
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when – user reloads the page
      await page.reload()

      const aktivAfterReload = getAktivzitierungSection(page)

      // then – deleted entry does not reappear
      await expect(aktivAfterReload.getByText('Zu löschendes SLI‑Buch')).toHaveCount(0)
      await expect(aktivAfterReload.getByText('Schmidt')).toHaveCount(0)
    },
  )
})
