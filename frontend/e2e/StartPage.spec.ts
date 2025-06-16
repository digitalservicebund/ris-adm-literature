import { test, expect } from '@playwright/test'
import testData from './test-data.json' with { type: 'json' }

// See here how to get started:
// https://playwright.dev/docs/intro

test.describe('StartPage', () => {
  test(
    'Visiting the app root url, it shows the title "Rechtsinformationen [...]", an icon and user data',
    { tag: ['@RISDEV-6041'] },
    async ({ page }) => {
      // Arrange
      await page.route('/api/documentation-units', async (route) => {
        await route.fulfill({
          json: {
            id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
            documentNumber: 'KSNR054920707',
          },
        })
      })

      // Action
      await page.goto('/')

      // Assert
      await expect(page.getByText('Rechtsinformationen')).toBeVisible()
      await expect(page.getByText('des Bundes')).toBeVisible()
      // user icon
      await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
      await expect(page.getByText('Vorname Nachname')).toBeVisible()
      await expect(page.getByText('BSG')).toHaveCount(1)
      await expect(page.getByText('Übersicht Verwaltungsvorschriften')).toHaveCount(1)
      await expect(page.getByText('Neue Dokumentationseinheit')).toHaveCount(1)
    },
  )

  test(
    'clicking "Neue Dokumentationseinheit", routes to a new documentation unit',
    { tag: ['@RISDEV-6041'] },
    async ({ page }) => {
      // Arrange
      await page.route('/api/documentation-units', async (route) => {
        await route.fulfill({
          json: {
            id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
            documentNumber: 'KSNR054920707',
          },
        })
      })

      // Action
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()

      // Assert
      // this needs to change when KSNR are generated dynamically
      await expect(page).toHaveURL('/documentUnit/KSNR054920707/fundstellen')
    },
  )
  test(
    'Creates 2 documentation units and expects at least 2 on start page',
    { tag: ['@RISDEV-7787'] },
    async ({ page }) => {
      // given

      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      const documentNumber1 = page
        .url()
        .split('/')
        .filter((urlPart) => urlPart.startsWith('KSNR'))[0]
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      const documentNumber2 = page
        .url()
        .split('/')
        .filter((urlPart) => urlPart.startsWith('KSNR'))[0]
      await page.goto('/')

      // then
      await expect(page.getByText(documentNumber1)).toHaveCount(1)
      await expect(page.getByText(documentNumber2)).toHaveCount(1)
    },
  )
})

test.describe('Search documentation units', () => {
  test(
    'should filter results by its "Dokumentnummer"',
    { tag: ['@RISDEV-7600'] },
    async ({ page }) => {
      await page.goto('/')

      // Action: Use data from the imported JSON
      await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
      await page.getByRole('button', { name: 'Ergebnisse zeigen' }).click()

      // Assert
      await expect(page.getByText(testData.docNumber1)).toBeVisible()
      await expect(page.getByText(testData.docNumber2)).toBeHidden()
    },
  )

  test('should filter by "Amtl. Langüberschrift"', { tag: ['@RISDEV-7948'] }, async ({ page }) => {
    await page.goto('/')

    // Action: Use data from the imported JSON
    await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc2Title)
    await page.getByRole('button', { name: 'Ergebnisse zeigen' }).click()

    // Assert
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
  })

  test('should filter by "Zitierdatum"', { tag: ['@RISDEV-7949'] }, async ({ page }) => {
    await page.goto('/')

    // Action: Use data that corresponds to doc2
    await page.getByLabel('Zitierdatum').fill('18.06.2024')
    await page.getByRole('button', { name: 'Ergebnisse zeigen' }).click()

    // Assert
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
  })

  test('should filter by "Fundstelle"', { tag: ['@RISDEV-7950'] }, async ({ page }) => {
    await page.goto('/')

    // Action: Use data that corresponds to doc1
    await page.getByLabel('Fundstelle').fill('BGB 123')
    await page.getByRole('button', { name: 'Ergebnisse zeigen' }).click()

    // Assert
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
  })

  test('should filter by a combination of all fields', async ({ page }) => {
    await page.goto('/')

    // Action: Use data from the imported JSON
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
    await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc1Title)
    await page.getByLabel('Fundstelle').fill('BGB 123')
    await page.getByLabel('Zitierdatum').fill('17.06.2024')
    await page.getByRole('button', { name: 'Ergebnisse zeigen' }).click()

    // Assert
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
  })
})
