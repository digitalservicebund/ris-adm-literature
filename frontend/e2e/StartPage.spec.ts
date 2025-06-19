import { test, expect } from '@playwright/test'
import testData from './test-data.json' with { type: 'json' }
import messages from '../src/i18n/messages.json' with { type: 'json' }

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
      await page.getByLabel('Dokumentnummer').fill(documentNumber1)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()
      await expect(page.getByText(documentNumber1)).toHaveCount(1)

      await page.getByLabel('Dokumentnummer').fill(documentNumber2)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()
      await expect(page.getByText(documentNumber2)).toHaveCount(1)
    },
  )

  test(
    'Counts 100 entries in the list of documentation units.',
    { tag: ['@RISDEV-7601'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      // when
      // then
      await expect(page.getByText('KSNR')).toHaveCount(100)
    },
  )

  test(
    'Switches over to the second page of search entries, finds at least one other entry and switches back.',
    { tag: ['@RISDEV-7601'] },
    async ({ page }) => {
      // given
      await page.goto('/')

      // when
      await page.getByRole('button', { name: 'Weiter', exact: true }).click()

      // then
      expect(await page.getByText('KSNR').count()).toBeGreaterThanOrEqual(1)

      // when
      await page.getByRole('button', { name: 'Zurück', exact: true }).click()

      // then
      await expect(page.getByRole('button', { name: 'Zurück', exact: true })).toHaveCount(0)
    },
  )

  test(
    'Start editing documentation unit from the search results',
    { tag: ['@RISDEV-7601'] },
    async ({ page }) => {
      // given
      await page.goto('/')

      // when
      await page
        .getByRole('button', {
          name: /Dokument KSNR\d+ editieren/i,
        })
        .first()
        .click()

      // then
      await expect(page.getByRole('button', { name: 'Speichern', exact: true })).toHaveCount(1)
    },
  )

  test(
    'Add a document, check if is indexed, change it, check if re-indexed.',
    { tag: ['@RISDEV-7601'] },
    async ({ page }) => {
      // given
      await page.goto('/')

      // when
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/documentUnit/)
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      const documentNumber = page
        .url()
        .split('/')
        .filter((urlPart) => urlPart.startsWith('KSNR'))[0]
      await page.getByText('Rubriken').click()
      const langue = page.getByRole('textbox', { name: 'Amtl. Langüberschrift' })
      const text1 = 'reindextest' + Date.now().toString()
      await langue.fill(text1)
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.goto('/')

      // then
      await expect(page.getByText(text1)).toHaveCount(1)

      // when
      await page
        .getByRole('button', {
          name: 'Dokument ' + documentNumber + ' editieren',
        })
        .click()
      await page.getByText('Rubriken').click()
      const text2 = 'reindextest' + Date.now().toString()
      await langue.fill(text2)
      await page.getByRole('button', { name: 'Speichern', exact: true }).click()
      await page.goto('/')

      // then
      await expect(page.getByText(text2)).toHaveCount(1)
    },
  )
})

test.describe('Search documentation units', () => {
  test(
    'should filter results by its "Dokumentnummer"',
    { tag: ['@RISDEV-7600'] },
    async ({ page }) => {
      await page.goto('/')

      // AC 1 initial state
      await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
      await expect(
        page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
      ).toBeVisible()

      // AC 2 full match search
      await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert whole documentNumber
      await expect(page.getByText(testData.docNumber1)).toBeVisible()
      await expect(page.getByText(testData.docNumber2)).toBeHidden()

      // AC 4 reset search
      await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
      await page.getByRole('button', { name: 'Zurücksetzen' }).click()

      // Assert
      await expect(page.getByLabel('Dokumentnummer')).toBeEmpty()
      await expect(page.getByText(testData.docNumber1)).toBeVisible()
      await expect(page.getByText(testData.docNumber2)).toBeVisible()
      await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()

      // AC 2 partial match search
      const partialDocNumberLeft = testData.docNumber1.substring(2)

      await page.getByLabel('Dokumentnummer').fill(partialDocNumberLeft)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert partial number remove chars right
      await expect(page.getByText(testData.docNumber1)).toBeVisible()
      await expect(page.getByText(testData.docNumber2)).toBeHidden()
      await page.getByRole('button', { name: 'Zurücksetzen' }).click()

      const partialDocNumberRight = testData.docNumber1.substring(0, testData.docNumber1.length - 2)
      await page.getByLabel('Dokumentnummer').fill(partialDocNumberRight)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert partial number remove chars left
      await expect(page.getByText(testData.docNumber1)).toBeVisible()
      await expect(page.getByText(testData.docNumber2)).toBeVisible()
      await page.getByRole('button', { name: 'Zurücksetzen' }).click()

      // AC 3 combined search
      // Action matching combination
      await page.getByLabel('Dokumentnummer').fill(testData.docNumber2)
      await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc2Title)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert matching combination
      await expect(page.getByText(testData.docNumber2)).toBeVisible()
      await expect(page.getByText(testData.docNumber1)).toBeHidden()

      // Action non-matching combination
      await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc1Title)
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert non-matching combination
      await expect(page.getByText(testData.docNumber1)).toBeHidden()
      await expect(page.getByText(testData.docNumber2)).toBeHidden()
      await page.getByRole('button', { name: 'Zurücksetzen' }).click()

      // AC 5 no results msg
      // Action
      await page.getByLabel('Dokumentnummer').fill('DUMMY-WERT-12345-ABCDE')
      await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

      // Assert
      await expect(page.getByText(testData.docNumber1)).toBeHidden()
      await expect(page.getByText(testData.docNumber2)).toBeHidden()
      await expect(page.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
    },
  )

  test('should filter by "Amtl. Langüberschrift"', { tag: ['@RISDEV-7948'] }, async ({ page }) => {
    await page.goto('/')

    // AC 1 initial state
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeVisible()

    // AC 2 search full match
    await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc1Title)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert whole title
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()

    // AC 4 reset search
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // Assert reset
    await expect(page.getByLabel('Amtl. Langüberschrift')).toBeEmpty()
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()

    // AC 2 test partial match
    const partialTitle = 'Global Setup'
    await page.getByLabel('Amtl. Langüberschrift').fill(partialTitle)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert partial match
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC 3 combined search
    await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc2Title)
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber2)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert matching combination
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByText(testData.docNumber1)).toBeHidden()

    // Action non-matching combination
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert non-matching combination
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC 5 no result msg
    await page.getByLabel('Amtl. Langüberschrift').fill('DUMMY-TITLE-XYZ-123')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert no results found message
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await expect(page.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
  })

  test('should filter by "Zitierdatum"', { tag: ['@RISDEV-7949'] }, async ({ page }) => {
    await page.goto('/')

    // AC 1 initial state
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeVisible()

    // AC 2 search full match
    await page.getByLabel('Zitierdatum').fill('18.06.2024')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByText(testData.docNumber1)).toBeHidden()

    // AC search second zitierdatum
    await page.getByLabel('Zitierdatum').fill('01.01.1950')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()

    // AC 4 reset search
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // Assert reset
    await expect(page.getByLabel('Zitierdatum')).toBeEmpty()
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()

    // AC 3 combined search
    await page.getByLabel('Zitierdatum').fill('17.06.2024')
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert matching combination
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()

    // Action non-matching combination
    await page.getByLabel('Zitierdatum').fill('18.06.2024')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert non-matching combination
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC 5 no result msg
    await page.getByLabel('Zitierdatum').fill('01.01.1999')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert no results found message
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await expect(page.getByText('Keine Suchergebnisse gefunden')).toBeVisible()

    // AC 6 invalid date
    // Test incomplete date
    await page.getByLabel('Zitierdatum').fill('12.12.')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()
    await expect(page.getByText('Unvollständiges Datum')).toBeVisible()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeDisabled()

    // Test invalid date
    await page.getByLabel('Zitierdatum').fill('99.99.2023')
    await expect(page.getByText('Kein valides Datum')).toBeVisible()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeDisabled()

    // Test future date
    await page.getByLabel('Zitierdatum').fill('31.12.2099')
    await expect(page.getByText('Das Datum darf nicht in der Zukunft liegen')).toBeVisible()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeDisabled()
  })

  test('should filter by "Fundstelle"', { tag: ['@RISDEV-7950'] }, async ({ page }) => {
    await page.goto('/')

    // AC 1 initial state
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
    await expect(
      page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }),
    ).toBeVisible()

    // AC 2 search full match
    await page.getByLabel('Fundstelle').fill('BGB 123')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert full match
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()

    // AC 4 reset search
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // Assert reset
    await expect(page.getByLabel('Fundstelle')).toBeEmpty()
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()

    // AC 2 partial match search
    // Assert partial match - left
    await page.getByLabel('Fundstelle').fill('BGB') // Should match both
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // Assert partial match - right
    await page.getByLabel('Fundstelle').fill('123')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC find second (not primary) fundstelle
    await page.getByLabel('Fundstelle').fill('VWV xyz')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC 3: Filter by matching combination
    await page.getByLabel('Fundstelle').fill('BGB 456')
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber2)
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert matching combination
    await expect(page.getByText(testData.docNumber2)).toBeVisible()
    await expect(page.getByText(testData.docNumber1)).toBeHidden()

    // Action non-matching combination
    await page.getByLabel('Fundstelle').fill('BGB 123')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert non-matching combination
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await page.getByRole('button', { name: 'Zurücksetzen' }).click()

    // AC 5 no result msg
    await page.getByLabel('Fundstelle').fill('NON-EXISTENT-REF-42')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert no results found message
    await expect(page.getByText(testData.docNumber1)).toBeHidden()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
    await expect(page.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
  })

  test('should filter by a combination of all fields', async ({ page }) => {
    await page.goto('/')

    // Action
    await page.getByLabel('Dokumentnummer').fill(testData.docNumber1)
    await page.getByLabel('Amtl. Langüberschrift').fill(testData.doc1Title)
    await page.getByLabel('Fundstelle').fill('BGB 123')
    await page.getByLabel('Zitierdatum').fill('17.06.2024')
    await page.getByRole('button', { name: messages.BTN_SHOW_SEARCH_RESULTS.message }).click()

    // Assert
    await expect(page.getByText(testData.docNumber1)).toBeVisible()
    await expect(page.getByText(testData.docNumber2)).toBeHidden()
  })
})
