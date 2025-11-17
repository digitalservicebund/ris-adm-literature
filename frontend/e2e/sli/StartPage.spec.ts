import { test, expect } from '@playwright/test'

test.describe('StartPage SLI', () => {
  test(
    'clicking on button "Neue Dokumentationseinheit" creates a SLI document with BAG office format (KALS) and redirects to edit page',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')

      // when
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

      // then
      // testbag user belongs to BAG office, so document number should have KALS prefix
      await expect(page.getByRole('heading', { name: /KALS\d{10}/ })).toHaveCount(1)
      expect(page.url()).toContain('/literatur-selbststaendig/dokumentationseinheit/')
    },
  )

  test(
    'sequential numbering: second document has larger ID than first',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      const firstDocNumber = await page.getByText(/KALS\d{10}/).textContent()

      // when - go back and create another
      await page.getByRole('link', { name: 'Suche' }).click()
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      const secondDocNumber = await page.getByText(/KALS\d{10}/).textContent()

      // then
      // eslint-disable-next-line playwright/prefer-web-first-assertions
      expect(secondDocNumber).not.toBe(firstDocNumber)
      // Second should be alphanumerically larger
      expect(secondDocNumber! > firstDocNumber!).toBe(true)
    },
  )

  test(
    'clicking "Suche" button from SLI edit view navigates to ULI tab',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

      // when
      await page.getByRole('link', { name: 'Suche' }).click()

      // then
      expect(page.url()).toContain('/literatur-selbststaendig')
      await expect(
        page.getByRole('tab', { name: 'Selbstständige Literatur', exact: true }),
      ).toHaveAttribute('aria-selected', 'true')
    },
  )

  test(
    'Veröffentlichungsjahr is a mandatory field (*), accepts alphanumeric input, and persists after saving and reloading',
    { tag: ['@RISDEV-10142', '@RISDEV-10119'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      // then - field marked as required
      await expect(page.getByText('Veröffentlichungsjahr *')).toBeVisible()

      // when - enter alphanumeric input (variable length)
      const veroeffentlichungsjahrInput = page.getByRole('textbox', {
        name: 'Veröffentlichungsjahr',
      })
      await veroeffentlichungsjahrInput.fill('2020 bis 2025 $%&abc123')

      // when - save
      await page.getByRole('button', { name: 'Speichern' }).click()

      // then - shows save confirmation
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // when - reload
      await page.reload()

      // then - value persists
      await expect(veroeffentlichungsjahrInput).toHaveValue('2020 bis 2025 $%&abc123')
    },
  )
})
