import { test, expect } from '@playwright/test'

test.describe('SLI Rubriken - Veroeffentlichungsjahr', () => {
  test(
    'Veröffentlichungsjahr is a mandatory field (*), accepts alphanumeric input, and persists after saving and reloading. Dates can be deleted again and persisted.',
    { tag: ['@RISDEV-10142', '@RISDEV-10119', '@RISDEV-10287'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      // then - field marked as required
      await expect(page.getByText('Veröffentlichungsjahr *')).toBeVisible()

      // when - enter alphanumeric input (variable length)
      const veroeffentlichungsjahrInput = page.getByRole('textbox', {
        name: 'Veröffentlichungsjahr',
      })
      await veroeffentlichungsjahrInput.fill('2020 bis 2025 $%&abc123 🎇')

      // when - save
      await page.getByRole('button', { name: 'Speichern' }).click()

      // then - shows save confirmation
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when - reload
      await page.reload()

      // then - value persists
      await expect(veroeffentlichungsjahrInput).toHaveValue('2020 bis 2025 $%&abc123 🎇')

      await veroeffentlichungsjahrInput.fill('')
      await page.getByText('Speichern').click()

      // then
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when
      await page.reload()

      // then
      await expect(veroeffentlichungsjahrInput).toHaveValue('')
    },
  )
})
