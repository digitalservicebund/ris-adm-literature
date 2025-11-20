import { test, expect } from '@playwright/test'

test.describe('SLI Rubriken – Dokumenttyp', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/literatur-selbstaendig')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
    await page.waitForURL(/dokumentationseinheit/)
  })

  test(
    'selecting and filtering document types shows abbreviation + name',
    { tag: ['@RISDEV-10120'] },
    async ({ page }) => {
      // given – new SLI document edit view
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.click()
      const overlay = page.getByRole('listbox', { name: 'Optionsliste' })
      await expect(overlay.getByRole('option', { name: 'Bib' })).toBeVisible()

      // when – user filters for Bib and selects it
      await input.fill('Bi')
      await expect(overlay.getByRole('option', { name: 'Bib' })).toBeVisible()
      await overlay.getByRole('option', { name: 'Bib' }).click()

      // then – chip displays Bib while overlay stays open
      // eslint-disable-next-line playwright/no-raw-locators
      const chipList = page
        .getByTestId('document-type-autocomplete')
        .locator('[role="listbox"][aria-orientation="horizontal"]')
      await expect(chipList.getByRole('option', { name: 'Bib' })).toBeVisible()

      // when – user adds Dis as well

      await input.fill('Dis')
      await expect(overlay.getByRole('option', { name: 'Dis' })).toBeVisible()
      await overlay.getByRole('option', { name: 'Dis' }).click()

      // then – both chips are visible
      await expect(chipList.getByRole('option', { name: 'Dis' })).toBeVisible()
    },
  )

  test(
    'removing a selected document type via chip X works',
    { tag: ['@RISDEV-10120'] },
    async ({ page }) => {
      // given
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      await page
        .getByRole('listbox', { name: 'Optionsliste' })
        .getByRole('option', { name: 'Bib' })
        .click()

      // eslint-disable-next-line playwright/no-raw-locators
      const chipList = page
        .getByTestId('document-type-autocomplete')
        .locator('[role="listbox"][aria-orientation="horizontal"]')
      await expect(chipList.getByRole('option', { name: 'Bib' })).toBeVisible()

      // when – user clicks the X icon on the chip
      // eslint-disable-next-line playwright/no-raw-locators
      await chipList
        .getByRole('option', { name: 'Bib', exact: true })
        .locator('svg[data-pc-section="removeicon"]')
        .click()

      // then
      await expect(chipList.getByRole('option', { name: 'Bib', exact: true })).toBeHidden()
    },
  )

  test(
    'selected document types persist after save + reload',
    { tag: ['@RISDEV-10120'] },
    async ({ page }) => {
      // then
      await expect(page.getByText('Dokumenttyp *')).toBeVisible()

      // given
      const input = page.getByRole('combobox', { name: 'Dokumenttyp' })
      await input.fill('Bi')
      await page
        .getByRole('listbox', { name: 'Optionsliste' })
        .getByRole('option', { name: 'Bib' })
        .click()
      await input.fill('Dis')
      await page
        .getByRole('listbox', { name: 'Optionsliste' })
        .getByRole('option', { name: 'Dis' })
        .click()

      // when – user saves the document
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when – user reloads the page
      await page.reload()

      // then – both chips are still visible
      // eslint-disable-next-line playwright/no-raw-locators
      const chipList = page
        .getByTestId('document-type-autocomplete')
        .locator('[role="listbox"][aria-orientation="horizontal"]')
      await expect(chipList.getByRole('option', { name: 'Bib' })).toBeVisible()
      await expect(chipList.getByRole('option', { name: 'Dis' })).toBeVisible()
    },
  )
})
