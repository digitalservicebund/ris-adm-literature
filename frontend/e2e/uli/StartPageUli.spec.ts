import { test, expect } from '@playwright/test'

test.describe('StartPage ULI', () => {
  test(
    'shows the ULI tab as active, user data, logout and "create document" buttons',
    { tag: ['@RISDEV-9370'] },
    async ({ page }) => {
      // Action
      await page.goto('/literatur-unselbstaendig')
      // Assert
      await expect(page.getByText('Rechtsinformationen')).toBeVisible()
      await expect(page.getByText('des Bundes')).toBeVisible()
      // user icon
      await expect(page.getByTestId('iconPermIdentity')).toHaveCount(1)
      await expect(page.getByText('bag nachname')).toBeVisible()
      await expect(page.getByText('BAG | staging')).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Neue Dokumentationseinheit' })).toHaveCount(1)
      await expect(page.getByRole('button', { name: 'Log out' })).toHaveCount(1)

      await expect(
        page.getByRole('tab', { name: 'Unselbständige Literatur', exact: true }),
      ).toBeVisible()
      await expect(
        page.getByRole('tab', { name: 'Selbständige Literatur', exact: true }),
      ).toBeVisible()
      await expect(
        page.getByRole('tab', { name: 'Unselbständige Literatur', exact: true }),
      ).toHaveAttribute('aria-selected', 'true')
    },
  )

  function extractSequenceNumber(fullId: string): number {
    // Remove all non-digit characters
    const numericPart = fullId.replace(/\D/g, '')

    // return the number as an integer
    return parseInt(numericPart, 10)
  }

  const SCENARIOS = [
    { office: 'BAG', prefix: 'KA', authFile: '../frontend/e2e/.auth/user-bag.json' },
    { office: 'BFH', prefix: 'ST', authFile: '../frontend/e2e/.auth/user-bfh.json' },
    { office: 'BSG', prefix: 'KS', authFile: '../frontend/e2e/.auth/user-bsg.json' },
    { office: 'BVERFG', prefix: 'KV', authFile: '../frontend/e2e/.auth/user-bverfg.json' },
    { office: 'BVERWG', prefix: 'WB', authFile: '../frontend/e2e/.auth/user-bverwg.json' },
  ]

  test.describe('Documentation Unit Creation ULI - Cross Office Check', () => {
    // Iterate over scenarios
    SCENARIOS.forEach(({ office, prefix, authFile }) => {
      test.describe(`Office: ${office}`, () => {
        // Load the specific user session for this office
        test.use({ storageState: authFile })

        test(
          `creates sequential document numbers (Increment check)`,
          { tag: ['@RISDEV-9887', '@RISDEV-9371'] },
          async ({ page }) => {
            // Create first document
            await page.goto('/literatur-unselbstaendig')
            await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

            const expectedPattern = new RegExp(`${prefix}LU\\d{10}`)
            const headingLocator = page.getByRole('heading', { name: expectedPattern })

            await expect(headingLocator).toBeVisible()
            const firstDocId = await headingLocator.textContent()

            expect(firstDocId).toMatch(expectedPattern)

            // Create second document
            await page.goto('/literatur-unselbstaendig')
            await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

            await expect(headingLocator).toBeVisible()
            const secondDocId = await headingLocator.textContent()

            // Compare
            const firstSeq = extractSequenceNumber(firstDocId!)
            const secondSeq = extractSequenceNumber(secondDocId!)

            // Check if it starts with the current year
            const currentYear = new Date().getFullYear().toString()
            const yearRegex = new RegExp(`^${currentYear}`)
            expect(firstSeq.toString()).toMatch(yearRegex)
            expect(secondSeq.toString()).toMatch(yearRegex)

            // The second number must be exactly one higher
            expect(secondSeq).toBe(firstSeq + 1)
          },
        )
      })
    })
  })
})
