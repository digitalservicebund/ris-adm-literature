import { test, expect } from "@playwright/test";

test.describe("ADM AbgabePage", () => {
  test(
    "A document is publishable when its mandatory fields are non empty",
    { tag: ["@RISDEV-8436"] },
    async ({ page }) => {
      // given
      await page.goto("/verwaltungsvorschriften/dokumentationseinheit/KSNR999999999");

      // when
      await page.getByText("Abgabe").click();

      // then
      await expect(page.getByRole("heading", { name: "Abgabe" })).toBeVisible();
      await expect(page.getByText("Plausibilitätsprüfung")).toBeVisible();
      await expect(page.getByText("Alle Pflichtfelder sind korrekt ausgefüllt.")).toBeVisible();
      await expect(
        page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }),
      ).toBeEnabled();
    },
  );

  test(
    `A document is not publishable when at least one of its mandatory fields is empty,
    shows the required fields and a link to the rubriken page`,
    { tag: ["@RISDEV-8436"] },
    async ({ page }) => {
      // given
      await page.goto("/verwaltungsvorschriften/dokumentationseinheit/KSNR999999999/rubriken");
      await expect(page.getByText("Amtl. Langüberschrift *")).toHaveCount(1);
      await page.getByText("Amtl. Langüberschrift *").fill("");
      await expect(page.getByRole("button", { name: "Speichern" })).toBeVisible();

      // when
      await page.getByText("Abgabe").click();

      // then
      await expect(page.getByRole("button", { name: "Speichern" })).toBeHidden();
      await expect(page.getByRole("heading", { name: "Abgabe" })).toBeVisible();
      await expect(page.getByText("Plausibilitätsprüfung")).toBeVisible();
      await expect(page.getByText("Folgende Pflichtfelder sind nicht befüllt")).toBeVisible();
      await expect(page.getByText("Amtl. Langüberschrift")).toBeVisible();
      await expect(page.getByRole("button", { name: "Rubriken bearbeiten" })).toBeVisible();
      await expect(
        page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }),
      ).toBeDisabled();

      // when
      await page.getByRole("button", { name: "Rubriken bearbeiten" }).click();

      // then
      await expect(page.getByRole("heading", { name: "Formaldaten" })).toBeVisible();
    },
  );

  test(
    "Should show links to the mandatory fields; when clicking on a link, navigates to the corresponding field in Rubriken page",
    { tag: ["@RISDEV-10275"] },
    async ({ page }) => {
      // given
      await page.goto("/");
      await page.getByText("Neue Dokumentationseinheit").click();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Amtl. Langüberschrift" }).click();
      // then
      await expect(page.getByText("Amtl. Langüberschrift *")).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Datum des Inkrafttretens" }).click();
      // then
      await expect(page.getByText("Datum des Inkrafttretens *")).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Dokumenttyp" }).click();
      // then
      await expect(page.getByText("Dokumenttyp *", { exact: true })).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Normgeber" }).click();
      // then
      await expect(page.getByText("Normgeber *", { exact: true })).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Zitierdatum" }).click();
      // then
      await expect(page.getByText("Zitierdatum *", { exact: true })).toBeInViewport();
    },
  );

  test("Should fill in all fields and succeed in publishing", async ({ page }) => {
    // Given
    await page.goto("/");
    await page.getByText("Neue Dokumentationseinheit").click();

    // Fundstellen
    await page.getByRole("combobox", { name: "Periodikum" }).fill("Die");
    await page.getByText("ABc | Die Beispieler").click();
    await page.getByRole("textbox", { name: "Zitatstelle" }).fill("2024, Seite 24");
    await page.getByText("Übernehmen").click();

    // Rubriken
    await page.getByRole("link", { name: "Rubriken" }).click();

    // Formaldaten
    await page.getByText("Amtl. Langüberschrift *").fill("This is my Langüberschrift");
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Zitierdatum" }).locator("input").fill("15.01.2025");

    // Normgeber
    await page.getByRole("combobox", { name: "Normgeber" }).click();
    await page.getByText("Erstes Organ").click();
    await page.getByRole("combobox", { name: "Region" }).fill("AA");
    await page.getByText("AA").click();
    await page.getByRole("button", { name: "Normgeber übernehmen", exact: true }).click();

    await page.getByText("Dokumenttyp *").click();
    await page.getByText("Dokumenttyp *").fill("VV");
    await page.getByText("VV").click();

    await page.getByText("Dokumenttyp Zusatz").fill("Bekanntmachung");

    await page.getByText("Datum des Inkrafttretens *").fill("15.01.2025");

    await page.getByText("Datum des Ausserkrafttretens").fill("15.01.2025");

    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Aktenzeichen" }).locator("input").fill("Az1");
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Aktenzeichen" }).locator("input").press("Enter");

    await page.getByRole("button", { name: "Titelaspekt hinzufügen" }).click();
    // eslint-disable-next-line playwright/no-raw-locators
    await page
      .getByRole("group", { name: "Titelaspekt" })
      .locator("input")
      .fill("Gemeinsamer Bundesausschuss");
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Titelaspekt" }).locator("input").press("Enter");

    // Gliederung
    await page.getByTestId("Gliederung Editor").click();
    await page.keyboard.insertText("Gliederung: Neuer Text");

    // Inhaltliche Erschließung
    await page.getByRole("button", { name: "Schlagwörter hinzufügen" }).click();
    await page.getByTestId("Schlagwörter_ListInputEdit").click();
    await page.getByTestId("Schlagwörter_ListInputEdit").fill("Schlagwort 1");
    await page
      .getByRole("button", {
        name: "Schlagwörter übernehmen",
      })
      .click();

    // Sachgebiete
    await page.getByRole("button", { name: "Sachgebiete hinzufügen" }).click();
    await page.getByLabel("Sachgebietsuche auswählen").click();
    await page.getByRole("button", { name: "Alle Sachgebiete aufklappen" }).click();
    await page.getByRole("button", { name: "Phantasierecht aufklappen" }).click();
    await page.getByLabel("PR-05 Beendigung der Phantasieverhältnisse hinzufügen").click();

    // Normenkette
    await page
      .getByTestId("normReferences")
      .getByRole("combobox", { name: "RIS-Abkürzung" })
      .click();
    await page.getByText("KVLG").click();
    await page.getByRole("textbox", { name: "Einzelnorm der Norm" }).fill("§ 2");
    await page.getByRole("button", { name: "Norm speichern" }).click();
    await page.getByTestId("list-entry-0").click();
    await page.getByRole("textbox", { name: "Fassungsdatum" }).fill("27.01.2025");
    await page.getByRole("textbox", { name: "Jahr" }).fill("2025");
    await page.getByRole("button", { name: "Weitere Einzelnorm" }).click();
    await page.getByRole("textbox", { name: "Einzelnorm der Norm" }).nth(1).fill("§ 3");
    await page.getByRole("button", { name: "Norm speichern" }).click();

    // Verweise
    await page
      .getByTestId("activeReferences") // Norm
      .getByRole("combobox", { name: "Art der Verweisung" })
      .click();
    await page.getByRole("option", { name: "Anwendung" }).click();
    await page
      .getByTestId("activeReferences")
      .getByRole("combobox", { name: "RIS-Abkürzung" })
      .click();
    await page.getByRole("option", { name: "SGB 5" }).click();
    await page.getByRole("textbox", { name: "Fassungsdatum der Norm" }).click();
    await page.getByRole("textbox", { name: "Fassungsdatum der Norm" }).fill("12.12.2024");
    await page.getByLabel("Verweis speichern").click();

    await page.getByRole("radio", { name: "Verwaltungsvorschrift auswä" }).click(); // verwaltungsvorschrift
    await page
      .getByTestId("activeReferences")
      .getByRole("combobox", { name: "Art der Verweisung" })
      .click();
    await page.getByRole("option", { name: "Anwendung" }).click();
    await page
      .getByTestId("activeReferences")
      .getByRole("combobox", { name: "RIS-Abkürzung" })
      .click();
    await page.getByRole("option", { name: "KVLG" }).click();
    await page.getByRole("textbox", { name: "Fassungsdatum" }).click();
    await page.getByRole("textbox", { name: "Fassungsdatum" }).fill("12.12.2024");
    await page.getByLabel("Verweis speichern").click();

    // aktivzitierung rechtsprechung
    await page.getByText("Art der Zitierung *").click();
    await page.getByText("Ablehnung").click();
    await page.getByRole("combobox", { name: "Gericht Aktivzitierung" }).click();
    await page.getByText("AG Aachen").click();
    await page.getByText("Entscheidungsdatum *").fill("15.01.2025");
    await page.getByText("Aktenzeichen *").fill("Az1");
    await page.getByRole("button", { name: "Aktivzitierung speichern" }).click();

    // Berufsbild
    await page.getByRole("button", { name: "Berufsbild hinzufügen" }).click();
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Berufsbild" }).locator("input").fill("Brillenschleifer");
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Berufsbild" }).locator("input").press("Enter");

    // Definition
    await page.getByRole("button", { name: "Definition hinzufügen" }).click();
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Definition" }).locator("input").fill("Sachgesamtheit");
    // eslint-disable-next-line playwright/no-raw-locators
    await page.getByRole("group", { name: "Definition" }).locator("input").press("Enter");

    // Kurzreferat
    await page.getByTestId("Kurzreferat Editor").click();
    await page.keyboard.insertText("Kurzreferat Eintrag 123");

    // when
    await page.getByText("Abgabe").click();
    // then
    await expect(page.getByText("Alle Pflichtfelder sind korrekt ausgefüllt.")).toBeVisible();

    // when
    await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
    // then
    await expect(page.getByText("Freigabe ist abgeschlossen.")).toBeVisible();
  });
});
