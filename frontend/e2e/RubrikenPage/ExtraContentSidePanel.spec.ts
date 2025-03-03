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

    // when
    await page.getByText('Speichern').click()
    await page.reload()

    // then
    await expect(page.getByRole('textbox', { name: 'Notiz Eingabefeld' })).toHaveValue(
      'Ein relativ langer Text am Rande',
    )
  })
})
