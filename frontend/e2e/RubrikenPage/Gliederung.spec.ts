import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Gliederung', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/documentation-units/KSNR054920707', async (route) => {
      const json = {
        documentNumber: 'KSNR054920707',
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        json: null,
      }
      await route.fulfill({ json })
    })
  })

  test('Enter Gliederung', { tag: ['@RISDEV-6047'] }, async ({ page }) => {
    await page.goto('/documentUnit/KSNR054920707/fundstellen')
    await page.getByText('Rubriken').click()

    await expect(page.getByText('Rubriken')).toHaveCount(1)

    const gliederungEditor = page.getByTestId('Gliederung Editor')
    await expect(gliederungEditor).toHaveCount(1)
    await gliederungEditor.click()
    await page.keyboard.insertText('Test 123')
    await expect(page.getByText('Test 123')).toHaveCount(1)
  })
})
