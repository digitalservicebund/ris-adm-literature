import { test, expect, type Page } from "@playwright/test";

export const getRsAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (Rechtsprechung)" });

test.describe("SLI Rubriken – Aktivzitierung Rechtsprechung", { tag: ["@RISDEV-10741"] }, () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/literatur-selbstaendig");
    await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
    await page.waitForURL(/dokumentationseinheit/);
  });

  test(
    "manual Rechtsprechung aktivzitierung entry can be created and persists after save + reload",
    { tag: ["@RISDEV-10741"] },
    async ({ page }) => {
      const aktiv = getRsAktivzitierungSection(page);

      await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
      const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
      await expect(citationOptions).toBeVisible();
      await citationOptions.getByRole("option").first().click();

      await aktiv.getByRole("combobox", { name: "Gericht" }).click();
      await page.getByRole("option", { name: "AG Aachen" }).click();

      const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
      await dateInput.fill("01.01.2026");

      const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await aktenzeichenInput.fill("AZ-123-!?#");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("AG Aachen", { exact: false })).toBeVisible();
      await expect(aktivList.getByText("AZ-123-!?#", { exact: false })).toBeVisible();

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getRsAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });

      await expect(aktivListAfterReload.getByText("AG Aachen", { exact: false })).toBeVisible();
      await expect(aktivListAfterReload.getByText("AZ-123-!?#", { exact: false })).toBeVisible();
    },
  );

  test("shows validation error when citation type is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
    await aktenzeichenInput.fill("AZ-123");

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("01.01.2026");

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    const citationTypeInput = aktiv.getByRole("combobox", { name: "Art der Zitierung" });
    await citationTypeInput.click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await expect(citationOptions).toBeVisible();
    await citationOptions.getByRole("option").first().click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("shows validation error when Gericht is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await expect(citationOptions).toBeVisible();
    await citationOptions.getByRole("option").first().click();

    const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
    await aktenzeichenInput.fill("AZ-123");

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("01.01.2026");

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test("shows validation error when Aktenzeichen is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await expect(citationOptions).toBeVisible();
    await citationOptions.getByRole("option").first().click();

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("01.01.2026");

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
    await aktenzeichenInput.fill("AZ-123");

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });

  test(
    "Rechtsprechung manual entry can be edited, cancelled, deleted and changes persist",
    { tag: ["@RISDEV-10741"] },
    async ({ page }) => {
      const aktiv = getRsAktivzitierungSection(page);

      await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
      const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
      await citationOptions.getByRole("option").first().click();

      await aktiv.getByRole("combobox", { name: "Gericht" }).click();
      await page.getByRole("option", { name: "AG Aachen" }).click();

      const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
      await dateInput.fill("01.01.2026");

      const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await aktenzeichenInput.fill("AZ 123");

      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
      await expect(aktivList.getByText("AZ 123", { exact: false })).toBeVisible();

      await aktiv.getByRole("button", { name: "Eintrag bearbeiten" }).click();
      const editAktenzeichen = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
      await editAktenzeichen.fill("AZ 999");
      await aktiv.getByRole("button", { name: "Übernehmen" }).click();

      await expect(aktivList.getByText("AZ 999", { exact: false })).toBeVisible();
      await expect(aktivList.getByText("AZ 123", { exact: false })).toHaveCount(0);

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterReload = getRsAktivzitierungSection(page);
      const aktivListAfterReload = aktivAfterReload.getByRole("list", {
        name: "Aktivzitierung Liste",
      });
      await expect(aktivListAfterReload.getByText("AZ 999", { exact: false })).toBeVisible();

      await aktivAfterReload.getByRole("button", { name: "Eintrag bearbeiten" }).click();
      const editAktenzeichen2 = aktivAfterReload.getByRole("textbox", { name: "Aktenzeichen" });
      await editAktenzeichen2.fill("AZ CANCELLED");
      await aktivAfterReload.getByRole("button", { name: "Abbrechen" }).click();

      await expect(aktivListAfterReload.getByText("AZ 999", { exact: false })).toBeVisible();
      await expect(aktivAfterReload.getByText("AZ CANCELLED")).toHaveCount(0);

      await aktivAfterReload.getByRole("button", { name: "Eintrag bearbeiten" }).click();
      await aktivAfterReload.getByRole("button", { name: "Eintrag löschen" }).click();
      await expect(aktivAfterReload.getByText("AZ 999", { exact: false })).toHaveCount(0);

      await page.getByRole("button", { name: "Speichern" }).click();
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();
      await page.reload();

      const aktivAfterSecondReload = getRsAktivzitierungSection(page);
      await expect(aktivAfterSecondReload.getByText("AZ 999", { exact: false })).toHaveCount(0);
    },
  );

  test("Rechtsprechung date validation shows error for 11.11.201", async ({ page }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await citationOptions.getByRole("option").first().click();

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("11.11.201");
    await dateInput.blur();

    const dateError = aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/);
    await expect(dateError).toBeVisible();

    const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
    await expect(saveButton).toBeEnabled();
    await saveButton.click();

    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);
  });

  test("Rechtsprechung date validation shows error for 21.21.2121", async ({ page }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await citationOptions.getByRole("option").first().click();

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("21.21.2121");
    await dateInput.blur();

    const dateError = aktiv.getByText(/Kein valides Datum|Unvollständiges Datum/);
    await expect(dateError).toBeVisible();

    const saveButton = aktiv.getByRole("button", { name: "Übernehmen" });
    await expect(saveButton).toBeEnabled();
    await saveButton.click();

    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);
  });

  test("shows validation error when Entscheidungsdatum is missing, clears after fill, then allows add", async ({
    page,
  }) => {
    const aktiv = getRsAktivzitierungSection(page);

    await aktiv.getByRole("combobox", { name: "Art der Zitierung" }).click();
    const citationOptions = page.getByRole("listbox", { name: "Optionsliste" });
    await expect(citationOptions).toBeVisible();
    await citationOptions.getByRole("option").first().click();

    await aktiv.getByRole("combobox", { name: "Gericht" }).click();
    await page.getByRole("option", { name: "AG Aachen" }).click();

    const aktenzeichenInput = aktiv.getByRole("textbox", { name: "Aktenzeichen" });
    await aktenzeichenInput.fill("AZ-123");

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).toBeVisible();
    const aktivList = aktiv.getByRole("list", { name: "Aktivzitierung Liste" });
    await expect(aktivList.getByRole("listitem")).toHaveCount(0);

    const dateInput = aktiv.getByRole("textbox", { name: "Entscheidungsdatum" });
    await dateInput.fill("01.01.2026");

    await expect(aktiv.getByText("Pflichtfeld nicht befüllt")).not.toBeVisible();

    await aktiv.getByRole("button", { name: "Übernehmen" }).click();

    await expect(aktivList.getByRole("listitem")).toHaveCount(1);
  });
});
