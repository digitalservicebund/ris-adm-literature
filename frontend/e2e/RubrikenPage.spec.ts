import { test, expect } from '@playwright/test'

test.describe('RubrikenPage', () => {
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

  test(
    'Visiting the Gliederung step of creating a documentUnit',
    { tag: ['@RISDEV-6047'] },
    async ({ page }) => {
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()

      await expect(page.getByText('Rubriken')).toHaveCount(1)

      const gliederungEditor = page.getByTestId('Gliederung Editor')
      await expect(gliederungEditor).toHaveCount(1)
      await gliederungEditor.click()
      await page.keyboard.insertText('Test 123')
      await expect(page.getByText('Test 123')).toHaveCount(1)
    },
  )

  test(
    'Visiting the Kurzreferat step of creating a documentUnit',
    { tag: ['@RISDEV-6047'] },
    async ({ page }) => {
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()

      await expect(page.getByText('Rubriken')).toHaveCount(1)

      const kurzReferatTitleElement = page.getByText('Kurzreferat')
      await expect(kurzReferatTitleElement).toHaveCount(3)

      const kurzreferatEditorElement = page.getByTestId('Kurzreferat Editor')
      await expect(kurzreferatEditorElement).toHaveCount(1)
      await kurzreferatEditorElement.click()
      await page.keyboard.insertText('Kurzreferat Eintrag 123')
      await expect(page.getByText('Kurzreferat Eintrag 123')).toHaveCount(1)
    },
  )

  test(
    'Opening Rubriken shows Sachgebiete heading and button',
    { tag: ['@RISDEV-6076'] },
    async ({ page }) => {
      // given, when
      await page.goto('/documentUnit/KSNR054920707/rubriken')

      // then
      await expect(page.getByRole('heading', { name: 'Sachgebiete' })).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Sachgebiete' })).toHaveCount(1)
    },
  )

  test(
    "We can add Sachgebiete via 'Direkteingabe' when clicking on the Sachgebiet button and entering + selecting an entry",
    { tag: ['@RISDEV-6076'] },
    async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/rubriken')
      await page.getByRole('button', { name: 'Sachgebiete' }).click()
      await page.getByRole('radio', { name: 'Direkteingabe' }).check()

      // when
      await page.getByRole('textbox', { name: 'Direkteingabe-' }).fill('Arbeitsr')
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Arbeitsrecht' })
        .click()

      // then
      await expect(page.getByText('ARArbeitsrecht')).toHaveCount(1)
    },
  )
})
