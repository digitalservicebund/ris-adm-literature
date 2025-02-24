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
    'Visiting the Schlagwörter step of creating a documentUnit',
    { tag: ['@RISDEV-6047'] },
    async ({ page }) => {
      await page.route('/api/documentation-units/KSNR054920707', async (route) => {
        const json = {
          documentNumber: 'KSNR054920707',
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          json: null,
        }
        await route.fulfill({ json })
      })
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()
      await expect(page.getByText('Rubriken')).toHaveCount(1)
      /////////////////
      // Schlagwörter
      /////////////////
      const schlagwoerterHeadingElement = page.getByText('Schlagwörter')
      await expect(schlagwoerterHeadingElement).toHaveCount(2) // two headings

      // enter single schlagwort, assert that it's visible after confirming
      const schlagwoerterListEditElement = page.getByTestId('Schlagwörter_ListInputEdit')
      await schlagwoerterListEditElement.click()
      await schlagwoerterListEditElement.fill('Schlagwort 1')
      const schlagwoerterUebernehmenElement = page.getByText('Übernehmen').first()
      await schlagwoerterUebernehmenElement.click()
      await expect(page.getByText('Schlagwort 1')).toHaveCount(1)

      // add another schlagwort, assert both are visible
      const schlagwoerterBearbeitenElement = page.getByText('Schlagwörter bearbeiten')
      await schlagwoerterBearbeitenElement.click()

      await schlagwoerterListEditElement.click()
      await schlagwoerterListEditElement.press('End')
      await schlagwoerterListEditElement.press('Enter')
      await schlagwoerterListEditElement.pressSequentially('Schlagwort 2')
      await schlagwoerterListEditElement.press('Enter')
      await schlagwoerterUebernehmenElement.click()
      await expect(page.getByText('Schlagwort 1')).toHaveCount(1)
      await expect(page.getByText('Schlagwort 2')).toHaveCount(1)

      // add one more, but click "abbrechen" instead of confirming, assert that the new element doe not get added
      await schlagwoerterBearbeitenElement.click()
      await schlagwoerterListEditElement.click()
      await schlagwoerterListEditElement.press('End')
      await schlagwoerterListEditElement.press('Enter')
      await schlagwoerterListEditElement.pressSequentially('This should not be added')
      await schlagwoerterListEditElement.press('Enter')
      const abbrechenElement = page
        .getByTestId('keywords')
        .getByRole('button', { name: 'Abbrechen' })
      await abbrechenElement.click()
      await expect(page.getByText('Schlagwort 1')).toHaveCount(1)
      await expect(page.getByText('Schlagwort 2')).toHaveCount(1)
      await expect(page.getByText('This should not be added')).toHaveCount(0)

      // add another one, have the list sorted
      await schlagwoerterBearbeitenElement.click()
      await schlagwoerterListEditElement.click()
      await schlagwoerterListEditElement.press('End')
      await schlagwoerterListEditElement.press('Enter')
      await schlagwoerterListEditElement.pressSequentially('A schlagwort starting with an "A"')
      await schlagwoerterListEditElement.press('Enter')
      const sortAlphabeticallyCheckboxElement = page.getByRole('checkbox', {
        name: 'Alphabetisch sortieren',
      })
      await sortAlphabeticallyCheckboxElement.check()
      await schlagwoerterUebernehmenElement.click()
      // new element is available
      await expect(page.getByText('Schlagwort 1')).toHaveCount(1)
      await expect(page.getByText('Schlagwort 2')).toHaveCount(1)
      await expect(page.getByText('A schlagwort starting with an "A"')).toHaveCount(1)
      // new element is sorted first in list
      await expect(page.getByText('A schlagwort starting with an "A"Schlagwort 1')).toHaveCount(1)
    },
  )

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

  test('Add an active citation, edit and save', { tag: ['@RISDEV-6077'] }, async ({ page }) => {
    await page.goto('/documentUnit/KSNR054920707/fundstellen')
    await page.getByText('Rubriken').click()
    const artDerZitierungInput = page.getByRole('textbox', { name: 'Art der Zitierung' })
    await expect(artDerZitierungInput).toHaveCount(1)

    await artDerZitierungInput.click()
    await page
      .getByRole('button', { name: 'dropdown-option' })
      .filter({ hasText: 'Ablehnung' })
      .click()
    await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
    await expect(page.getByText('Fehlende Daten')).toBeVisible()

    await page.getByTestId('list-entry-0').click()
    await expect(page.getByText('Pflichtfeld nicht befüllt')).toHaveCount(3)
    await page.getByRole('textbox', { name: 'Gericht Aktivzitierung' }).click()
    await page
      .getByRole('button', { name: 'dropdown-option' })
      .filter({ hasText: 'AG Aachen' })
      .click()
    await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
    await expect(page.getByText('Ablehnung, AG Aachen')).toBeVisible()
    await expect(page.getByText('Fehlende Daten')).toBeVisible()

    await page.getByTestId('list-entry-0').click()
    await expect(page.getByText('Pflichtfeld nicht befüllt')).toHaveCount(2)
    await page.getByRole('textbox', { name: 'Entscheidungsdatum' }).fill('15.01.2025')
    await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
    await expect(page.getByText('Ablehnung, AG Aachen, 15.01.2025')).toBeVisible()
    await expect(page.getByText('Fehlende Daten')).toBeVisible()

    await page.getByTestId('list-entry-0').click()
    await expect(page.getByText('Pflichtfeld nicht befüllt')).toHaveCount(1)
    await page.getByRole('textbox', { name: 'Aktenzeichen Aktivzitierung' }).fill('Az1')
    await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
    await expect(page.getByText('Ablehnung, AG Aachen, 15.01.2025, Az1')).toBeVisible()
    await expect(page.getByText('Fehlende Daten')).toHaveCount(0)
  })

  test(
    'Add two active citations, delete the first item',
    { tag: ['@RISDEV-6077'] },
    async ({ page }) => {
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()

      await page.getByRole('textbox', { name: 'Art der Zitierung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Ablehnung' })
        .click()
      await page.getByRole('textbox', { name: 'Gericht Aktivzitierung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'AG Aachen' })
        .click()
      await page.getByRole('textbox', { name: 'Entscheidungsdatum' }).fill('15.01.2025')
      await page.getByRole('textbox', { name: 'Aktenzeichen Aktivzitierung' }).fill('Az1')
      await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
      await expect(page.getByText('Ablehnung, AG Aachen, 15.01.2025, Az1')).toBeVisible()
      await expect(page.getByText('Fehlende Daten')).toHaveCount(0)

      await page.getByRole('textbox', { name: 'Art der Zitierung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Übernahme' })
        .click()
      await page.getByRole('textbox', { name: 'Gericht Aktivzitierung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Berufsgericht für Architekten Bremen' })
        .click()
      await page.getByRole('textbox', { name: 'Entscheidungsdatum' }).fill('31.12.2024')
      await page.getByRole('textbox', { name: 'Aktenzeichen Aktivzitierung' }).fill('Az2')
      await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()
      await expect(
        page.getByText('Übernahme, Berufsgericht für Architekten Bremen, 31.12.2024, Az2'),
      ).toBeVisible()
      await expect(page.getByText('Fehlende Daten')).toHaveCount(0)

      await page.getByTestId('list-entry-1').click()
      await page.getByRole('button', { name: 'Eintrag löschen' }).click()
      await expect(page.getByText('Ablehnung, AG Aachen, 15.01.2025, Az1')).toBeVisible()
      await expect(
        page.getByText('Übernahme, Berufsgericht für Architekten Bremen, 31.12.2024, Az2'),
      ).toHaveCount(0)
    },
  )

  test(
    'Search active citation, take it, change type, save, search again and cancel because already added',
    { tag: ['@RISDEV-6077'] },
    async ({ page }) => {
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()

      await page.getByRole('textbox', { name: 'Art der Zitierung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Übernahme' })
        .click()
      await page.getByRole('button', { name: 'Nach Entscheidung suchen' }).click()
      await expect(
        page.getByText('label1, 01.02.2022, test fileNumber1, documentType1'),
      ).toBeVisible()
      await page.getByRole('button', { name: 'Treffer übernehmen' }).click()
      await expect(
        page.getByText('Übernahme, label1, 01.02.2022, test fileNumber1, documentType1'),
      ).toBeVisible()

      // re-open the same record
      await page.getByTestId('list-entry-0').click()

      // by clicking into the input, there shall be no drop down suggestions
      await page.getByRole('textbox', { name: 'Dokumenttyp Aktivzitierung' }).click()
      await expect(
        page.getByRole('button', { name: 'dropdown-option' }).filter({ hasText: 'VR' }),
      ).toHaveCount(0)

      // but when deleting input content then option VR shall appear
      await page.keyboard.press('Backspace')
      await expect(
        page.getByRole('button', { name: 'dropdown-option' }).filter({ hasText: 'VR' }),
      ).toBeVisible()

      // click this option and save it
      await page.getByRole('button', { name: 'dropdown-option' }).filter({ hasText: 'VR' }).click()
      await page.getByRole('button', { name: 'Aktivzitierung speichern' }).click()

      // then
      await expect(
        page.getByText('Übernahme, label1, 01.02.2022, test fileNumber1, VR'),
      ).toBeVisible()

      await page.getByTestId('list-entry-0').click()
      await page.getByRole('button', { name: 'Nach Entscheidung suchen' }).click()
      await expect(page.getByText('Bereits hinzugefügt')).toBeVisible()

      await page.getByTestId('activeCitations').getByRole('button', { name: 'Abbrechen' }).click()
      await expect(page.getByRole('textbox', { name: 'Art der Zitierung' })).toHaveCount(0)
    },
  )

  test('Add an active reference, edit and save', { tag: ['@RISDEV-6074'] }, async ({ page }) => {
    // given
    await page.goto('/documentUnit/KSNR054920707/fundstellen')
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)
    const referenceTypeElement = page
      .getByTestId('activeReferences')
      .getByRole('textbox', { name: 'Art der Verweisung' })
    await expect(referenceTypeElement).toHaveCount(1)
    const activeReferenceElement = page
      .getByTestId('activeReferences')
      .getByRole('textbox', { name: 'RIS-Abkürzung' })
    await expect(activeReferenceElement).toHaveCount(1)

    await expect(page.getByRole('radio', { name: 'Norm auswählen' })).toHaveCount(1)
    await expect(page.getByRole('radio', { name: 'Verwaltungsvorschrift auswählen' })).toHaveCount(
      1,
    )

    // when
    await referenceTypeElement.click()
    await expect(page.getByText('Neuregelung')).toBeVisible()
    await page.getByText('Neuregelung').click()
    await activeReferenceElement.click()
    await expect(page.getByText('KVLG')).toBeVisible()
    await page.getByText('KVLG').click()
    await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('§ 2')
    await page.getByRole('button', { name: 'Verweis speichern' }).click()
    await expect(page.getByText('Neuregelung | KVLG, § 2')).toHaveCount(1)

    await page.getByTestId('list-entry-0').click()

    // the radio buttons shall be gone as one cannot switch after creating
    await expect(page.getByRole('radio', { name: 'Norm auswählen' })).toHaveCount(0)
    await expect(page.getByRole('radio', { name: 'Verwaltungsvorschrift auswählen' })).toHaveCount(
      0,
    )

    await page.getByRole('textbox', { name: 'Fassungsdatum' }).fill('27.01.2025')
    await page.getByRole('button', { name: 'Verweis speichern' }).click()

    // then
    await expect(page.getByText('Neuregelung | KVLG, § 2, 27.01.2025')).toHaveCount(1)
  })

  test(
    'Add two active references, delete the first item',
    { tag: ['@RISDEV-6074'] },
    async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()
      await expect(page.getByText('Rubriken')).toHaveCount(1)
      const referenceTypeElement = page
        .getByTestId('activeReferences')
        .getByRole('textbox', { name: 'Art der Verweisung' })
      await expect(referenceTypeElement).toHaveCount(1)
      const activeReferenceElement = page
        .getByTestId('activeReferences')
        .getByRole('textbox', { name: 'RIS-Abkürzung' })
      await expect(activeReferenceElement).toHaveCount(1)

      // when
      await referenceTypeElement.click()
      await expect(page.getByText('Anwendung')).toBeVisible()
      await page.getByText('Anwendung').click()

      await activeReferenceElement.click()
      await expect(page.getByText('SGB 5')).toBeVisible()
      await page.getByText('SGB 5').click()
      await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('1991, Seite 92')
      await page.getByRole('button', { name: 'Verweis speichern' }).click()

      await referenceTypeElement.click()
      await expect(page.getByText('Rechtsgrundlage')).toBeVisible()
      await page.getByText('Rechtsgrundlage').click()

      await activeReferenceElement.click()
      await expect(page.getByText('KVLG')).toBeVisible()
      await page.getByText('KVLG').click()
      await page.getByRole('textbox', { name: 'Einzelnorm der Norm' }).fill('§ 2')
      await page.getByRole('button', { name: 'Verweis speichern' }).click()
      await page.getByTestId('list-entry-0').click()
      await page.getByText('Eintrag löschen').click()

      // then
      await expect(page.getByText('SGB 5, 1991, Seite 92')).toHaveCount(0)
      await expect(page.getByText('Rechtsgrundlage | KVLG, § 2')).toHaveCount(1)
    },
  )

  test(
    'type of active reference is not editable after save',
    { tag: ['@RISDEV-6074'] },
    async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByRole('link', { name: 'Rubriken' }).click()
      await page.getByRole('radio', { name: 'Verwaltungsvorschrift auswä' }).click()
      await page.getByRole('textbox', { name: 'Art der Verweisung' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'Anwendung' })
        .click()
      await page.getByRole('textbox', { name: 'Suche nach Verwaltungsvorschrift' }).click()
      await page
        .getByRole('button', { name: 'dropdown-option' })
        .filter({ hasText: 'SGB 5Sozialgesetzbuch (SGB) F' })
        .click()
      await page.getByRole('textbox', { name: 'Fassungsdatum der Norm' }).click()
      await page.getByRole('textbox', { name: 'Fassungsdatum der Norm' }).fill('12.12.2024')
      await page.getByRole('button', { name: 'Verweis speichern' }).click()

      // when
      await page.getByTestId('list-entry-0').click()

      // then
      await expect(page.getByRole('radio', { name: 'Verwaltungsvorschrift auswä' })).toHaveCount(0)
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
