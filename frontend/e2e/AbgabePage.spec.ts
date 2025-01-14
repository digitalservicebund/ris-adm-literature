import { test, expect } from '@playwright/test'

test(
  'Visiting the Abgabe step of creating a documentUnit which displays a Button to end the process and leads to the StartPage',
  { tag: ['@RISDEV-6048'] },
  async ({ page }) => {
    // In the future the new documentUnit needs to be mocked
    // As this functionality is not there yet we can simply enter the desired page and finish the process
    await page.goto('/documentUnit/KSNR054920707/abgabe')
    await expect(page.getByText('Abgabe')).toHaveCount(2)
    await expect(page.getByText('Zur Veröffentlichung freigeben')).toHaveCount(1)
    await page.getByText('Zur Veröffentlichung freigeben').click()
    await expect(page).toHaveURL('') // Check if the user is redirected to the StartPage
  },
)
