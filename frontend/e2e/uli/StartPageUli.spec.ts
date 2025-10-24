import { test, expect } from '@playwright/test'

test.describe('StartPage ULI', () => {
  test(
    'shows the title "Unselbstständige Literatur", the user data, a logout and a "create document" buttons',
    { tag: ['@RISDEV-9370'] },
    async ({ page }) => {
      // Action
      await page.goto('/literatur-unselbststaendig')
      // Assert
      await expect(page.getByText('Rechtsinformationen')).toBeVisible()
      await expect(page.getByText('des Bundes')).toBeVisible()
      // user icon
      await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
      await expect(page.getByText('bag nachname')).toBeVisible()
      await expect(page.getByText('BAG | staging')).toHaveCount(1)
      await expect(page.getByText('Übersicht Unselbstständige Literatur')).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Neue Dokumentationseinheit' })).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Log out' })).toHaveCount(1)
    },
  )

  test(
    'clicking on button "Neue Dokumentationseinheit" creates a document and redirects to the edit page on success, route shows "/dokumentationseinheit/"',
    { tag: ['@RISDEV-9887'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-unselbststaendig')

      // when
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

      // then
      await expect(page.getByRole('heading', { name: /KALU\d{10}/ })).toHaveCount(1)
      expect(page.url()).toContain('/dokumentationseinheit/')
    },
  )
})
