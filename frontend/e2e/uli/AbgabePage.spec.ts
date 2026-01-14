import { expect, Page, test } from "@playwright/test";

test.describe("ULI AbgabePage", () => {
  test.beforeEach(async ({ page }) => {
    // given
    await page.goto("/literatur-unselbstaendig");
    await page.getByRole("button", { name: "Neue Dokumentationseinheit" }).click();
  });

  test(
    "Should enter mandatory data, validate fine and publish successfully",
    { tag: ["@RISDEV-9375", "@RISDEV-9374"] },
    async ({ page }) => {
      // given
      const input = page.getByRole("combobox", { name: "Dokumenttyp" });
      await input.fill("Auf");
      await page.getByText("Aufsatz").click();
      await input.fill("Kon");
      await page.getByText("Kongressvortrag").click();
      await page
        .getByRole("textbox", { name: "Hauptsachtitel", exact: true })
        .fill("Die unendliche Verhandlung 123äöüß$%&");
      await page.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill("2025");
      await page.getByText("Speichern").click();

      // when
      await page.getByText("Abgabe").click();
      // then
      await expect(page.getByText("Alle Pflichtfelder sind korrekt ausgefüllt.")).toBeVisible();

      // when
      await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
      // then
      await expect(page.getByText("Freigabe ist abgeschlossen.")).toBeVisible();
    },
  );

  async function fillMandatoryFields(page: Page, missing: string[] = []) {
    const form = page.getByRole("region", { name: "Formaldaten" });

    if (!missing.includes("Dokumenttyp")) {
      const input = form.getByRole("combobox", { name: /^Dokumenttyp/ });
      await input.fill("Au");
      await page
        .getByRole("listbox", { name: "Optionsliste" })
        .getByRole("option", { name: "Auf" })
        .click();
    }

    if (!missing.includes("Hauptsachtitel")) {
      await form
        .getByRole("textbox", { name: /^Hauptsachtitel/ })
        .fill("Die unendliche Verhandlung");
    }

    if (!missing.includes("Veröffentlichungsjahr")) {
      await form.getByRole("textbox", { name: /^Veröffentlichungsjahr/ }).fill("2025");
    }

    await page.getByRole("button", { name: "Speichern" }).click();
  }

  const missingFields = [
    ["Dokumenttyp"],
    ["Hauptsachtitel"],
    ["Veröffentlichungsjahr"],
    ["Dokumenttyp", "Veröffentlichungsjahr"],
    ["Dokumenttyp", "Hauptsachtitel"],
    ["Hauptsachtitel", "Veröffentlichungsjahr"],
    ["Dokumenttyp", "Hauptsachtitel", "Veröffentlichungsjahr"],
  ];

  for (const fields of missingFields) {
    test(
      `Should show validation error when mandatory ${fields.join()} are missing, shows the required fields and a link to the rubriken page`,
      { tag: ["@RISDEV-9374"] },
      async ({ page }) => {
        await fillMandatoryFields(page, fields);

        await page.getByText("Abgabe").click();

        await expect(page.getByText("Folgende Pflichtfelder sind nicht befüllt:")).toBeVisible();
        for (const field of fields) {
          await expect(page.getByText(field)).toBeVisible();
        }
        const publishButton = page.getByRole("button", { name: "Zur Veröffentlichung freigeben" });
        await expect(publishButton).toBeDisabled();
        await expect(page.getByRole("button", { name: "Rubriken bearbeiten" })).toBeVisible();
        await page.getByRole("button", { name: "Rubriken bearbeiten" }).click();

        await expect(page.getByRole("heading", { name: "Formaldaten" })).toBeVisible();
      },
    );
  }

  test(
    "Should show validation error when mandatory fields contain whitespaces only",
    { tag: ["@RISDEV-10137"] },
    async ({ page }) => {
      // given
      const input = page.getByRole("combobox", { name: "Dokumenttyp" });
      await input.fill("Auf");
      await page.getByText("Aufsatz").click();
      await page.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill(" ");
      await page.getByRole("textbox", { name: "Hauptsachtitel", exact: true }).fill("     ");

      // when
      await page.getByText("Abgabe").click();

      // then
      await expect(page.getByText("Folgende Pflichtfelder sind nicht befüllt:")).toBeVisible();
      await expect(page.getByText("Veröffentlichungsjahr")).toBeVisible();
      await expect(page.getByText("Hauptsachtitel / Dokumentarischer Titel")).toBeVisible();
      await expect(
        page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }),
      ).toBeDisabled();
    },
  );

  test(
    "Should show links to the mandatory fields; when clicking on a link, navigates to the corresponding field in Rubriken page",
    { tag: ["@RISDEV-9374"] },
    async ({ page }) => {
      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Dokumenttyp" }).click();
      // then
      await expect(page.getByText("Dokumenttyp")).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Veröffentlichungsjahr" }).click();
      // then
      await expect(page.getByText("Veröffentlichungsjahr")).toBeInViewport();

      // when
      await page.getByText("Abgabe").click();
      await page.getByRole("link", { name: "Hauptsachtitel / Dokumentarischer Titel" }).click();
      // then
      await expect(page.getByText("Hauptsachtitel *", { exact: true })).toBeInViewport();
    },
  );

  test(
    "Should show a publication error on backend error 500",
    { tag: ["@RISDEV-9375"] },
    async ({ page }) => {
      // given
      const input = page.getByRole("combobox", { name: "Dokumenttyp" });
      await input.fill("Auf");
      await page.getByText("Aufsatz").click();
      await page
        .getByRole("textbox", { name: "Hauptsachtitel", exact: true })
        .fill(
          "Dieser Test simuliert einen Fehler im Backend; unabhängig davon, was wir hier eingeben.",
        );
      await page.getByRole("textbox", { name: "Veröffentlichungsjahr" }).fill("2025");
      await page.getByText("Speichern").click();
      await page.getByText("Abgabe").click();

      // mock an error 500 response on publishing
      await page.route("*/**/api/literature/uli/documentation-units/*/publish", async (route) => {
        const response = await route.fetch();
        await route.fulfill({ status: 500, response });
      });

      // when
      await page.getByRole("button", { name: "Zur Veröffentlichung freigeben" }).click();
      // then
      await expect(
        page.getByText("Die Freigabe ist aus technischen Gründen nicht durchgeführt worden."),
      ).toBeVisible();
    },
  );
});
