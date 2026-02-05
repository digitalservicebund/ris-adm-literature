import { test, expect, Page } from "@playwright/test";

export const getAdmAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (Verwaltungsvorschrift)" });

test.describe(
  "SLI Rubriken – Aktivzitierung ADM (Verwaltungsvorschrift)",
  { tag: ["@RISDEV-10323"] },
  () => {
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

        const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
        await citationTypeInput.fill("Ve");
        const options = page.getByRole("listbox", { name: "Optionsliste" });
        await expect(options).toBeVisible();
        await options.getByRole("option", { name: "Vergleiche" }).click();
        const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
        await aktenzeichenInput.fill("Az 123");
        await aktiv.getByRole("button", { name: "Übernehmen" }).click();
        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByText("Az 123", { exact: false })).toBeVisible();

        await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();
        const editAktenzeichen = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
        await editAktenzeichen.fill("Az 999");
        await aktiv.getByRole("button", { name: "Übernehmen" }).click();

        await expect(aktivList.getByText("Az 999", { exact: false })).toBeVisible();
        await expect(aktivList.getByText("Az 123", { exact: false })).toHaveCount(0);

        await page.getByRole("button", { name: "Speichern" }).click();
        await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
        await page.reload();

        const aktivAfterReload = getAdmAktivzitierungSection(page);
        const aktivListAfterReload = aktivAfterReload.getByRole("list", {
          name: "Aktivzitierung Liste",
        });
        await expect(aktivListAfterReload.getByText("Az 999", { exact: false })).toBeVisible();

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

      const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await aktenzeichenInput.fill("AZ-123");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const citationTypeField = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
      await expect(citationTypeField).toBeInViewport();
      await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByRole("listitem")).toHaveCount(0);

      await citationTypeField.click();
      await page.getByRole("option", { name: "Vergleiche" }).click();

      await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();
      const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
      await expect(saveButton).toBeEnabled();

      await saveButton.click();

      await expect(aktivList.getByRole("listitem")).toHaveCount(1);
    });

    test(
      "ADM date validation error blocks submit when invalid date, error stays visible",
      { tag: ["@RISDEV-10665"] },
      async ({ page }) => {
        const aktiv = getAdmAktivzitierungSection(page);

        await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
        await page.getByRole("option", { name: "Vergleiche" }).click();

        const dateInput = aktiv.getByRole("textbox", { name: "Inkrafttretedatum" });
        await dateInput.fill("00.00.00");
        await dateInput.blur();

        await expect(aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/)).toBeVisible();
        const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
        await expect(saveButton).toBeEnabled();

        await saveButton.click();

        const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
        await expect(aktivList.getByRole("listitem")).toHaveCount(0);
        await expect(aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/)).toBeVisible();
      },
    );

    test.describe(
      "SLI Rubriken – Aktivzitierung ADM (Verwaltungsvorschrift) – Suche",
      { tag: ["@RISDEV-10325"] },
      () => {
        test("ADM search by document number finds seeded document", async ({ page }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results).toBeVisible();
          await expect(results.getByText("KSNR000000001")).toBeVisible();
          await expect(results.getByText("Alpha Global Setup Document")).toBeVisible();
        });

        test("ADM search by partial document number finds seeded document (left and right truncated)", async ({
          page,
        }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR00000000");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results.getByText("KSNR000000001")).toBeVisible();

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000001")).toBeVisible();

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("SNR00000000");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000001")).toBeVisible();
        });

        test("ADM search should narrow the results when adding more search criterias (AND-relationship)", async ({
          page,
        }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv.getByRole("textbox", { name: "Inkrafttretedatum" }).fill("01.12.2025");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results).toBeVisible();
          const items = results.getByRole("listitem");
          await expect(items).toHaveCount(2);
          await expect(results.getByText("KSNR000000001")).toBeVisible();
          await expect(results.getByText("KSNR000000004")).toBeVisible();

          await aktiv.getByRole("combobox", { name: "Periodikum" }).click();
          await page.getByRole("option", { name: "BKK" }).click();
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(items).toHaveCount(1);
          await expect(results.getByText("KSNR000000004")).toBeVisible();

          const normgeber = aktiv.getByRole("combobox", { name: "Normgeber" });
          await normgeber.click();
          await expect(page.getByRole("listbox", { name: "Optionsliste" })).toBeVisible();
          await page.getByRole("option", { name: "Erstes Organ" }).click();
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000004")).toBeVisible();

          await aktiv.getByRole("textbox", { name: "Aktenzeichen" }).fill("Akt 1");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000004")).toBeVisible();

          const dokumenttyp = aktiv.getByRole("combobox", { name: "Dokumenttyp" });
          await dokumenttyp.click();
          await expect(page.getByRole("listbox", { name: "Optionsliste" })).toBeVisible();
          await page.getByRole("option", { name: "VR" }).click();
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000004")).toBeVisible();

          await aktiv.getByRole("textbox", { name: "Zitatstelle" }).fill("789");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(results.getByText("KSNR000000004")).toBeVisible();
        });

        test("ADM search shows no-results message when nothing matches", async ({ page }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv
            .getByRole("textbox", { name: "Dokumentnummer" })
            .fill("NO-MATCH-" + crypto.randomUUID());
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
        });

        test("ADM search paginates 15 results per page", async ({ page }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv.getByRole("button", { name: "Suchen" }).click();

          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          const items = results.getByRole("listitem");
          await expect(items).toHaveCount(15);
        });

        test("ADM search only retrieves published documents", async ({ page }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000003");
          await aktiv.getByRole("button", { name: "Suchen" }).click();

          await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
        });

        test("ADM search result can be added with citation type, clears search, cannot be edited, can be deleted, persists", async ({
          page,
        }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
          await citationTypeInput.click();
          await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
          await page.getByRole("option", { name: "Vergleiche" }).click();

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();
          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results.getByText("KSNR000000001")).toBeVisible();

          await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

          const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
          await expect(aktivList.getByRole("listitem")).toHaveCount(1);
          await expect(aktiv.getByRole("button", { name: "Weitere Angabe" })).toBeVisible();
          await expect(
            aktivList.getByRole("button", { name: "Eintrag bearbeiten" }),
          ).not.toBeAttached();
          await expect(aktivList.getByRole("button", { name: "Eintrag löschen" })).toBeVisible();
          await expect(aktivList.getByText(/Vergleiche.*KSNR000000001/)).toBeVisible();
          await expect(aktivList.getByText("KSNR000000001")).toBeVisible();

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

          await aktivListAfterReload.getByRole("button", { name: "Eintrag löschen" }).click();
          await page.getByRole("button", { name: "Speichern" }).click();
          await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
          await page.reload();

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

          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();
          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results.getByText("KSNR000000001")).toBeVisible();

          await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

          const citationTypeField = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
          await expect(citationTypeField).toBeInViewport();
          await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
          const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
          await expect(aktivList.getByRole("listitem")).toHaveCount(0);

          await citationTypeField.click();
          await page.getByRole("option", { name: "Vergleiche" }).click();

          await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

          await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

          await expect(aktivList.getByRole("listitem")).toHaveCount(1);
        });

        test("ADM allows adding same document with different citation types", async ({ page }) => {
          const aktiv = getAdmAktivzitierungSection(page);

          const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
          await citationTypeInput.fill("Ab");
          await expect(page.getByRole("option", { name: "Ablehnung" })).toBeVisible();
          await page.getByRole("option", { name: "Ablehnung" }).click();
          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();
          await aktiv.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

          const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
          await expect(aktivList.getByRole("listitem")).toHaveCount(1);

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

          await expect(aktivList.getByRole("listitem")).toHaveCount(2);

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

          const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
          await citationTypeInput.click();
          await expect(page.getByRole("option", { name: "Ablehnung" })).toBeVisible();
          await page.getByRole("option", { name: "Ablehnung" }).click();
          await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill("KSNR000000001");
          await aktiv.getByRole("button", { name: "Suchen" }).click();
          const results = aktiv.getByRole("list", { name: "Passende Suchergebnisse" });
          await expect(results.getByText("KSNR000000001")).toBeVisible();

          await citationTypeInput.click();
          await citationTypeInput.clear();
          await citationTypeInput.fill("Ve");
          await expect(page.getByRole("option", { name: "Vergleiche" })).toBeVisible();
          await page.getByRole("option", { name: "Vergleiche" }).click();

          await results.getByRole("button", { name: "Aktivzitierung hinzufügen" }).first().click();

          const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
          await expect(aktivList.getByText(/Vergleiche.*KSNR000000001/)).toBeVisible();
          await expect(aktivList.getByText(/Ablehnung.*KSNR000000001/)).toHaveCount(0);
        });
      },
    );
  },
);
