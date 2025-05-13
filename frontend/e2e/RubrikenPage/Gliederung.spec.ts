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

  test(
    'Text changes can be undone and redone using the UI elements',
    { tag: ['@RISDEV-7843'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const kurzreferatEditorElement = page.getByTestId('Gliederung Editor')
      await kurzreferatEditorElement.click()
      await page.keyboard.insertText('Gliederung: Neuer Text')

      // when
      const undoButton = page
        .getByLabel('Gliederung Button Leiste')
        .getByRole('button', { name: 'Rückgängig machen' })
      undoButton.click()
      // then
      await expect(page.getByText('Gliederung: Neuer Text')).toHaveCount(0)

      // when
      const redoButton = page
        .getByLabel('Gliederung Button Leiste')
        .getByRole('button', { name: 'Wiederherstellen' })
      redoButton.click()
      // then
      await expect(page.getByText('Gliederung: Neuer Text')).toHaveCount(1)
    },
  )
})
