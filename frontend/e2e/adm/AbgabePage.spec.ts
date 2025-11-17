import { test, expect } from '@playwright/test'

test.describe('ADM AbgabePage', () => {
  test(
    'A document is publishable when its mandatory fields are non empty',
    { tag: ['@RISDEV-8436'] },
    async ({ page }) => {
      // given
      await page.goto('/verwaltungsvorschriften/dokumentationseinheit/KSNR999999999')

      // when
      await page.getByText('Abgabe').click()

      // then
      await expect(page.getByRole('heading', { name: 'Abgabe' })).toBeVisible()
      await expect(page.getByText('Plausibilitätsprüfung')).toBeVisible()
      await expect(page.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeVisible()
      await expect(
        page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }),
      ).toBeEnabled()
    },
  )

  test(
    `A document is not publishable when at least one of its mandatory fields is empty,
    shows the required fields and a link to the rubriken page`,
    { tag: ['@RISDEV-8436'] },
    async ({ page }) => {
      // given
      await page.goto('/verwaltungsvorschriften/dokumentationseinheit/KSNR999999999/rubriken')
      await expect(page.getByText('Amtl. Langüberschrift *')).toHaveCount(1)
      await page.getByText('Amtl. Langüberschrift *').fill('')
      await expect(page.getByRole('button', { name: 'Speichern' })).toBeVisible()

      // when
      await page.getByText('Abgabe').click()

      // then
      await expect(page.getByRole('button', { name: 'Speichern' })).toBeHidden()
      await expect(page.getByRole('heading', { name: 'Abgabe' })).toBeVisible()
      await expect(page.getByText('Plausibilitätsprüfung')).toBeVisible()
      await expect(page.getByText('Folgende Pflichtfelder sind nicht befüllt')).toBeVisible()
      await expect(page.getByText('Amtl. Langüberschrift')).toBeVisible()
      await expect(page.getByRole('button', { name: 'Rubriken bearbeiten' })).toBeVisible()
      await expect(
        page.getByRole('button', { name: 'Zur Veröffentlichung freigeben' }),
      ).toBeDisabled()

      // when
      await page.getByRole('button', { name: 'Rubriken bearbeiten' }).click()

      // then
      await expect(page.getByRole('heading', { name: 'Formaldaten' })).toBeVisible()
    },
  )

  test(
    'Should show links to the mandatory fields; when clicking on a link, navigates to the corresponding field in Rubriken page',
    { tag: ['@RISDEV-9374'] },
    async ({ page }) => {
      // given
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Amtl. Langüberschrift' }).click()
      // then
      await expect(page.getByText('Amtl. Langüberschrift *')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Datum des Inkrafttretens' }).click()
      // then
      await expect(page.getByText('Datum des Inkrafttretens *')).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Dokumenttyp' }).click()
      // then
      await expect(page.getByText('Dokumenttyp *', { exact: true })).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Normgeber' }).click()
      // then
      await expect(page.getByText('Normgeber *', { exact: true })).toBeInViewport()

      // when
      await page.getByText('Abgabe').click()
      await page.getByRole('link', { name: 'Zitierdatum' }).click()
      // then
      await expect(page.getByText('Zitierdatum *', { exact: true })).toBeInViewport()
    },
  )

  test('Should fill in all fields and succeed in publishing', async ({ page }) => {
    // Given
    await page.goto('/')
    await page.getByText('Neue Dokumentationseinheit').click()

    await page.getByRole('combobox', { name: 'Periodikum' }).fill('Die')
    await page.getByText('ABc | Die Beispieler').click()
    await page.getByRole('textbox', { name: 'Zitatstelle' }).fill('2024, Seite 24')
    await page.getByText('Übernehmen').click()

    await page.getByRole('link', { name: 'Rubriken' }).click()
    await page.getByText('Amtl. Langüberschrift *').fill('This is my Langüberschrift')
    await page.getByRole('group', { name: 'Zitierdatum' }).locator('input').fill('15.01.2025')

    await page.getByRole('combobox', { name: 'Normgeber' }).click()
    await page.getByText('Erstes Organ').click()
    await page.getByRole('combobox', { name: 'Region' }).fill('AA')
    await page.getByText('AA').click()
    await page.getByRole('button', { name: 'Normgeber übernehmen', exact: true }).click()
  })
})
