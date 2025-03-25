import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Sachgebiete', () => {
  const phantasierecht = 'PR | Phantasierecht'

  test.describe('With mocked routes', () => {
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
      "rendering initial state, switching between 'Direkteingabe' and 'Suche', collapsing inputs",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given, when
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        await expect(page.getByRole('heading', { name: 'Sachgebiete' })).toBeVisible()
        await expect(page.getByRole('button', { name: 'Sachgebiete' })).toBeVisible()
        await page.getByRole('button', { name: 'Sachgebiete' }).click()

        await expect(page.getByText('Direkteingabe Sachgebiet')).toBeVisible()

        await page.getByLabel('Sachgebietsuche auswählen').click()

        await expect(page.getByLabel('Sachgebietskürzel')).toBeVisible()
        await expect(page.getByLabel('Sachgebietsbezeichnung')).toBeVisible()
        await expect(page.getByLabel('Sachgebietsnorm')).toBeVisible()
        await expect(page.getByLabel('Sachgebietssuche ausführen')).toBeVisible()
        await expect(page.getByText('Sachgebietsbaum')).toBeVisible()

        await page.getByRole('button', { name: 'Fertig' }).click()
        await expect(page.getByRole('button', { name: 'Sachgebiete' })).toBeVisible()
      },
    )

    test(
      "click on root element in 'fields of law'-tree open level one",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given, when
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()

        await page
          .getByRole('button', {
            name: 'Alle Sachgebiete aufklappen',
          })
          .click()

        await expect(page.getByText(phantasierecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung der Phantasieverhältnisse')).toBeHidden()
      },
    )

    test(
      "click on root element in 'fields of law'-tree and on level one on 'Phantasierecht'",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given, when
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()

        await page
          .getByRole('button', {
            name: 'Alle Sachgebiete aufklappen',
          })
          .click()

        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()

        await expect(page.getByText(phantasierecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung der Phantasieverhältnisse')).toBeVisible()
      },
    )

    test(
      "click on root element in 'fields of law'-tree and on level one on 'Phantasierecht, close and reopen root then sub-tree is still open'",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()

        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()
        await page.getByRole('button', { name: 'Alle Sachgebiete einklappen' }).click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()

        // then
        await expect(page.getByText(phantasierecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung der Phantasieverhältnisse')).toBeVisible()
      },
    )

    test(
      "open 'Phantasierecht' - tree and add 'Beendigung der Phantasieverhältnisse' from tree",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()
        await page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse hinzufügen').click()

        // then
        await expect(page.getByText('Die Liste ist aktuell leer')).toBeHidden()
        await expect(
          page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse im Sachgebietsbaum anzeigen'),
        ).toBeVisible()
        await expect(
          page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse entfernen'),
        ).toBeVisible()
      },
    )

    test(
      "open 'Phantasierecht' - tree, add and remove 'Beendigung der Phantasieverhältnisse' from tree",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()
        await page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse hinzufügen').click()
        await page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse entfernen').click()

        // then
        await expect(
          page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse hinzufügen'),
        ).toBeVisible()
      },
    )

    test(
      'opening and closing tree nodes, nodes with no children do not display expand icon',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()
        await page
          .getByRole('button', { name: 'Beendigung der Phantasieverhältnisse aufklappen' })
          .click()

        // then
        await expect(
          page.getByText('Phantasie besonderer Art, Ansprüche anderer Art'),
        ).toBeVisible()
        await expect(
          page.getByLabel('Phantasie besonderer Art, Ansprüche anderer Art aufklappen'),
        ).toBeHidden()
      },
    )

    test(
      'add field of law from tree and remove via selection list',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Phantasierecht aufklappen' }).click()
        await page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse hinzufügen').click()
        await page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse entfernen').click()

        // then
        await expect(
          page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse aus Liste entfernen'),
        ).toBeHidden()
        await expect(
          page.getByLabel('PR-05 Beendigung der Phantasieverhältnisse hinzufügen'),
        ).toBeVisible()
      },
    )

    // Search

    test('Search without results', { tag: ['@RISDEV-6315'] }, async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/rubriken')

      // when
      await page.getByRole('button', { name: 'Sachgebiete' }).click()
      await page.getByLabel('Sachgebietsuche auswählen').click()
      await page.getByLabel('Sachgebietskürzel').fill('xyz')
      await page.keyboard.press('Enter')

      // then
      await expect(page.getByText('Keine Suchergebnisse gefunden')).toBeVisible()
    })

    test(
      'Search with paginated results - test the pagination navigation',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByLabel('Sachgebietsbezeichnung').fill('arbeit')
        await page.getByRole('button', { name: 'Sachgebietssuche ausführen' }).click()

        // then
        // There are only 3 results
        await expect(page.getByText('Seite 1')).toBeHidden()
        await expect(page.getByRole('button', { name: 'vorherige Ergebnisse' })).toBeHidden()
        await expect(page.getByRole('button', { name: 'nächste Ergebnisse' })).toBeHidden()
      },
    )

    test(
      'Search with paginated results - first result to open in tree',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByLabel('Sachgebietsbezeichnung').fill('Phantasie')
        await page.keyboard.press('Enter')

        // then
        // if these two are visible, it must mean that the tree opened automatically with the first result
        await expect(page.getByText('Beendigung der Phantasieverhältnisse')).toBeVisible()
        await expect(
          page.getByText('Phantasie besonderer Art, Ansprüche anderer Art'),
        ).toBeVisible()
      },
    )

    test(
      'Search with paginated results - click on result opens tree and adds result to list',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByLabel('Sachgebietsbezeichnung').fill('phantasie')
        await page.keyboard.press('Enter')
        const searchResult = page.getByLabel('PR-05 hinzufügen')
        await searchResult.click()

        // then
        await expect(
          page.getByRole('button', {
            name: 'PR-05 Beendigung der Phantasieverhältnisse aus Liste entfernen',
          }),
        ).toBeVisible()
      },
    )

    test(
      'Search with both norm string and description - sets show norm checkbox to true',
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()

        await page.getByLabel('Sachgebietsbezeichnung').fill('phantasie')
        await page.getByLabel('Sachgebietsnorm').fill('§ 99')
        await page.keyboard.press('Enter')

        await expect(page.getByLabel('PR-05 hinzufügen')).toBeVisible()

        // if this is visible, it means that the "Normen anzeigen" checkbox got set to true
        await expect(page.getByText('§ 99 PStG').first()).toBeVisible()
      },
    )

    test(
      "click on 'Suche zurücksetzen' empties search inputs, hides search results and collapses tree",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByLabel('Sachgebietskürzel').fill('PR')
        await page.getByLabel('Sachgebietsbezeichnung').fill('arbeit')
        await page.getByLabel('Sachgebietsnorm').fill('§ 99')
        await page.keyboard.press('Enter')
        await page.getByRole('button', { name: 'Suche zurücksetzen' }).click()

        // then
        await expect(page.getByLabel('Sachgebietskürzel')).toHaveValue('')
        await expect(page.getByLabel('Sachgebietsbezeichnung')).toHaveValue('')
        await expect(page.getByLabel('Sachgebietsnorm')).toHaveValue('')
        await expect(page.getByLabel('PR-05 hinzufügen')).toBeHidden()
        await expect(
          page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }),
        ).toBeVisible()
      },
    )

    // Direct input

    test('Direct input - search and choose item', { tag: ['@RISDEV-6315'] }, async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/rubriken')

      // when
      await page.getByRole('button', { name: 'Sachgebiete' }).click()
      await page.getByLabel('Direkteingabe-Sachgebietssuche eingeben').fill('PR')
      await page.getByText('Beendigung').click()

      // then
      // it was added to the selection list
      await expect(
        page.getByRole('button', {
          name: 'PR-05 Beendigung der Phantasieverhältnisse aus Liste entfernen',
        }),
      ).toBeVisible()
    })
  })

  test('Direkteingabe allows for entering data which persists through a reload', async ({
    page,
  }) => {
    // given
    await page.goto('/')
    await page.getByText('Neue Dokumentationseinheit').click()
    await page.getByText('Rubriken').click()

    await page.getByRole('button', { name: 'Sachgebiete' }).click()
    await page.getByRole('radio', { name: 'Direkteingabe' }).check()

    // when
    await page.getByRole('textbox', { name: 'Direkteingabe-' }).fill('Arbeitsr')
    await page
      .getByRole('button', { name: 'dropdown-option' })
      .filter({ hasText: 'Phantasierecht' })
      .click()
    // then
    await expect(page.getByRole('button', { name: 'Fertig' })).toHaveCount(1)
    await expect(page.getByText('PRPhantasierecht')).toHaveCount(1)

    //when
    await page.getByRole('button', { name: 'Speichern', exact: true }).click()
    await page.reload()
    // then
    await expect(page.getByText('PRPhantasierecht')).toHaveCount(1)
  })
})
