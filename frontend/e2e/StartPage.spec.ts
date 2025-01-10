import { test, expect } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test('Visiting the app root url, it shows the title "Rechtsinformationen [...]", an icon and user data',
  { tag: ['@RISDEV-6041']}, async ({
  page,
}) => {
  await page.goto('/')
  expect(await page.getByText('Rechtsinformationen').innerText()).toContain('DES BUNDES')
  // user icon
  await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
  expect(await page.getByText('Vorname').innerText()).toContain('Nachname')
  await expect(page.getByText('BSG')).toHaveCount(1)
  await expect(page.getByText('Übersicht Verwaltungsvorschriften')).toHaveCount(1)
  await expect(page.getByRole('button', { name: 'Neue Dokumentationseinheit' })).toHaveCount(1)
})
