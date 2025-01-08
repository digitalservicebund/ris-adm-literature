import { test, expect } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test('Visiting the app root url, it shows the title "Rechtsinformationen [...]", an icon and user data', async ({
  page,
}) => {
  await page.goto('/')
  expect(await page.getByText('Rechtsinformationen').innerText()).toContain('DES BUNDES')
  // user icon
  await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
  expect(await page.getByText('Vorname').innerText()).toContain('Nachname')
  await expect(page.getByText('BSG')).toHaveCount(1)
  // TODO RISDEV-6041
  // const targetContent = page.getByText('Übersicht')
  // const innerContentText = await targetContent.innerText()
  // expect(innerContentText).toContain('Übersicht Verwaltungsvorschriften')
  // const targetButton = targetContent.getByText("Dokumentationseinheit")
  // const targetButtonText = await targetButton.innerText()
  // expect(targetButtonText).toContain('Neue Dokumentationseinheit')
})
