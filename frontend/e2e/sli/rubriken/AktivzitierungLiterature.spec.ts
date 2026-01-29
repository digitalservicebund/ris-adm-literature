import { test, expect, Page } from "@playwright/test";

const getSliAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (selbst. Literatur)" });

const getAdmAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (Verwaltungsvorschrift)" });

test.describe("SLI Rubriken – Aktivzitierung Literatur", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/literatur-selbstaendig");
    await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
    await page.waitForURL(/dokumentationseinheit/);
  });

  test(
    "manual Aktivzitierung entry can be created and persists after save + reload",
    { tag: ["@RISDEV-7455"] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page);

      // then – inputs are visible
      await expect(aktiv.getByText("Hauptsachtitel / Dokumentarischer Titel")).toBeVisible();
      await expect(aktiv.getByText("Veröffentlichungsjahr")).toBeVisible();
      await expect(aktiv.getByText("Dokumenttyp")).toBeVisible();
      await expect(aktiv.getByText("Verfasser/in")).toBeVisible();

      // given – user fills all Aktivzitierung fields
      const titleInput = aktiv.getByRole("textbox", { name: "Hauptsachtitel" });
      await titleInput.fill("Mein SLI‑Buch");

      const yearInput = aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" });
      await yearInput.fill("2024");

      const dokumenttypInput = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttypInput.click();
      const optionsOverlay = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(optionsOverlay.getByRole("option", { name: "Bib" })).toBeVisible();
      await optionsOverlay.getByRole("option", { name: "Bib" }).click();

      const verfasserGroup = aktiv.getByLabel("Verfasser/in");
      const verfasserInput = verfasserGroup.getByRole("textbox");
      await verfasserInput.click();
      await verfasserInput.fill("Müller");
      await verfasserInput.press("Enter");

      // when – user transfers values to the list
      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      // creation panel closes
      await expect(aktiv.getByText("Hauptsachtitel")).not.toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();

      // then – entry appears in the Aktivzitierung list
      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Mein SLI‑Buch")).toBeVisible();
      await expect(aktivList.getByText("2024")).toBeVisible();
      await expect(aktivList.getByText("Müller")).toBeVisible();
      await expect(aktivList.getByText("Bib")).toBeVisible();

      // when – user saves the document
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when – user reloads the page
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

      // then – Aktivzitierung entry is still present
      await expect(aktivListAfterReload.getByText("Mein SLI‑Buch")).toBeVisible();
      await expect(aktivListAfterReload.getByText("2024")).toBeVisible();
      await expect(aktivListAfterReload.getByText("Müller")).toBeVisible();
      await expect(aktivListAfterReload.getByText("Bib")).toBeVisible();
    },
  );

  test(
    "deleted Aktivzitierung entry is removed and does not reappear after save + reload",
    { tag: ["@RISDEV-7455"] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page);

      // given – create one Aktivzitierung entry (same steps as in first test)
      const titleInput = aktiv.getByRole("textbox", { name: "Hauptsachtitel" });
      await titleInput.fill("Zu löschendes SLI‑Buch");

      const yearInput = aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" });
      await yearInput.fill("2025");

      const dokumenttypInput = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttypInput.click();

      const optionsOverlay = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(optionsOverlay.getByRole("option", { name: "Bib" })).toBeVisible();
      await optionsOverlay.getByRole("option", { name: "Bib" }).click();

      const verfasserGroup = aktiv.getByLabel("Verfasser/in");
      const verfasserInput = verfasserGroup.getByRole("textbox");
      await verfasserInput.click();
      await verfasserInput.fill("Schmidt");
      await verfasserInput.press("Enter");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Zu löschendes SLI‑Buch")).toBeVisible();

      // when – user opens the entry and deletes it
      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();

      // There can be multiple "Eintrag löschen" buttons (chips etc.), so scope to Aktivzitierung section
      const deleteButtons = aktiv.getByRole("button", { name: "Eintrag löschen" });
      await deleteButtons.last().click();

      // then – entry disappears from the list
      await expect(aktiv.getByText("Zu löschendes SLI‑Buch")).toHaveCount(0);

      // when – user saves the document
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when – user reloads the page
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);

      // then – deleted entry does not reappear
      await expect(aktivAfterReload.getByText("Zu löschendes SLI‑Buch")).toHaveCount(0);
      await expect(aktivAfterReload.getByText("Schmidt")).toHaveCount(0);
    },
  );

  test(
    "editing an Aktivzitierung entry and saving updates the summary and persists",
    { tag: ["@RISDEV-7455"] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page);

      // given – create initial entry
      await aktiv.getByRole("textbox", { name: "Hauptsachtitel" }).fill("Alte Version");
      await aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill("2023");

      const dokumenttypInput = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttypInput.click();

      const optionsOverlay = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(optionsOverlay.getByRole("option", { name: "Bib" })).toBeVisible();
      await optionsOverlay.getByRole("option", { name: "Bib" }).click();

      const verfasserGroup = aktiv.getByLabel("Verfasser/in");
      const verfasserInput = verfasserGroup.getByRole("textbox");
      await verfasserInput.click();
      await verfasserInput.fill("Altmann");
      await verfasserInput.press("Enter");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Alte Version")).toBeVisible();

      // when – user edits the entry and clicks Übernehmen in edit mode
      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();

      const editTitleInput = aktiv.getByRole("textbox", {
        name: "Hauptsachtitel / Dokumentarischer Titel",
      });
      await editTitleInput.fill("Neue Version");

      const editYearInput = aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" });
      await editYearInput.fill("2026");

      const editVerfasserGroup = aktiv.getByLabel("Verfasser/in");
      const editVerfasserInput = editVerfasserGroup.getByRole("textbox");
      await editVerfasserInput.click();
      await editVerfasserInput.fill("Neumann");
      await editVerfasserInput.press("Enter");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      // then – summary shows updated values
      await expect(aktivList.getByText("Neue Version")).toBeVisible();
      await expect(aktivList.getByText("2026")).toBeVisible();
      await expect(aktivList.getByText("Neumann")).toBeVisible();

      // when – user saves and reloads
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

      // then – updated values persist
      await expect(aktivListAfterReload.getByText("Neue Version")).toBeVisible();
      await expect(aktivListAfterReload.getByText("2026")).toBeVisible();
      await expect(aktivListAfterReload.getByText("Neumann")).toBeVisible();
    },
  );
  test(
    "cancelling edits on an Aktivzitierung entry keeps the original summary and persists",
    { tag: ["@RISDEV-7455"] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page);

      // given – create initial entry
      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill("Original Titel");
      await aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill("2020");

      const dokumenttypInput = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttypInput.click();
      const optionsOverlay = page.getByRole("listbox", { name: "Optionsliste" });
      const bibOption = optionsOverlay.getByRole("option", { name: "Bib" });

      await expect(bibOption).toBeVisible();
      await bibOption.click();

      const verfasserGroup = aktiv.getByLabel("Verfasser/in");
      const verfasserInput = verfasserGroup.getByRole("textbox");
      await verfasserInput.click();
      await verfasserInput.fill("Original Autor");
      await verfasserInput.press("Enter");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Original Titel")).toBeVisible();

      // when – user edits but clicks Abbrechen
      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();

      const editTitleInput = aktiv.getByRole("textbox", {
        name: "Hauptsachtitel / Dokumentarischer Titel",
      });
      await editTitleInput.fill("Geänderter Titel");

      await aktiv.getByRole("button", { name: "Abbrechen" }).click();

      // then – summary shows original values, not the changed ones
      await expect(aktivList.getByText("Original Titel")).toBeVisible();
      await expect(aktiv.getByText("Geänderter Titel")).toHaveCount(0);

      // when – user saves and reloads
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

      // then – original summary still persists
      await expect(aktivListAfterReload.getByText("Original Titel")).toBeVisible();
      await expect(aktivAfterReload.getByText("Geänderter Titel")).toHaveCount(0);
    },
  );
});

// Helper function to create a sli doc
async function createDocument(
  page: Page,
  data: { title: string; year: string; docType: string[] },
  publish: boolean,
) {
  await page.goto("/literatur-selbstaendig");
  await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
  await page.waitForURL(/dokumentationseinheit/);

  const formaldaten = page.getByRole("region", { name: "Formaldaten" });

  const input = formaldaten.getByRole("combobox", { name: "Dokumenttyp" });
  const overlay = page.getByRole("listbox", { name: "Optionsliste" });

  for (const type of data.docType) {
    await input.fill(type);
    await overlay.getByRole("option", { name: type }).click();
  }

  await formaldaten
    .getByRole("textbox", { name: "Hauptsachtitel *", exact: true })
    .fill(data.title);
  await formaldaten.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill(data.year);

  await page.getByRole("button", { name: "Speichern" }).click();
  if (publish) {
    await page.getByText("Abgabe").click();
    await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
  }
}

test.describe(
  "Add aktivzitierung via searching through the SLI documents",
  { tag: ["@RISDEV-10276"] },
  () => {
    // This suite is configured to run in 'serial' mode
    // because the expensive document creation is performed ONCE in beforeAll
    // using a shared 'page' object, which requires sequential execution
    // to maintain state and prevent race conditions between tests.
    test.describe.configure({ mode: "serial" });

    let page: Page;

    // Shared, unique test IDs
    const titleSharedId = crypto.randomUUID();
    const titleDoc1 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`;
    const titleDoc2 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`;
    const yearSharedId = crypto.randomUUID();
    const veroeffentlichungsjahrDoc1 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`;
    const veroeffentlichungsjahrDoc2 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`;

    // Configure tests (documents creation)
    test.beforeAll(async ({ browser }) => {
      page = await browser.newPage();

      // Create Doc 1
      await createDocument(
        page,
        {
          title: titleDoc1,
          year: veroeffentlichungsjahrDoc1,
          docType: ["Bib"],
        },
        true,
      );

      // Create Doc 2
      await createDocument(
        page,
        {
          title: titleDoc2,
          year: veroeffentlichungsjahrDoc2,
          docType: ["Dis"],
        },
        true,
      );
    });

    test.afterAll(async () => {
      await page.close();
    });

    test.beforeEach(async () => {
      await page.goto("/literatur-selbstaendig");
      await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
      await page.waitForURL(/dokumentationseinheit/);
    });

    test("shows no results message when searching for non-existent title", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      // when
      const nonExistingTitle = crypto.randomUUID();
      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(nonExistingTitle);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();
      // then
      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });

    test("search by shared title ID retrieves exactly 2 documents", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      // when
      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titleSharedId);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      const searchResultsList = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(searchResultsList).toBeVisible();
      const listItems = searchResultsList.getByRole("listitem");
      await expect(listItems).toHaveCount(2);
      await expect(page.getByText(titleDoc1)).toBeVisible();
      await expect(page.getByText(titleDoc2)).toBeVisible();
    });

    test("search by shared year ID retrieves exactly 2 documents", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      // when
      await aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill(yearSharedId);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      const searchResultsList = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(searchResultsList).toBeVisible();
      const listItems = searchResultsList.getByRole("listitem");
      await expect(listItems).toHaveCount(2);
      await expect(page.getByText(titleDoc1)).toBeVisible();
      await expect(page.getByText(titleDoc2)).toBeVisible();
    });

    test("adding more search parameters (Document Type) reduces results to 1", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titleSharedId);

      // when adding an additional filter: Doc Type 'Bib' (Doc1 only)
      const input = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      const overlay = page.getByRole("listbox", { name: "Optionsliste" });
      await input.fill("Bib");
      await overlay.getByRole("option", { name: "Bib" }).click();

      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      const listItems = aktiv.getByRole("listitem");
      await expect(listItems).toHaveCount(1);
      await expect(page.getByText(titleDoc1)).toBeVisible();
      await expect(page.getByText(titleDoc2)).toBeHidden();
    });

    test("shows a correctly formatted search result: Veröffentlichungsjahr, Verfasser, Dokumentnummer, Hauptsachtitel or Dokumentarischer Titel", async () => {
      const aktiv = getSliAktivzitierungSection(page);
      const currentYear = new Date().getFullYear();
      const documentIdPattern = new RegExp("KALS" + currentYear);
      const headingStructureRegex = new RegExp(
        "Veröffentlichungsjahr: .*KALS" + currentYear + ".*",
      );

      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titleDoc1);

      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      const listItems = aktiv.getByRole("listitem");
      await expect(listItems).toHaveCount(1);
      await expect(listItems.getByText(documentIdPattern)).toBeVisible();
      await expect(listItems.getByText(headingStructureRegex)).toBeVisible();
    });

    test("retrieves only published documents", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      const titel = "Unpublished doc";
      await createDocument(
        page,
        {
          title: titel,
          year: "2025",
          docType: ["Dis"],
        },
        false,
      );

      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titel);

      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });

    test(`clicking on the search result "add" button adds a reference to the aktivzitierung list and clears the search,
      it can only be removed but not edited,
      it can be saved and persist after reload,
      finally the document can be published`, async () => {
      // given
      const formaldaten = page.getByRole("region", { name: "Formaldaten" });

      const input = formaldaten.getByRole("combobox", { name: "Dokumenttyp" });
      const overlay = page.getByRole("listbox", { name: "Optionsliste" });
      await input.fill("Dis");
      await overlay.getByRole("option", { name: "Dis" }).click();

      await formaldaten
        .getByRole("textbox", { name: "Hauptsachtitel *", exact: true })
        .fill("TheTitle");
      await formaldaten.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill("2025");

      const aktiv = getSliAktivzitierungSection(page);

      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titleDoc1);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();
      await expect(aktiv.getByText(titleDoc1)).toBeVisible();

      // when – user adds an aktivzitierung from the search results
      await aktiv.getByRole("button", { name: "Aktivzitierung hinzufügen" }).click();
      // then
      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      const listItems = aktivList.getByRole("listitem");
      await expect(listItems).toHaveCount(1);
      await expect(listItems.getByText(titleDoc1)).toBeVisible();
      await expect(listItems.getByRole("button", { name: "Eintrag löschen" })).toBeVisible();
      await expect(
        listItems.getByRole("button", { name: "Eintrag bearbeiten" }),
      ).not.toBeAttached();

      // when – user saves the document
      await page.getByRole("button", { name: "Speichern" }).click();
      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when – user reloads the page
      await page.reload();
      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      // then – Aktivzitierung entry is still present
      await expect(aktivListAfterReload.getByText(titleDoc1)).toBeVisible();

      // when – user published
      await page.getByText("Abgabe").click();
      await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
      // then
      await expect(page.getByText("Freigabe ist abgeschlossen")).toBeVisible();
    });

    test("shows a paginated list of 15 search results per page, clicking on previous and next triggers shows more results", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      // when
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      // then
      const searchResultsList = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(searchResultsList).toBeVisible();
      const listItems = searchResultsList.getByRole("listitem");
      await expect(listItems).toHaveCount(15);
      await expect(page.getByText("Seite 1")).toBeVisible();

      // when
      await aktiv.getByRole("button", { name: "Weiter" }).click();

      // then
      await expect(aktiv.getByText("Seite 2")).toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Zurück" })).toBeVisible();

      // when
      await aktiv.getByRole("button", { name: "Zurück" }).click();

      // then
      await expect(aktiv.getByText("Seite 1")).toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Zurück" })).toBeHidden();
    });
  },
);

test.describe("SLI Rubriken – Aktivzitierung ADM (Verwaltungsvorschrift)", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/literatur-selbstaendig");
    await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
    await page.waitForURL(/dokumentationseinheit/);
  });

  test(
    "manual ADM aktivzitierung entry can be created and persists after save + reload",
    { tag: ["@RISDEV-10323"] },
    async ({ page }) => {
      const aktiv = getAdmAktivzitierungSection(page);

      // Given: ADM fields are visible
      await expect(aktiv.getByText("Art der Zitierung")).toBeVisible();
      await expect(aktiv.getByText("Normgeber")).toBeVisible();
      await expect(aktiv.getByText("Datum des Inkrafttretens")).toBeVisible();
      await expect(aktiv.getByText("Aktenzeichen")).toBeVisible();
      await expect(aktiv.getByText("Periodikum")).toBeVisible();
      await expect(aktiv.getByText("Zitatstelle")).toBeVisible();
      await expect(aktiv.getByText("Dokumenttyp")).toBeVisible();
      await expect(aktiv.getByText("Dokumentnummer")).toBeVisible();

      // When: user fills all fields (Dokumentnummer should be ignored)
      await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
      const options = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(options).toBeVisible();
      await page.getByRole("option", { name: "Vergleiche" }).click();
      await aktiv.getByRole("combobox", { name: "Normgeber" }).click();
      await page.getByRole("option", { name: "Erstes Organ" }).click();
      const dateInput = aktiv.getByRole("textbox", { name: "Inkrafttretedatum" });
      await dateInput.fill("01.01.2024");
      const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await aktenzeichenInput.fill("Az 123");
      await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
      await page.getByRole("option", { name: "ABc | Die Beispi" }).click();
      const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
      await zitatstelleInput.fill("S. 10");
      const dokumenttypInput = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttypInput.click();
      await page.getByRole("option", { name: "VV" }).click();
      const documentNumberInput = aktiv.getByRole("textbox", { name: "Dokumentnummer" });
      await documentNumberInput.fill("DOC-123-MANUAL");

      // And: user clicks Übernehmen
      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      // Then: creation panel is closed and summary shows data without manual document number
      await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();
      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(
        aktivList.getByText("Erstes Organ, 01.01.2024, Az 123, ABc S. 10 (VV)", { exact: false }),
      ).toBeVisible();
      await expect(aktivList.getByText("DOC-123-MANUAL")).toHaveCount(0);

      // When: user saves the document
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // When: user reloads the page
      await page.reload();

      const aktivAfterReload = getAdmAktivzitierungSection(page);
      // Then: Aktivzitierung entry is still present
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(
        aktivListAfterReload.getByText("Erstes Organ, 01.01.2024, Az 123, ABc S. 10 (VV)", {
          exact: false,
        }),
      ).toBeVisible();
    },
  );

  test(
    "manual ADM aktivzitierung entry can be edited, cancelled, deleted and changes persist",
    { tag: ["@RISDEV-10323"] },
    async ({ page }) => {
      const aktiv = getAdmAktivzitierungSection(page);

      // Given: ADM fields are visible
      const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
      await citationTypeInput.fill("Ve");
      const options = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(options).toBeVisible();
      const vergleicheOption = options.getByRole("option", { name: "Vergleiche" });
      await expect(vergleicheOption).toBeVisible();
      await vergleicheOption.click();
      const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await aktenzeichenInput.fill("Az 123");
      await aktiv.getByRole("button", { name: "Übernehmen" }).click();
      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Az 123", { exact: false })).toBeVisible();

      // When: user edits Aktenzeichen and saves
      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();
      const editAktenzeichen = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await editAktenzeichen.fill("Az 999");
      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      // Then: summary updates to new value

      await expect(aktivList.getByText("Az 999", { exact: false })).toBeVisible();
      await expect(aktivList.getByText("Az 123", { exact: false })).toHaveCount(0);

      // And: after save + reload, edit persists

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getAdmAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(aktivListAfterReload.getByText("Az 999", { exact: false })).toBeVisible();

      // And: user deletes the entry; after save + reload it’s gone

      await aktivAfterReload.getByRole("button", { name: "Eintrag bearbeiten" }).click();
      await aktivAfterReload.getByRole("button", { name: "Eintrag löschen" }).click();
      await expect(aktivAfterReload.getByText("Az 999", { exact: false })).toHaveCount(0);

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterSecondReload = getAdmAktivzitierungSection(page);
      await expect(aktivAfterSecondReload.getByText("Az 999", { exact: false })).toHaveCount(0);
    },
  );

  test("ADM manual entry shows validation error when 'Übernehmen' clicked without citation type", async ({
    page,
  }) => {
    const aktiv = getAdmAktivzitierungSection(page);

    // Given: user fills some fields but NOT citation type
    const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
    await aktenzeichenInput.fill("AZ-123");

    // When: click Übernehmen
    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    // Then: validation error appears, no entry added
    const citationTypeField = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
    await expect(citationTypeField).toBeInViewport();
    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    // When: user selects citation type
    await citationTypeField.click();
    await page.getByRole("option", { name: "Vergleiche" }).click();

    // Then: error clears, Übernehmen enabled
    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();
    const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
    await expect(saveButton).toBeEnabled();

    // When: click Übernehmen again
    await saveButton.click();

    // Then: entry is added
    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("ADM date validation error blocks submit when invalid date, error stays visible", async ({
    page,
  }) => {
    const aktiv = getAdmAktivzitierungSection(page);

    // Given: user selects citation type and fills invalid date
    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    await page.getByRole("option", { name: "Vergleiche" }).click();

    const dateInput = aktiv.getByRole("textbox", { name: "Inkrafttretedatum" });
    await dateInput.fill("00.00.00");
    await dateInput.blur(); // trigger validation

    // Then: date error is visible, Übernehmen is enabled (only disabled when form empty)
    await expect(aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/)).toBeVisible();
    const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
    await expect(saveButton).toBeEnabled();

    // When: user clicks Übernehmen
    await saveButton.click();

    // Then: no entry added, date error still visible
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);
    await expect(aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/)).toBeVisible();
  });

  test.describe(
    "SLI Rubriken – Aktivzitierung ADM (Verwaltungsvorschrift) – Suche",
    {
      tag: ["@RISDEV-10325"],
    },
    () => {
      test("ADM search by document number finds seeded document", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // When: search for seeded ADM document
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result appears with title
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results).toBeVisible();
        await expect(results.getByText("KSNR000000001")).toBeVisible();
        await expect(results.getByText("Alpha Global Setup Document")).toBeVisible();
      });

      test("ADM search by partial document number finds seeded document (left and right truncated)", async ({
        page,
      }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // When: search with partial document number (left-truncated - missing right chars)
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR00000000");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result appears
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results.getByText("KSNR000000001")).toBeVisible();

        // When: search with partial document number (right-truncated - missing left chars)
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result appears
        await expect(results.getByText("KSNR000000001")).toBeVisible();

        // When: search with partial document number (middle match - missing both ends)
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("SNR00000000");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result appears
        await expect(results.getByText("KSNR000000001")).toBeVisible();
      });

      test("ADM search should narrow the results when adding more search criterias (AND-relationship)", async ({
        page,
      }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // When: search for a specific seeded Inkrafttretedatum
        await aktiv.getByRole("textbox", { name: "Inkrafttretedatum" }).fill("01.12.2025");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: 2 results should be visible
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results).toBeVisible();
        const items = results.getByRole("listitem");
        await expect(items).toHaveCount(2);
        await expect(results.getByText("KSNR000000001")).toBeVisible();
        await expect(results.getByText("KSNR000000004")).toBeVisible();

        // When: narrowing the search with a periodikum
        await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
        await page.getByRole("option", { name: "BKK" }).click();
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: 1 result should be visible
        await expect(items).toHaveCount(1);
        await expect(results.getByText("KSNR000000004")).toBeVisible();

        // When: narrowing the search with normgeber
        const normgeber = aktiv.getByRole("combobox", { name: "Normgeber" });
        await normgeber.click();
        await expect(page.getByRole("listbox", { name: "Optionsliste" })).toBeVisible();
        await page.getByRole("option", { name: "Erstes Organ" }).click();
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result should still be visible
        await expect(results.getByText("KSNR000000004")).toBeVisible();

        // When: narrowing the search with aktenzeichen
        await aktiv.getByRole("textbox", { name: "Aktenzeichen" }).fill("Akt 1");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result should still be visible
        await expect(results.getByText("KSNR000000004")).toBeVisible();

        // When: narrowing the search with dokumenttyp
        const dokumenttyp = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
        await dokumenttyp.click();
        await expect(page.getByRole("listbox", { name: "Optionsliste" })).toBeVisible();
        await page.getByRole("option", { name: "VR" }).click();
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result should still be visible
        await expect(results.getByText("KSNR000000004")).toBeVisible();

        // When: narrowing the search with Zitatstelle
        await aktiv.getByRole("textbox", { name: "Zitatstelle" }).fill("789");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: result should still be visible
        await expect(results.getByText("KSNR000000004")).toBeVisible();
      });

      test("ADM search shows no-results message when nothing matches", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given
        await aktiv
          .getByRole("textbox", { name: "Dokumentnummer" })
          .fill("NO-MATCH-" + crypto.randomUUID());
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then
        await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
      });

      test("ADM search paginates 15 results per page", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // When
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then: page 1 has 15 items
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        const items = results.getByRole("listitem");
        await expect(items).toHaveCount(15);
      });

      test("ADM search only retrieves published documents", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000003");
        await aktiv.getByRole("button", { name: "Suchen" }).click();

        // Then
        await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
      });

      test("ADM search result can be added with citation type, clears search, cannot be edited, can be deleted, persists", async ({
        page,
      }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given: user selects a citation type
        const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
        await citationTypeInput.click();
        await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
        await page.getByRole("option", { name: "Vergleiche" }).click();

        // Then: user searches for a document
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results.getByText("KSNR000000001")).toBeVisible();

        // When
        await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: entry in list, creation panel closed, not editable, deletable
        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByRole("listitem")).toHaveCount(1);
        await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();
        await expect(
          aktivList.getByRole("button", { name: "Eintrag bearbeiten" }),
        ).not.toBeAttached();
        await expect(aktivList.getByRole("button", { name: "Eintrag löschen" })).toBeVisible();
        // Verify summary shows document number and title
        await expect(aktivList.getByText(/Vergleiche.*KSNR000000001/)).toBeVisible();
        await expect(aktivList.getByText("KSNR000000001")).toBeVisible();

        // When: save + reload
        await page.getByRole("button", { name: "Speichern" }).click();
        await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
        await page.reload();

        const aktivAfterReload = getAdmAktivzitierungSection(page);
        const aktivListAfterReload = aktivAfterReload.getByRole("list", {
          name: "Aktivzitierung Liste",
        });
        await expect(aktivListAfterReload.getByRole("listitem")).toHaveCount(1);
        await expect(aktivListAfterReload.getByText(/Vergleiche.*KSNR000000001/)).toBeVisible();
        await expect(aktivListAfterReload.getByText("KSNR000000001")).toBeVisible();

        // When: delete + save + reload
        await aktivListAfterReload.getByRole("button", { name: "Eintrag löschen" }).click();
        await page.getByRole("button", { name: "Speichern" }).click();
        await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
        await page.reload();

        // Then: deleted entry does not reappear after reload
        const aktivAfterDelete = getAdmAktivzitierungSection(page);
        const aktivListAfterDelete = aktivAfterDelete.getByRole("list", {
          name: "Aktivzitierung Liste",
        });
        await expect(aktivListAfterDelete.getByRole("listitem")).toHaveCount(0);
        await expect(aktivListAfterDelete.getByText(/Vergleiche.*KSNR000000001/)).toHaveCount(0);
        await expect(aktivListAfterDelete.getByText("KSNR000000001")).toHaveCount(0);
      });

      test("ADM search result cannot be added without citation type, shows validation error", async ({
        page,
      }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given: search without selecting citation type
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results.getByText("KSNR000000001")).toBeVisible();

        // When: try to add without citation type
        await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: validation error appears, no entry added
        const citationTypeField = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
        await expect(citationTypeField).toBeInViewport();
        await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByRole("listitem")).toHaveCount(0);

        // When: user selects citation type
        await citationTypeField.click();
        await page.getByRole("option", { name: "Vergleiche" }).click();

        // Then: error clears
        await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

        // When: click add again
        await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: entry is added
        await expect(aktivList.getByRole("listitem")).toHaveCount(1);
      });

      test("ADM allows adding same document with different citation types", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given: add document with citation type "Ablehnung"
        const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
        await citationTypeInput.fill("Ab");
        await expect(page.getByRole("option", { name: "Ablehnung" })).toBeVisible();
        await page.getByRole("option", { name: "Ablehnung" }).click();
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        await aktiv.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: first entry added
        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByRole("listitem")).toHaveCount(1);

        // When: re-open creation panel, search again, change citation type to "Vergleiche", add again
        await aktiv.getByRole("button", { name: "Weitere Angabe" }).click();
        const citationTypeInputSecond = aktiv.getByRole("combobox", {
          name: "Art der Zitierung",
        });
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        await citationTypeInputSecond.clear();
        await citationTypeInputSecond.fill("Ve");
        await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
        await page.getByRole("option", { name: "Vergleiche" }).click();
        await aktiv.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: second entry added (different citation type)
        await expect(aktivList.getByRole("listitem")).toHaveCount(2);

        // When: re-open creation panel, select "Vergleiche" again, search, then add button must be disabled
        await aktiv.getByRole("button", { name: "Weitere Angabe" }).click();
        const citationTypeInputThird = aktiv.getByRole("combobox", {
          name: "Art der Zitierung",
        });
        await citationTypeInputThird.click();
        await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
        await page.getByRole("option", { name: "Vergleiche" }).click();
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(
          results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first(),
        ).toBeDisabled();
        await expect(results.getByText("Bereits hinzugefügt")).toBeVisible();
      });

      test("ADM uses latest citation type when changed after search", async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        // Given: select "Ablehnung", search
        const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
        await citationTypeInput.click();
        await expect(page.getByRole("option", { name: "Ablehnung" })).toBeVisible();
        await page.getByRole("option", { name: "Ablehnung" }).click();
        await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
        await aktiv.getByRole("button", { name: "Suchen" }).click();
        const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
        await expect(results.getByText("KSNR000000001")).toBeVisible();

        // When: open combobox again, clear citation type, then select "Vergleiche" (without re-searching)
        await citationTypeInput.click();
        await citationTypeInput.clear();
        await citationTypeInput.fill("Ve");
        await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
        await page.getByRole("option", { name: "Vergleiche" }).click();

        // Then: click add
        await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

        // Then: entry has "Vergleiche" (not "Ablehnung")
        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByText(/Vergleiche.*KSNR000000001/)).toBeVisible();
        await expect(aktivList.getByText(/Ablehnung.*KSNR000000001/)).toHaveCount(0);
      });
    },
  );
});
