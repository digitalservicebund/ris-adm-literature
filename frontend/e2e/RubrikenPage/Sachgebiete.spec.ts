import { test, expect } from '@playwright/test'

test.describe('RubrikenPage - Sachgebiete', () => {
  const arbeitsrecht = 'AR | Arbeitsrecht'

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

        await expect(page.getByText(arbeitsrecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung des Arbeitsverhältnisses')).toBeHidden()
      },
    )

    test(
      "click on root element in 'fields of law'-tree and on level one on 'Arbeitsrecht'",
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

        await page.getByRole('button', { name: 'Arbeitsrecht aufklappen' }).click()

        await expect(page.getByText(arbeitsrecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung des Arbeitsverhältnisses')).toBeVisible()
      },
    )

    test(
      "click on root element in 'fields of law'-tree and on level one on 'Arbeitsrecht, close and reopen root then sub-tree is still open'",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()

        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Arbeitsrecht aufklappen' }).click()
        await page.getByRole('button', { name: 'Alle Sachgebiete einklappen' }).click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()

        // then
        await expect(page.getByText(arbeitsrecht, { exact: true })).toBeVisible()
        await expect(page.getByText('Beendigung des Arbeitsverhältnisses')).toBeVisible()
      },
    )

    test(
      "open 'Arbeitsrecht' - tree and add 'Beendigung des Arbeitsverhältnisses' from tree",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Arbeitsrecht aufklappen' }).click()
        await page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses hinzufügen').click()

        // then
        await expect(page.getByText('Die Liste ist aktuell leer')).toBeHidden()
        await expect(
          page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses im Sachgebietsbaum anzeigen'),
        ).toBeVisible()
        await expect(
          page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses entfernen'),
        ).toBeVisible()
      },
    )

    test(
      "open 'Arbeitsrecht' - tree, add and remove 'Beendigung des Arbeitsverhältnisses' from tree",
      { tag: ['@RISDEV-6315'] },
      async ({ page }) => {
        // given
        await page.goto('/documentUnit/KSNR054920707/rubriken')

        // when
        await page.getByRole('button', { name: 'Sachgebiete' }).click()
        await page.getByLabel('Sachgebietsuche auswählen').click()
        await page.getByRole('button', { name: 'Alle Sachgebiete aufklappen' }).click()
        await page.getByRole('button', { name: 'Arbeitsrecht aufklappen' }).click()
        await page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses hinzufügen').click()
        await page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses entfernen').click()

        // then
        await expect(
          page.getByLabel('AR-05 Beendigung des Arbeitsverhältnisses hinzufügen'),
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
        await page.getByRole('button', { name: 'Arbeitsrecht aufklappen' }).click()
        await page
          .getByRole('button', { name: 'Beendigung des Arbeitsverhältnisses aufklappen' })
          .click()

        // then
        await expect(
          page.getByText('Beendigungen besonderer Art, nachvertragliche Ansprüche'),
        ).toBeVisible()
        await expect(
          page.getByLabel('Beendigungen besonderer Art, nachvertragliche Ansprüche aufklappen'),
        ).toBeHidden()
      },
    )
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
      .filter({ hasText: 'Arbeitsrecht' })
      .click()
    // then
    await expect(page.getByRole('button', { name: 'Fertig' })).toHaveCount(1)
    await expect(page.getByText('ARArbeitsrecht')).toHaveCount(1)

    //when
    await page.getByRole('button', { name: 'Speichern', exact: true }).click()
    await page.reload()
    // then
    await expect(page.getByText('ARArbeitsrecht')).toHaveCount(1)
  })
})
