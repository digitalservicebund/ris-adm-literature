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
  )

  test(
    'Text changes can be undone and redone using the UI elements',
    { tag: ['@RISDEV-7668'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
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
      const redoButton = page
        .getByLabel('Kurzreferat Button Leiste')
        .getByRole('button', { name: 'Wiederherstellen' })
      redoButton.click()
      // then
      await expect(page.getByText('Kurzreferat: Neuer Text')).toHaveCount(1)
    },
  )

  test(
    'Text box can be expanded, shows line 1 again after expansion',
    { tag: ['@RISDEV-7666]'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
      await kurzreferatEditorElement.click()
      await page.keyboard.type('Kurzreferat: Neuer Text 1\n Line 2\n Line 3\n Line 4\n Line 5')

      const text1 = page.getByText('Text 1')
      await expect(text1).not.toBeInViewport()

      // when
      const expansionButton = page
        .getByLabel('Kurzreferat Button Leiste')
        .getByRole('button', { name: 'Erweitern' })
      expansionButton.click()

      // then
      await expect(text1).toBeInViewport()
    },
  )

  test(
    'Hide unprintable characters after clicking "Nicht-druckbare Zeichen"',
    { tag: ['@RISDEV-7667]'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()

      const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
      await kurzreferatEditorElement.click()
      await page.keyboard.type('Kurzreferat: Neuer Text 1')

      // eslint-disable-next-line playwright/no-raw-locators
      const linebreakIndicator = kurzreferatEditorElement.locator('br')
      await expect(linebreakIndicator).toHaveCount(1)

      // when
      const expansionButton = page
        .getByLabel('Kurzreferat Button Leiste')
        .getByRole('button', { name: 'Nicht-druckbare Zeichen' })
      expansionButton.click()

      // then
      await expect(linebreakIndicator).toHaveCount(0)
    },
  )
})
