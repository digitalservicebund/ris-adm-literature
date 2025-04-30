import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Kurzreferat', () => {
  test(
    'Kurzreferat can be entered and persists a reload',
    { tag: ['@RISDEV-6047', '@RISDEV-6310'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const kurzReferatTitleElement = page.getByText('Kurzreferat')
      await expect(kurzReferatTitleElement).toHaveCount(3)
      const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
      await expect(kurzreferatEditorElement).toHaveCount(1)

      // when
      await kurzreferatEditorElement.click()
      await page.keyboard.insertText('Kurzreferat Eintrag 123')
      // then
      await expect(page.getByText('Kurzreferat Eintrag 123')).toHaveCount(1)

      // when
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.reload()
      // then
      await expect(page.getByText('Kurzreferat Eintrag 123')).toHaveCount(1)
    },
  ),
    test(
      'Text changes can be undone and redone using the UI elements',
      { tag: ['@RISDEV-7668'] },
      async ({ page }) => {
        // given
        await page.goto('/')
        await page.getByText('Neue Dokumentationseinheit').click()
        await page.getByText('Rubriken').click()

        const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
        await expect(kurzreferatEditorElement).toHaveCount(1)
        await kurzreferatEditorElement.click()
        await page.keyboard.insertText('Kurzreferat: Neuer Text')

        // when
        const undoButton = page
          .getByLabel('Kurzreferat Button Leiste')
          .getByRole('button', { name: 'Rückgängig machen' })
        undoButton.click()
        // then
        await expect(page.getByText('Kurzreferat: Neuer Text')).toHaveCount(0)

        // when
        // then
        await expect(page.getByText('Kurzreferat: Neuer Text')).toHaveCount(1)
      },
    )
})
