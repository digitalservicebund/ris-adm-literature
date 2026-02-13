import { test, expect, type Page } from "@playwright/test";

export const getUliAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (unselbst. Literatur)" });

test.describe("SLI Rubriken – Aktivzitierung ULI", { tag: ["@RISDEV-10324"] }, () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/literatur-selbstaendig");
    await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
    await page.waitForURL(/dokumentationseinheit/);
  });

  test("manual ULI aktivzitierung entry can be created and persists after save + reload", async ({
    page,
  }) => {
    const aktiv = getUliAktivzitierungSection(page);

    const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await zitatstelleInput.fill("2026, 123");

    const verfasserGroup = aktiv.getByLabel("Verfasser/in");
    const verfasserInput = verfasserGroup.getByRole("textbox");
    await verfasserInput.click();
    await verfasserInput.fill("Doe, John");
    await verfasserInput.press("Enter");

    await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
    await page.getByRole("option", { name: "ABc | Die Beispi" }).click();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByText("Doe, John", { exact: false })).toBeVisible();
    await expect(aktivList.getByText("ABc", { exact: false })).toBeVisible();
    await expect(aktivList.getByText("2026, 123", { exact: false })).toBeVisible();

    await page.getByRole("button", { name: "Speichern" }).click();
    await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
    await page.reload();

    const aktivAfterReload = getUliAktivzitierungSection(page);
    const aktivListAfterReload = aktivAfterReload.getByRole("list", {
      name: "Aktivzitierung Liste",
    });
    await expect(aktivListAfterReload.getByText("Doe, John", { exact: false })).toBeVisible();
    await expect(aktivListAfterReload.getByText("ABc", { exact: false })).toBeVisible();
    await expect(aktivListAfterReload.getByText("2026, 123", { exact: false })).toBeVisible();
  });

  test("shows validation error when Verfasser is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getUliAktivzitierungSection(page);

    const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await zitatstelleInput.fill("2026, 123");

    await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
    await page.getByRole("option", { name: "ABc | Die Beispi" }).click();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    const verfasserGroup = aktiv.getByLabel("Verfasser/in");
    const verfasserInput = verfasserGroup.getByRole("textbox");
    await verfasserInput.click();
    await verfasserInput.fill("Doe, John");
    await verfasserInput.press("Enter");

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("shows validation error when Periodikum is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getUliAktivzitierungSection(page);

    const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await zitatstelleInput.fill("2026, 123");

    const verfasserGroup = aktiv.getByLabel("Verfasser/in");
    const verfasserInput = verfasserGroup.getByRole("textbox");
    await verfasserInput.click();
    await verfasserInput.fill("Doe, John");
    await verfasserInput.press("Enter");

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
    await page.getByRole("option", { name: "ABc | Die Beispi" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("shows validation error when Zitatstelle is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getUliAktivzitierungSection(page);

    const verfasserGroup = aktiv.getByLabel("Verfasser/in");
    const verfasserInput = verfasserGroup.getByRole("textbox");
    await verfasserInput.click();
    await verfasserInput.fill("Doe, John");
    await verfasserInput.press("Enter");

    await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
    await page.getByRole("option", { name: "ABc | Die Beispi" }).click();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await zitatstelleInput.fill("2026, 123");

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("ULI manual entry can be edited, cancelled, deleted and changes persist", async ({
    page,
  }) => {
    const aktiv = getUliAktivzitierungSection(page);

    const zitatstelleInput = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await zitatstelleInput.fill("2026, 123");

    const verfasserGroup = aktiv.getByLabel("Verfasser/in");
    const verfasserInput = verfasserGroup.getByRole("textbox");
    await verfasserInput.click();
    await verfasserInput.fill("Doe, John");
    await verfasserInput.press("Enter");

    await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
    await page.getByRole("option", { name: "ABc | Die Beispi" }).click();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByText("Doe, John", { exact: false })).toBeVisible();
    await expect(aktivList.getByText("2026, 123", { exact: false })).toBeVisible();

    await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();
    const editZitatstelle = aktiv.getByRole("textbox", { name: "Zitatstelle" });
    await editZitatstelle.fill("2027, 456");
    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByText("2027, 456", { exact: false })).toBeVisible();
    await expect(aktivList.getByText("2026, 123", { exact: false })).toHaveCount(0);

    await page.getByRole("button", { name: "Speichern" }).click();
    await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
    await page.reload();

    const aktivAfterReload = getUliAktivzitierungSection(page);
    const aktivListAfterReload = aktivAfterReload.getByRole("list", {
      name: "Aktivzitierung Liste",
    });
    await expect(aktivListAfterReload.getByText("2027, 456", { exact: false })).toBeVisible();

    await aktivAfterReload.getByRole("button", { name: "Eintrag bearbeiten" }).click();
    const editZitatstelle2 = aktivAfterReload.getByRole("textbox", { name: "Zitatstelle" });
    await editZitatstelle2.fill("2028, 789");
    await aktivAfterReload.getByRole("button", { name: "Abbrechen" }).click();

    await expect(aktivListAfterReload.getByText("2027, 456", { exact: false })).toBeVisible();
    await expect(aktivAfterReload.getByText("2028, 789")).toHaveCount(0);

    await aktivAfterReload.getByRole("button", { name: "Eintrag bearbeiten" }).click();
    const deleteButtons = aktivAfterReload.getByRole("button", { name: "Eintrag löschen" });
    await deleteButtons.last().click();
    await expect(aktivAfterReload.getByText("2027, 456", { exact: false })).toHaveCount(0);

    await page.getByRole("button", { name: "Speichern" }).click();
    await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
    await page.reload();

    const aktivAfterSecondReload = getUliAktivzitierungSection(page);
    await expect(aktivAfterSecondReload.getByText("2027, 456", { exact: false })).toHaveCount(0);
  });
});

test.describe(
  "Add aktivzitierung via searching through the ULI documents",
  { tag: ["@RISDEV-10664"] },
  () => {
    test.beforeEach(async ({ page }) => {
      await page.goto("/literatur-selbstaendig");
      await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
      await page.waitForURL(/dokumentationseinheit/);
    });

    test("ULI search by document number finds seeded document", async ({ page }) => {
      const aktiv = getUliAktivzitierungSection(page);

      await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KALU999999999");
      await aktiv.getByRole("button", { name: "Suchen" }).click();

      const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(results).toBeVisible();
      await expect(results.getByText("KALU999999999")).toBeVisible();
      await expect(results.getByText("Lexikon Soziologie und Sozialtheorie")).toBeVisible();
    });

    test("ULI search should narrow the results when adding more search criterias (AND-relationship)", async ({
      page,
    }) => {
      const aktiv = getUliAktivzitierungSection(page);

      const dokumenttyp = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
      await dokumenttyp.click();
      await expect(page.getByRole("listbox", { name: "Optionsliste" })).toBeVisible();
      await expect(page.getByRole("option", { name: "Auf" })).toBeVisible();
      await page.getByRole("option", { name: "Auf" }).click();
      await dokumenttyp.fill("E");
      await page.getByRole("option", { name: "Ebs" }).click();
      await aktiv.getByRole("button", { name: "Suchen" }).click();

      const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(results).toBeVisible();
      const items = results.getByRole("listitem");
      await expect(items).toHaveCount(15);

      await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KALU999999999");
      await aktiv.getByRole("button", { name: "Suchen" }).click();

      await expect(items).toHaveCount(1);
      await expect(results.getByText("KALU999999999")).toBeVisible();
    });

    test("shows no results message when searching for non-existent title", async ({ page }) => {
      const aktiv = getUliAktivzitierungSection(page);

      const nonExistingDocNumber = crypto.randomUUID();
      await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill(nonExistingDocNumber);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });

    test("shows a paginated list of 15 search results per page, clicking on previous and next triggers shows more results", async ({
      page,
    }) => {
      const aktiv = getUliAktivzitierungSection(page);

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

    test("ULI search result can be added, clears search, cannot be edited, can be deleted, persists", async ({
      page,
    }) => {
      const aktiv = getUliAktivzitierungSection(page);

      await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KALU999999999");
      await aktiv.getByRole("button", { name: "Suchen" }).click();
      const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
      await expect(results.getByText("KALU999999999")).toBeVisible();

      await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByRole("listitem")).toHaveCount(1);
      await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();
      await expect(
        aktivList.getByRole("button", { name: "Eintrag bearbeiten" }),
      ).not.toBeAttached();
      await expect(aktivList.getByRole("button", { name: "Eintrag löschen" })).toBeVisible();
      await expect(aktivList.getByText("KALU999999999")).toBeVisible();

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getUliAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(aktivListAfterReload.getByRole("listitem")).toHaveCount(1);
      await expect(aktivListAfterReload.getByText("KALU999999999")).toBeVisible();

      await aktivListAfterReload.getByRole("button", { name: "Eintrag löschen" }).click();
      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterDelete = getUliAktivzitierungSection(page);
      const aktivListAfterDelete = aktivAfterDelete.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(aktivListAfterDelete.getByRole("listitem")).toHaveCount(0);
      await expect(aktivListAfterDelete.getByText("KALU999999999")).toHaveCount(0);
    });
  },
);
