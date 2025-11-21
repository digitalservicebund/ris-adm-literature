import { test, expect } from '@playwright/test'

test.describe('StartPage SLI', () => {
  test(
    'clicking on button "Neue Dokumentationseinheit" creates a SLI document with BAG office format (KALS) and redirects to edit page',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')

      // when
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

      // then
      // testbag user belongs to BAG office, so document number should have KALS prefix
      await expect(page.getByRole('heading', { name: /KALS\d{10}/ })).toHaveCount(1)
      expect(page.url()).toContain('/literatur-selbstaendig/dokumentationseinheit/')
    },
  )

  test(
    'sequential numbering: second document has larger ID than first',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
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
    'clicking "Suche" button from SLI edit view navigates to SLI tab',
    { tag: ['@RISDEV-10118'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

      // when
      await page.getByRole('link', { name: 'Suche' }).click()

      // then
      expect(page.url()).toContain('/literatur-selbstaendig')
      await expect(
        page.getByRole('tab', { name: 'Selbständige Literatur', exact: true }),
      ).toHaveAttribute('aria-selected', 'true')
    },
  )
})
