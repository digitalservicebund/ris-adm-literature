import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Verweise: Norm', () => {
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

  test('Add a norm, edit and save', { tag: ['@RISDEV-6075'] }, async ({ page }) => {
    // given
    await page.goto('/documentUnit/KSNR054920707/fundstellen')
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    await expect(
      page.getByTestId('normReferences').getByRole('textbox', { name: 'RIS-Abkürzung' }),
    ).toHaveCount(1)

    // when
    await page.getByTestId('normReferences').getByRole('textbox', { name: 'RIS-Abkürzung' }).click()
    await expect(page.getByText('KVLG')).toBeVisible()
    await page.getByText('KVLG').click()
    await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('§ 2')
    await page.getByRole('button', { name: 'Norm speichern' }).click()
    await page.getByTestId('list-entry-0').click()
    await page.getByRole('textbox', { name: 'Fassungsdatum' }).fill('27.01.2025')
    await page.getByRole('button', { name: 'Norm speichern' }).click()

    // then
    await expect(page.getByText('KVLG, § 2, 27.01.2025')).toHaveCount(1)
  })

  test('Add two norms, delete the first item', { tag: ['@RISDEV-6075'] }, async ({ page }) => {
    // given
    await page.goto('/documentUnit/KSNR054920707/fundstellen')
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    await expect(
      page.getByTestId('normReferences').getByRole('textbox', { name: 'RIS-Abkürzung' }),
    ).toHaveCount(1)

    // when
    await page.getByTestId('normReferences').getByRole('textbox', { name: 'RIS-Abkürzung' }).click()
    await expect(page.getByText('SGB 5')).toBeVisible()
    await page.getByText('SGB 5').click()
    await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('1991, Seite 92')
    await page.getByRole('button', { name: 'Norm speichern' }).click()
    await page.getByTestId('normReferences').getByRole('textbox', { name: 'RIS-Abkürzung' }).click()
    await expect(page.getByText('KVLG')).toBeVisible()
    await page.getByText('KVLG').click()
    await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('§ 2')
    await page.getByRole('button', { name: 'Norm speichern' }).click()
    await page.getByTestId('list-entry-0').click()
    await page.getByText('Eintrag löschen').click()

    // then
    await expect(page.getByText('SGB 5, 1991, Seite 92')).toHaveCount(0)
    await expect(page.getByText('KVLG, § 2')).toHaveCount(1)
  })
})
