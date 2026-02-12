import { test, expect, type Page } from "@playwright/test";

export const getUliAktivzitierungSection = (page: Page) =>
  page.getByRole("region", { name: "Aktivzitierung (unselbst. Literatur)" });

export async function createUliDocument(
  page: Page,
  data: { title: string; year: string; docType: string[] },
  publish: boolean,
) {
  await page.goto("/literatur-unselbstaendig");
  await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
  await page.waitForURL(/dokumentationseinheit/);

  const formaldaten = page.getByRole("region", { name: "Formaldaten" });

  const input = formaldaten.getByRole("combobox", { name: "Dokumenttyp" });
  const overlay = page.getByRole("listbox", { name: "Optionsliste" });

  for (const type of data.docType) {
    await input.fill(type);
    await overlay.getByRole("option", { name: type }).click();
  }

  await formaldaten.getByRole("textbox", { name: "Dokumentarischer Titel" }).fill(data.title);
  await formaldaten.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill(data.year);

  await page.getByRole("button", { name: "Speichern" }).click();
  if (publish) {
    await page.getByText("Abgabe").click();
    await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
  }
}

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

      await createUliDocument(
        page,
        {
          title: titleDoc1,
          year: veroeffentlichungsjahrDoc1,
          docType: ["Auf"],
        },
        true,
      );

      await createUliDocument(
        page,
        {
          title: titleDoc2,
          year: veroeffentlichungsjahrDoc2,
          docType: ["Ebs"],
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
      const aktiv = getUliAktivzitierungSection(page);

      const nonExistingDocNumber = crypto.randomUUID();
      await aktiv.getByRole("textbox", { name: "Dokumentnummer" }).fill(nonExistingDocNumber);
      await aktiv.getByRole("button", { name: "Dokumente Suchen" }).click();

      await expect(aktiv.getByText("Keine Suchergebnisse gefunden")).toBeVisible();
    });
  },
);
