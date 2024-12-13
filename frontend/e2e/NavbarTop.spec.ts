import { test, expect } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test('Visiting the app root url, it shows "Rechtsinformationen"', async ({ page }) => {
  await page.goto('/')
  const targetTextElement = page.getByText('Rechtsinformationen')
  const innerText = await targetTextElement.innerText()
  expect(innerText).toContain('Rechtsinformationen')
})
