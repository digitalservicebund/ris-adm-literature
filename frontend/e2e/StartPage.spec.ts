import { test, expect } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test('Visiting the app root url, it shows "Rechtsinformationen" and user data', async ({ page }) => {
  await page.goto('/')
  const targetHeaderElement = page.getByText('Rechtsinformationen')
  const innerText = await targetHeaderElement.innerText()
  expect(innerText).toContain('Rechtsinformationen')
  // TODO RISDEV-6041
  // expect(innerText).toContain('Vorname Nachname')
  // expect(innerText).toContain('BSG')
  // Expect user icon
  // const targetContent = page.getByText('Übersicht')
  // const innerContentText = await targetContent.innerText()
  // expect(innerContentText).toContain('Übersicht Verwaltungsvorschriften')
  // const targetButton = targetContent.getByText("Dokumentationseinheit")
  // const targetButtonText = await targetButton.innerText()
  // expect(targetButtonText).toContain('Neue Dokumentationseinheit')
})
