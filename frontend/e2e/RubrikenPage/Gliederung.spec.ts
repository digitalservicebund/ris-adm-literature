import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Gliederung', () => {
  test(
    'Gliederung can be entered and persists a reload',
    { tag: ['@RISDEV-6047', '@RISDEV-6304'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const gliederungEditor = page.getByTestId('Gliederung Editor')
      await expect(gliederungEditor).toHaveCount(1)

      // when
      await gliederungEditor.click()
      await page.keyboard.insertText('Test 123')
      // then
      await expect(page.getByText('Test 123')).toHaveCount(1)

      // when
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.reload()
      // then
      await expect(page.getByText('Test 123')).toHaveCount(1)
    },
  )
})
