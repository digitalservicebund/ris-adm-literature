import { test, expect, Page } from '@playwright/test'

const getSliAktivzitierungSection = (page: Page) =>
  page.getByRole('region', { name: 'Aktivzitierung (selbst. Literatur)' })

const getAdmAktivzitierungSection = (page: Page) =>
  page.getByRole('region', { name: 'Aktivzitierung (Verwaltungsvorschrift)' })

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
      const aktiv = getSliAktivzitierungSection(page)

      // then – inputs are visible
      await expect(aktiv.getByText('Hauptsachtitel / Dokumentarischer Titel')).toBeVisible()
      await expect(aktiv.getByText('Veröffentlichungsjahr')).toBeVisible()
      await expect(aktiv.getByText('Dokumenttyp')).toBeVisible()
      await expect(aktiv.getByText('Verfasser/in')).toBeVisible()

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

      // creation panel stays open and inputs are cleared after Übernehmen
      await expect(aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })).toHaveValue('')
      await expect(aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' })).toHaveValue('')

      // eslint-disable-next-line playwright/no-raw-locators
      const dokumenttypChipList = aktiv
        .getByTestId('document-type-autocomplete')
        .locator('[role="listbox"][aria-orientation="horizontal"]')

      await expect(
        // eslint-disable-next-line playwright/no-raw-locators
        dokumenttypChipList.locator('.p-autocomplete-input-chip'),
      ).toHaveCount(0)

      const verfasserGroupAfter = aktiv.getByLabel('Verfasser/in')
      await expect(verfasserGroupAfter.getByRole('textbox')).toHaveValue('')

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

      const aktivAfterReload = getSliAktivzitierungSection(page)
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
      const aktiv = getSliAktivzitierungSection(page)

      // given – create one Aktivzitierung entry (same steps as in first test)
      const titleInput = aktiv.getByRole('textbox', { name: 'Hauptsachtitel' })
      await titleInput.fill('Zu löschendes SLI‑Buch')

      const yearInput = aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await yearInput.fill('2025')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()

      const optionsOverlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(optionsOverlay.getByRole('option', { name: 'Bib' })).toBeVisible()
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
      await aktiv.getByRole('button', { name: 'Eintrag bearbeiten' }).click()

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

      const aktivAfterReload = getSliAktivzitierungSection(page)

      // then – deleted entry does not reappear
      await expect(aktivAfterReload.getByText('Zu löschendes SLI‑Buch')).toHaveCount(0)
      await expect(aktivAfterReload.getByText('Schmidt')).toHaveCount(0)
    },
  )

  test(
    'editing an Aktivzitierung entry and saving updates the summary and persists',
    { tag: ['@RISDEV-7455'] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page)

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
      await aktiv.getByRole('button', { name: 'Eintrag bearbeiten' }).click()

      const editTitleInput = aktiv.getByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      })
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

      const aktivAfterReload = getSliAktivzitierungSection(page)
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
      const aktiv = getSliAktivzitierungSection(page)

      // given – create initial entry
      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill('Original Titel')
      await aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2020')

      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()
      const optionsOverlay = page.getByRole('listbox', { name: 'Optionsliste' })
      const bibOption = optionsOverlay.getByRole('option', { name: 'Bib' })

      await expect(bibOption).toBeVisible()
      await bibOption.click()

      const verfasserGroup = aktiv.getByLabel('Verfasser/in')
      const verfasserInput = verfasserGroup.getByRole('textbox')
      await verfasserInput.click()
      await verfasserInput.fill('Original Autor')
      await verfasserInput.press('Enter')

      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Original Titel')).toBeVisible()

      // when – user edits but clicks Abbrechen
      await aktiv.getByRole('button', { name: 'Eintrag bearbeiten' }).click()

      const editTitleInput = aktiv.getByRole('textbox', {
        name: 'Hauptsachtitel / Dokumentarischer Titel',
      })
      await editTitleInput.fill('Geänderter Titel')

      await aktiv.getByRole('button', { name: 'Abbrechen' }).click()

      // then – summary shows original values, not the changed ones
      await expect(aktivList.getByText('Original Titel')).toBeVisible()
      await expect(aktiv.getByText('Geänderter Titel')).toHaveCount(0)

      // when – user saves and reloads
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()
      await page.reload()

      const aktivAfterReload = getSliAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })

      // then – original summary still persists
      await expect(aktivListAfterReload.getByText('Original Titel')).toBeVisible()
      await expect(aktivAfterReload.getByText('Geänderter Titel')).toHaveCount(0)
    },
  )
})

// Helper function to create a sli doc
async function createDocument(
  page: Page,
  data: { title: string; year: string; docType: string[] },
  publish: boolean,
) {
  await page.goto('/literatur-selbstaendig')
  await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
  await page.waitForURL(/dokumentationseinheit/)

  const formaldaten = page.getByRole('region', { name: 'Formaldaten' })

  const input = formaldaten.getByRole('combobox', { name: 'Dokumenttyp' })
  const overlay = page.getByRole('listbox', { name: 'Optionsliste' })

  for (const type of data.docType) {
    await input.fill(type)
    await overlay.getByRole('option', { name: type }).click()
  }

  await formaldaten.getByRole('textbox', { name: 'Hauptsachtitel *', exact: true }).fill(data.title)
  await formaldaten.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill(data.year)

  await page.getByRole('button', { name: 'Speichern' }).click()
  if (publish) {
    await page.getByText('Abgabe').click()
    await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
  }
}

test.describe(
  'Add aktivzitierung via searching through the SLI documents',
  { tag: ['@RISDEV-10276'] },
  () => {
    // This suite is configured to run in 'serial' mode
    // because the expensive document creation is performed ONCE in beforeAll
    // using a shared 'page' object, which requires sequential execution
    // to maintain state and prevent race conditions between tests.
    test.describe.configure({ mode: 'serial' })

    let page: Page

    // Shared, unique test IDs
    const titleSharedId = crypto.randomUUID()
    const titleDoc1 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`
    const titleDoc2 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`
    const yearSharedId = crypto.randomUUID()
    const veroeffentlichungsjahrDoc1 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`
    const veroeffentlichungsjahrDoc2 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`

    // Configure tests (documents creation)
    test.beforeAll(async ({ browser }) => {
      page = await browser.newPage()

      // Create Doc 1
      await createDocument(
        page,
        {
          title: titleDoc1,
          year: veroeffentlichungsjahrDoc1,
          docType: ['Bib'],
        },
        true,
      )

      // Create Doc 2
      await createDocument(
        page,
        {
          title: titleDoc2,
          year: veroeffentlichungsjahrDoc2,
          docType: ['Dis'],
        },
        true,
      )
    })

    test.afterAll(async () => {
      await page.close()
    })

    test.beforeEach(async () => {
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)
    })

    test('shows no results message when searching for non-existent title', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      // when
      const nonExistingTitle = crypto.randomUUID()
      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(nonExistingTitle)
      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()
      // then
      await expect(aktiv.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
    })

    test('search by shared title ID retrieves exactly 2 documents', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      // when
      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(titleSharedId)
      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      const searchResultsList = aktiv.getByRole('list', { name: 'Passende Suchergebnisse' })
      await expect(searchResultsList).toBeVisible()
      const listItems = searchResultsList.getByRole('listitem')
      await expect(listItems).toHaveCount(2)
      await expect(page.getByText(titleDoc1)).toBeVisible()
      await expect(page.getByText(titleDoc2)).toBeVisible()
    })

    test('search by shared year ID retrieves exactly 2 documents', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      // when
      await aktiv.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill(yearSharedId)
      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      const searchResultsList = aktiv.getByRole('list', { name: 'Passende Suchergebnisse' })
      await expect(searchResultsList).toBeVisible()
      const listItems = searchResultsList.getByRole('listitem')
      await expect(listItems).toHaveCount(2)
      await expect(page.getByText(titleDoc1)).toBeVisible()
      await expect(page.getByText(titleDoc2)).toBeVisible()
    })

    test('adding more search parameters (Document Type) reduces results to 1', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(titleSharedId)

      // when adding an additional filter: Doc Type 'Bib' (Doc1 only)
      const input = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await input.fill('Bib')
      await overlay.getByRole('option', { name: 'Bib' }).click()

      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      const listItems = aktiv.getByRole('listitem')
      await expect(listItems).toHaveCount(1)
      await expect(page.getByText(titleDoc1)).toBeVisible()
      await expect(page.getByText(titleDoc2)).toBeHidden()
    })

    test('shows a correctly formatted search result: Veröffentlichungsjahr, Verfasser, Dokumentnummer, Hauptsachtitel or Dokumentarischer Titel', async () => {
      const aktiv = getSliAktivzitierungSection(page)
      const currentYear = new Date().getFullYear()
      const documentIdPattern = new RegExp('KALS' + currentYear)
      const headingStructureRegex = new RegExp('Veröffentlichungsjahr: .*KALS' + currentYear + '.*')

      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(titleDoc1)

      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      const listItems = aktiv.getByRole('listitem')
      await expect(listItems).toHaveCount(1)
      await expect(listItems.getByText(documentIdPattern)).toBeVisible()
      await expect(listItems.getByText(headingStructureRegex)).toBeVisible()
    })

    test('retrieves only published documents', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      const titel = 'Unpublished doc'
      await createDocument(
        page,
        {
          title: titel,
          year: '2025',
          docType: ['Dis'],
        },
        false,
      )

      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(titel)

      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      await expect(aktiv.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
    })

    test(`clicking on the search result "add" button adds a reference to the aktivzitierung list and clears the search,
      it can only be removed but not edited,
      it can be saved and persist after reload,
      finally the document can be published`, async () => {
      // given
      const formaldaten = page.getByRole('region', { name: 'Formaldaten' })

      const input = formaldaten.getByRole('combobox', { name: 'Dokumenttyp' })
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await input.fill('Dis')
      await overlay.getByRole('option', { name: 'Dis' }).click()

      await formaldaten
        .getByRole('textbox', { name: 'Hauptsachtitel *', exact: true })
        .fill('TheTitle')
      await formaldaten.getByRole('textbox', { name: 'Veröffentlichungsjahr' }).fill('2025')

      const aktiv = getSliAktivzitierungSection(page)

      await aktiv
        .getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' })
        .fill(titleDoc1)
      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()
      await expect(aktiv.getByText(titleDoc1)).toBeVisible()

      // when – user adds an aktivzitierung from the search results
      await aktiv.getByRole('button', { name: 'Aktivzitierung hinzufügen' }).click()
      // then
      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      const listItems = aktivList.getByRole('listitem')
      await expect(listItems).toHaveCount(1)
      await expect(listItems.getByText(titleDoc1)).toBeVisible()
      await expect(
        aktiv.getByRole('textbox', { name: 'Hauptsachtitel / Dokumentarischer Titel' }),
      ).toBeEmpty()
      await expect(listItems.getByRole('button', { name: 'Eintrag löschen' })).toBeVisible()
      await expect(listItems.getByRole('button', { name: 'Eintrag bearbeiten' })).not.toBeAttached()

      // when – user saves the document
      await page.getByRole('button', { name: 'Speichern' }).click()
      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when – user reloads the page
      await page.reload()
      const aktivAfterReload = getSliAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })
      // then – Aktivzitierung entry is still present
      await expect(aktivListAfterReload.getByText(titleDoc1)).toBeVisible()

      // when – user published
      await page.getByText('Abgabe').click()
      await page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }).click()
      // then
      await expect(page.getByText('Freigabe ist abgeschlossen')).toBeVisible()
    })

    test('shows a paginated list of 15 search results per page, clicking on previous and next triggers shows more results', async () => {
      const aktiv = getSliAktivzitierungSection(page)

      // when
      await aktiv.getByRole('button', { name: 'Dokumente Suchen' }).click()

      // then
      const searchResultsList = aktiv.getByRole('list', { name: 'Passende Suchergebnisse' })
      await expect(searchResultsList).toBeVisible()
      const listItems = searchResultsList.getByRole('listitem')
      await expect(listItems).toHaveCount(15)
      await expect(page.getByText('Seite 1')).toBeVisible()

      // when
      await aktiv.getByRole('button', { name: 'Weiter' }).click()

      // then
      await expect(aktiv.getByText('Seite 2')).toBeVisible()
      await expect(aktiv.getByRole('button', { name: 'Zurück' })).toBeVisible()

      // when
      await aktiv.getByRole('button', { name: 'Zurück' }).click()

      // then
      await expect(aktiv.getByText('Seite 1')).toBeVisible()
      await expect(aktiv.getByRole('button', { name: 'Zurück' })).toBeHidden()
    })
  },
)

test.describe('SLI Rubriken – Aktivzitierung ADM (Verwaltungsvorschrift)', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/literatur-selbstaendig')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
    await page.waitForURL(/dokumentationseinheit/)
  })

  test(
    'manual ADM aktivzitierung entry can be created and persists after save + reload',
    { tag: ['@RISDEV-10323'] },
    async ({ page }) => {
      const aktiv = getAdmAktivzitierungSection(page)

      // Given: ADM fields are visible
      await expect(aktiv.getByText('Art der Zitierung')).toBeVisible()
      await expect(aktiv.getByText('Normgeber')).toBeVisible()
      await expect(aktiv.getByText('Datum des Inkrafttretens')).toBeVisible()
      await expect(aktiv.getByText('Aktenzeichen')).toBeVisible()
      await expect(aktiv.getByText('Periodikum')).toBeVisible()
      await expect(aktiv.getByText('Zitatstelle')).toBeVisible()
      await expect(aktiv.getByText('Dokumenttyp')).toBeVisible()
      await expect(aktiv.getByText('Dokumentnummer')).toBeVisible()

      // When: user fills all fields (Dokumentnummer should be ignored)
      await aktiv.getByRole('combobox', { name: 'Art der Zitierung' }).click()
      const options = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(options).toBeVisible()
      await page.getByRole('option', { name: 'Übernahme' }).click()
      await aktiv.getByRole('combobox', { name: 'Normgeber' }).click()
      await page.getByRole('option', { name: 'Erstes Organ' }).click()
      const dateInput = aktiv.getByRole('textbox', { name: 'Inkrafttretedatum' })
      await dateInput.fill('01.01.2024')
      const aktenzeichenInput = aktiv.getByRole('textbox', { name: 'Aktenzeichen' })
      await aktenzeichenInput.fill('Az 123')
      await aktiv.getByRole('combobox', { name: 'Periodikum' }).click()
      await page.getByRole('option', { name: 'ABc | Die Beispi' }).click()
      const zitatstelleInput = aktiv.getByRole('textbox', { name: 'Zitatstelle' })
      await zitatstelleInput.fill('S. 10')
      const dokumenttypInput = aktiv.getByRole('combobox', { name: 'Dokumenttyp' })
      await dokumenttypInput.click()
      await page.getByRole('option', { name: 'VV' }).click()
      const documentNumberInput = aktiv.getByRole('textbox', { name: 'Dokumentnummer' })
      await documentNumberInput.fill('DOC-123-MANUAL')

      // And: user clicks Übernehmen
      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      // Then: fields are cleared and summary shows data without manual document number
      await expect(aktiv.getByRole('textbox', { name: 'Inkrafttretedatum' })).toHaveValue('')
      await expect(aktiv.getByRole('textbox', { name: 'Aktenzeichen' })).toHaveValue('')
      await expect(aktiv.getByRole('textbox', { name: 'Zitatstelle' })).toHaveValue('')
      await expect(aktiv.getByRole('textbox', { name: 'Dokumentnummer' })).toHaveValue('')
      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(
        aktivList.getByText('Erstes Organ, 01.01.2024, Az 123, ABc S. 10 (VV)', { exact: false }),
      ).toBeVisible()
      await expect(aktivList.getByText('DOC-123-MANUAL')).toHaveCount(0)

      // When: user saves the document
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // When: user reloads the page
      await page.reload()

      const aktivAfterReload = getAdmAktivzitierungSection(page)
      // Then: Aktivzitierung entry is still present
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })
      await expect(
        aktivListAfterReload.getByText('Erstes Organ, 01.01.2024, Az 123, ABc S. 10 (VV)', {
          exact: false,
        }),
      ).toBeVisible()
    },
  )

  test(
    'manual ADM aktivzitierung entry can be edited, cancelled, deleted and changes persist',
    { tag: ['@RISDEV-10323'] },
    async ({ page }) => {
      const aktiv = getAdmAktivzitierungSection(page)

      // Given: ADM fields are visible
      await aktiv.getByRole('combobox', { name: 'Art der Zitierung' }).click()
      const options = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(options).toBeVisible()
      await page.getByRole('option', { name: 'Übernahme' }).click()
      const aktenzeichenInput = aktiv.getByRole('textbox', { name: 'Aktenzeichen' })
      await aktenzeichenInput.fill('Az 123')
      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()
      const aktivList = aktiv.getByRole('list', { name: 'Aktivzitierung Liste' })
      await expect(aktivList.getByText('Az 123', { exact: false })).toBeVisible()

      // When: user edits Aktenzeichen and saves
      await aktiv.getByRole('button', { name: 'Eintrag bearbeiten' }).click()
      const editAktenzeichen = aktiv.getByRole('textbox', { name: 'Aktenzeichen' })
      await editAktenzeichen.fill('Az 999')
      await aktiv.getByRole('button', { name: 'Übernehmen' }).click()

      // Then: summary updates to new value

      await expect(aktivList.getByText('Az 999', { exact: false })).toBeVisible()
      await expect(aktivList.getByText('Az 123', { exact: false })).toHaveCount(0)

      // And: after save + reload, edit persists

      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()
      await page.reload()

      const aktivAfterReload = getAdmAktivzitierungSection(page)
      const aktivListAfterReload = aktivAfterReload.getByRole('list', {
        name: 'Aktivzitierung Liste',
      })
      await expect(aktivListAfterReload.getByText('Az 999', { exact: false })).toBeVisible()

      // And: user deletes the entry; after save + reload it’s gone

      await aktivAfterReload.getByRole('button', { name: 'Eintrag bearbeiten' }).click()
      await aktivAfterReload.getByRole('button', { name: 'Eintrag löschen' }).click()
      await expect(aktivAfterReload.getByText('Az 999', { exact: false })).toHaveCount(0)

      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()
      await page.reload()

      const aktivAfterSecondReload = getAdmAktivzitierungSection(page)
      await expect(aktivAfterSecondReload.getByText('Az 999', { exact: false })).toHaveCount(0)
    },
  )
})
