import { test, expect, type Page } from "@playwright/test";

export const getSliAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (selbst. Literatur)" });

export async function createDocument(
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

test.describe("SLI Rubriken – Aktivzitierung Literatur", { tag: ["@RISDEV-10276"] }, () => {
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

      await expect(aktiv.getByText("Hauptsachtitel / Dokumentarischer Titel")).toBeVisible();
      await expect(aktiv.getByText("Veröffentlichungsjahr")).toBeVisible();
      await expect(aktiv.getByText("Dokumenttyp")).toBeVisible();
      await expect(aktiv.getByText("Verfasser/in")).toBeVisible();

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

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      await expect(aktiv.getByText("Hauptsachtitel")).not.toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("Mein SLI‑Buch")).toBeVisible();
      await expect(aktivList.getByText("2024")).toBeVisible();
      await expect(aktivList.getByText("Müller")).toBeVisible();
      await expect(aktivList.getByText("Bib")).toBeVisible();

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

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

      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();

      const deleteButtons = aktiv.getByRole("button", { name: "Eintrag löschen" });
      await deleteButtons.last().click();

      await expect(aktiv.getByText("Zu löschendes SLI‑Buch")).toHaveCount(0);

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);

      await expect(aktivAfterReload.getByText("Zu löschendes SLI‑Buch")).toHaveCount(0);
      await expect(aktivAfterReload.getByText("Schmidt")).toHaveCount(0);
    },
  );

  test(
    "editing an Aktivzitierung entry and saving updates the summary and persists",
    { tag: ["@RISDEV-7455"] },
    async ({ page }) => {
      const aktiv = getSliAktivzitierungSection(page);

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

      await expect(aktivList.getByText("Neue Version")).toBeVisible();
      await expect(aktivList.getByText("2026")).toBeVisible();
      await expect(aktivList.getByText("Neumann")).toBeVisible();

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

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

      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();

      const editTitleInput = aktiv.getByRole("textbox", {
        name: "Hauptsachtitel / Dokumentarischer Titel",
      });
      await editTitleInput.fill("Geänderter Titel");

      await aktiv.getByRole("button", { name: "Abbrechen" }).click();

      await expect(aktivList.getByText("Original Titel")).toBeVisible();
      await expect(aktiv.getByText("Geänderter Titel")).toHaveCount(0);

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

      await expect(aktivListAfterReload.getByText("Original Titel")).toBeVisible();
      await expect(aktivAfterReload.getByText("Geänderter Titel")).toHaveCount(0);
    },
  );
});

test.describe(
  "Add aktivzitierung via searching through the SLI documents",
  { tag: ["@RISDEV-10276"] },
  () => {
    test.describe.configure({ mode: "serial" });

    let page: Page;

    const titleSharedId = crypto.randomUUID();
    const titleDoc1 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`;
    const titleDoc2 = `Titel: ${titleSharedId} ${crypto.randomUUID()}`;
    const yearSharedId = crypto.randomUUID();
    const veroeffentlichungsjahrDoc1 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`;
    const veroeffentlichungsjahrDoc2 = `Veröffentlichungsjahr: ${yearSharedId} ${crypto.randomUUID()}`;

    test.beforeAll(async ({ browser }) => {
      page = await browser.newPage();

      await createDocument(
        page,
        {
          title: titleDoc1,
          year: veroeffentlichungsjahrDoc1,
          docType: ["Bib"],
        },
        true,
      );

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

      const nonExistingTitle = crypto.randomUUID();
      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(nonExistingTitle);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });

    test("search by shared title ID retrieves exactly 2 documents", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      await aktiv
        .getByRole("textbox", { name: "Hauptsachtitel / Dokumentarischer Titel" })
        .fill(titleSharedId);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      const searchResultsList = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(searchResultsList).toBeVisible();
      const listItems = searchResultsList.getByRole("listitem");
      await expect(listItems).toHaveCount(2);
      await expect(page.getByText(titleDoc1)).toBeVisible();
      await expect(page.getByText(titleDoc2)).toBeVisible();
    });

    test("search by shared year ID retrieves exactly 2 documents", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      await aktiv.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill(yearSharedId);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

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

      const input = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      const overlay = page.getByRole("listbox", { name: "Optionsliste" });
      await input.fill("Bib");
      await overlay.getByRole("option", { name: "Bib" }).click();

      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

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

      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });

    test(`clicking on the search result "add" button adds a reference to the aktivzitierung list and clears the search,
      it can only be removed but not edited,
      it can be saved and persist after reload,
      finally the document can be published`, async () => {
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

      await aktiv.getByRole("button", { name: "Aktivzitierung hinzufügen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      const listItems = aktivList.getByRole("listitem");
      await expect(listItems).toHaveCount(1);
      await expect(listItems.getByText(titleDoc1)).toBeVisible();
      await expect(listItems.getByRole("button", { name: "Eintrag löschen" })).toBeVisible();
      await expect(
        listItems.getByRole("button", { name: "Eintrag bearbeiten" }),
      ).not.toBeAttached();

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      await page.reload();
      const aktivAfterReload = getSliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(aktivListAfterReload.getByText(titleDoc1)).toBeVisible();

      await page.getByText("Abgabe").click();
      await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
      await expect(page.getByText("Freigabe ist abgeschlossen")).toBeVisible();
    });

    test("shows a paginated list of 15 search results per page, clicking on previous and next triggers shows more results", async () => {
      const aktiv = getSliAktivzitierungSection(page);

      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      const searchResultsList = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(searchResultsList).toBeVisible();
      const listItems = searchResultsList.getByRole("listitem");
      await expect(listItems).toHaveCount(15);
      await expect(page.getByText("Seite 1")).toBeVisible();

      await aktiv.getByRole("button", { name: "Weiter" }).click();

      await expect(aktiv.getByText("Seite 2")).toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Zurück" })).toBeVisible();

      await aktiv.getByRole("button", { name: "Zurück" }).click();

      await expect(aktiv.getByText("Seite 1")).toBeVisible();
      await expect(aktiv.getByRole("button", { name: "Zurück" })).toBeHidden();
    });
  },
);
