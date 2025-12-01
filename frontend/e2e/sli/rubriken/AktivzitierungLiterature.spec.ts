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
    { tag: ['@RISDEV-7455'] },
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
    { tag: ['@RISDEV-7455'] },
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

  test(
    'editing an Aktivzitierung entry and saving updates the summary and persists',
    { tag: ['@RISDEV-7455'] },
    async ({ page }) => {
      const aktiv = getAktivzitierungSection(page)

      // given – create initial entry
      await aktiv.getByRole('textbox', { name: 'Hauptsachtitel' }).fill('Alte Version')
      await aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2023')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()

      const optionsOverlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(optionsOverlay.getByRole('option', { name: 'Bib' })).toBeVisible()
      await optionsOverlay.getByRole('option', { name: 'Bib' }).click()

      const verfasserGroup = aktiv.getByLabel('Verfasser/in')
      const verfasserInput = verfasserGroup.getByRole('textbox')
      await verfasserInput.click()
      await verfasserInput.fill('Altmann')
      await verfasserInput.press('Enter')

      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Alte Version')).toBeVisible()

      // when – user edits the entry and clicks Übernehmen in edit mode
      await aktiv.getByRole('button', { name: 'Aktivzitierung Editieren' }).click()

      const editTitleInput = aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })
      await editTitleInput.fill('Neue Version')

      const editYearInput = aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await editYearInput.fill('2026')

      const editVerfasserGroup = aktiv.getByLabel('Verfasser/in')
      const editVerfasserInput = editVerfasserGroup.getByRole('textbox')
      await editVerfasserInput.click()
      await editVerfasserInput.fill('Neumann')
      await editVerfasserInput.press('Enter')

      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      // then – summary shows updated values
      await expect(aktivList.getByText('Neue Version')).toBeVisible()
      await expect(aktivList.getByText('2026')).toBeVisible()
      await expect(aktivList.getByText('Neumann')).toBeVisible()

      // when – user saves and reloads
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()
      await page.reload()

      const aktivAfterReload = getAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })

      // then – updated values persist
      await expect(aktivListAfterReload.getByText('Neue Version')).toBeVisible()
      await expect(aktivListAfterReload.getByText('2026')).toBeVisible()
      await expect(aktivListAfterReload.getByText('Neumann')).toBeVisible()
    },
  )
  test(
    'cancelling edits on an Aktivzitierung entry keeps the original summary and persists',
    { tag: ['@RISDEV-7455'] },
    async ({ page }) => {
      const aktiv = getAktivzitierungSection(page)

      // given – create initial entry
      await aktiv.getByRole('textbox', { name: 'Hauptsachtitel' }).fill('Original Titel')
      await aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2020')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()
      await page
        .getByRole('listbox', { name: 'Optionsliste' })
        .getByRole('option', { name: 'Bib' })
        .click()

      const verfasserGroup = aktiv.getByLabel('Verfasser/in')
      const verfasserInput = verfasserGroup.getByRole('textbox')
      await verfasserInput.click()
      await verfasserInput.fill('Original Autor')
      await verfasserInput.press('Enter')

      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Original Titel')).toBeVisible()

      // when – user edits but clicks Abbrechen
      await aktiv.getByRole('button', { name: 'Aktivzitierung Editieren' }).click()

      const editTitleInput = aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })
      await editTitleInput.fill('Geänderter Titel')

      await aktiv.getByRole('button', { name: 'Abbrechen' }).click()

      // then – summary shows original values, not the changed ones
      await expect(aktivList.getByText('Original Titel')).toBeVisible()
      await expect(aktiv.getByText('Geänderter Titel')).toHaveCount(0)

      // when – user saves and reloads
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()
      await page.reload()

      const aktivAfterReload = getAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })

      // then – original summary still persists
      await expect(aktivListAfterReload.getByText('Original Titel')).toBeVisible()
      await expect(aktivAfterReload.getByText('Geänderter Titel')).toHaveCount(0)
    },
  )
})
