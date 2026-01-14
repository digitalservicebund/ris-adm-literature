import { expect, test } from "@playwright/test";

test.describe("ULI Rubriken - titles", () => {
  test(
    `Hauptsachtitel: its value persists after saving and reloading,
    when filled, Dokumentarischer Titel is disabled`,
    { tag: ["@RISDEV-9867"] },
    async ({ page }) => {
      // when
      await page.goto("/");
      await page.getByText("Neue Dokumentationseinheit").click();
      await page.waitForURL(/dokumentationseinheit/);

      // then
      await expect(page.getByText("Hauptsachtitel *")).toBeVisible();

      // when
      const input = page.getByRole("textbox", { name: "Hauptsachtitel", exact: true });
      await input.fill("Die unendliche Verhandlung 123äöüß$%&");

      // then
      await expect(page.getByRole("textbox", { name: "Dokumentarischer Titel" })).toBeDisabled();

      // when
      await page.getByText("Speichern").click();

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when
      await page.reload();

      // then
      await expect(input).toHaveValue("Die unendliche Verhandlung 123äöüß$%&");

      // when
      await input.clear();

      // then
      await expect(page.getByRole("textbox", { name: "Dokumentarischer Titel" })).toBeEnabled();
    },
  );

  test(
    `Zusatz zum Hauptsachtitel: its value persists after saving and reloading,
     when filled, Dokumentarischer Titel is disabled`,
    { tag: ["@RISDEV-9868"] },
    async ({ page }) => {
      // when
      await page.goto("/");
      await page.getByText("Neue Dokumentationseinheit").click();
      await page.waitForURL(/dokumentationseinheit/);

      // then
      await expect(page.getByText("Zusatz zum Hauptsachtitel")).toBeVisible();

      // when
      const input = page.getByRole("textbox", { name: "Zusatz zum Hauptsachtitel" });
      await input.fill("Zusatz 123äöüß$%&");

      // then
      await expect(page.getByRole("textbox", { name: "Dokumentarischer Titel" })).toBeDisabled();

      // when
      await page.getByText("Speichern").click();

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when
      await page.reload();

      // then
      await expect(input).toHaveValue("Zusatz 123äöüß$%&");

      // when
      await input.clear();

      // then
      await expect(page.getByRole("textbox", { name: "Dokumentarischer Titel" })).toBeEnabled();
    },
  );

  test(
    `Dokumentarischer Titel: its value persists after saving and reloading,
    when filled, Hauptsachtitel and Zusatz zum Hauptsachtitel are disabled`,
    { tag: ["@RISDEV-9867"] },
    async ({ page }) => {
      // when
      await page.goto("/");
      await page.getByText("Neue Dokumentationseinheit").click();
      await page.waitForURL(/dokumentationseinheit/);

      // then
      await expect(page.getByText("Dokumentarischer Titel *")).toBeVisible();

      // when
      const input = page.getByRole("textbox", { name: "Dokumentarischer Titel" });
      await input.fill("Die Rückkehr der Akten 123äöüß$%&");

      // then
      await expect(
        page.getByRole("textbox", { name: "Hauptsachtitel", exact: true }),
      ).toBeDisabled();
      await expect(page.getByRole("textbox", { name: "Zusatz zum Hauptsachtitel" })).toBeDisabled();

      // when
      await page.getByText("Speichern").click();

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible();

      // when
      await page.reload();

      // then
      await expect(input).toHaveValue("Die Rückkehr der Akten 123äöüß$%&");

      // when
      await input.clear();

      // then
      await expect(
        page.getByRole("textbox", { name: "Hauptsachtitel", exact: true }),
      ).toBeEnabled();
      await expect(page.getByRole("textbox", { name: "Zusatz zum Hauptsachtitel" })).toBeEnabled();
    },
  );
});
