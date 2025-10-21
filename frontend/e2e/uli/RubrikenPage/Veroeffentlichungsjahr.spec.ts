import { expect, test } from '@playwright/test'

test.describe('ULI Rubriken - Veroeffentlichungsjahr', () => {
  test(
    'Veroeffentlichungsjahr is a mandatory field and its value persists after saving and reloading',
    { tag: ['@RISDEV-9372'] },
    async ({ page }) => {
      // when
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.waitForURL(/dokumentationseinheit/)

      // then
      await expect(page.getByText('Veröffentlichungsjahr *')).toBeVisible()

      // when
      const yearInput = page.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await yearInput.fill('2025')
      await page.getByText('Speichern').click()
      await page.reload()

      // then
      await expect(yearInput).toHaveValue('2025')
    },
  )
})

test.describe('ULI Rubriken - Veroeffentlichungsjahr (with mocked api route)', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/documentation-units/KALU123456789', async (route) => {
      const json = {
        documentNumber: 'KALU123456789',
        id: 'de469be9-4401-485a-9e54-a43dee00cbbe',
        json: null,
      }
      await route.fulfill({ json })
    })
  })

  test(
    'Shows validation error for an incomplete or invalid year, only numbers are accepted',
    { tag: ['@RISDEV-9372'] },
    async ({ page }) => {
      // when
      await page.goto('/literatur-unselbstaendig/dokumentationseinheit/KALU123456789/rubriken')
      const yearInput = page.getByRole('textbox', { name: 'Veröffentlichungsjahr' })
      await yearInput.fill('20')
      await yearInput.blur()

      // then
      await expect(page.getByText('Unvollständiges Jahr')).toBeVisible()

      // when
      await yearInput.clear()
      await yearInput.fill('0000')
      await yearInput.blur()

      // then
      await expect(page.getByText('Kein valides Jahr')).toBeVisible()

      // when
      await yearInput.clear()
      await yearInput.fill('year')

      // then
      await expect(yearInput).toHaveValue('____')
    },
  )
})
