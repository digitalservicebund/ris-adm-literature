import { test, expect } from '@playwright/test'

test.describe('Overview SLI', () => {
  test(
    'shows the SLI tab as active, the user data, a logout and a "create document" buttons',
    { tag: ['@RISDEV-10109'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')

      // then
      await expect(page.getByText('Rechtsinformationen')).toBeVisible()
      await expect(page.getByText('des Bundes')).toBeVisible()
      await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
      await expect(page.getByText('bag nachname')).toBeVisible()
      await expect(page.getByText('BAG | staging')).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Log out' })).toHaveCount(1)

      await expect(
        page.getByRole('tab', { name: 'Unselbstständige Literatur', exact: true }),
      ).toBeVisible()
      await expect(
        page.getByRole('tab', { name: 'Selbstständige Literatur', exact: true }),
      ).toBeVisible()
      await expect(
        page.getByRole('tab', { name: 'Selbstständige Literatur', exact: true }),
      ).toHaveAttribute('aria-selected', 'true')

      await expect(page.getByRole('button', { name: 'Neue Dokumentationseinheit' })).toBeDisabled()
    },
  )

  test(
    'switches from SLI to ULI tab and URL change',
    { tag: ['@RISDEV-10109'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')
      const sliTab = page.getByRole('tab', { name: 'Selbstständige Literatur', exact: true })

      // ensure initial state
      await expect(sliTab).toHaveAttribute('aria-selected', 'true')

      // when
      await page.getByRole('tab', { name: 'Unselbstständige Literatur', exact: true }).click()

      // then
      await expect(page).toHaveURL('/literatur-unselbststaendig')
      await expect(
        page.getByRole('tab', { name: 'Unselbstständige Literatur', exact: true }),
      ).toHaveAttribute('aria-selected', 'true')
    },
  )

  test(
    'active tab and URL persists on page refresh',
    { tag: ['@RISDEV-10109'] },
    async ({ page }) => {
      // given
      await page.goto('/literatur-selbststaendig')
      const sliTab = page.getByRole('tab', { name: 'Selbstständige Literatur', exact: true })
      await expect(sliTab).toHaveAttribute('aria-selected', 'true')

      // when
      await page.reload()

      // then
      await expect(sliTab).toHaveAttribute('aria-selected', 'true')
      expect(page.url()).toContain('/literatur-selbststaendig')
    },
  )
})
