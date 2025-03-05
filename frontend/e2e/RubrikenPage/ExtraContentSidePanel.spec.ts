import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - ExtraContentSidePanel', () => {
  test('Note persist during reload when saved', { tag: ['@RISDEV-6446'] }, async ({ page }) => {
    // given
    await page.goto('/')
    await page.getByText('Neue Dokumentationseinheit').click()
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    await page.getByRole('button', { name: 'Seitenpanel öffnen' }).click()
    await expect(page.getByRole('textbox', { name: 'Notiz Eingabefeld' })).toHaveCount(1)

    // when
    await page
      .getByRole('textbox', { name: 'Notiz Eingabefeld' })
      .fill('Ein relativ langer Text am Rande')
    await page.getByText('Speichern').click()
    await page.reload()

    // then
    await expect(page.getByRole('textbox', { name: 'Notiz Eingabefeld' })).toHaveValue(
      'Ein relativ langer Text am Rande',
    )
  })

  test('Fill in long note and expect resizing of text area', { tag: ['@RISDEV-6446'] }, async ({ page }) => {
    // given
    await page.goto('/')
    await page.getByText('Neue Dokumentationseinheit').click()
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    await page.getByRole('button', { name: 'Seitenpanel öffnen' }).click()
    await expect(page.getByRole('textbox', { name: 'Notiz Eingabefeld' })).toHaveCount(1)
    const longNote = `Dies ist ein sehr langer Text.

    Er enthält auch noch Zeilenumbrüche.

    Umbrüche sind ein ständiger Begleiter im Leben.

    Zeilen konkurrieren mit Spalten.

    Felsspalten sind gefährlich und sollten gemieden werden.`

    // when
    await page
      .getByRole('textbox', { name: 'Notiz Eingabefeld' })
      .fill(longNote)

    // then
    await expect(page.getByRole('textbox', { name: 'Notiz Eingabefeld' }))
      // Height should be set to a three-digit number
      .toHaveCSS('height', /[1-9]{3}px/)
  })
})
