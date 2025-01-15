import { test, expect } from '@playwright/test'

test(
  'Visiting the Rubriken step of creating a documentUnit',
  { tag: ['@RISDEV-6043'] },
  async ({ page }) => {
    // In the future the new documentUnit needs to be mocked
    // As this functionality is not there yet we can simply enter the desired page and finish the process
    await page.goto('/documentUnit/KSNR054920707/rubriken')
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    await expect(page.getByText('Formaldaten')).toHaveCount(2)
  },
)
