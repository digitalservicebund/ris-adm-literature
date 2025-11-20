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

  test(
    'Veröffentlichungsjahr is a mandatory field (*), accepts alphanumeric input, and persists after saving and reloading',
    { tag: ['@RISDEV-10142', '@RISDEV-10119'] },
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

  test.describe('Documentation Unit Creation SLI - Cross Office Check', () => {
    // Iterate over scenarios
    SCENARIOS.forEach(({ office, prefix, authFile }) => {
      test.describe(`Office: ${office}`, () => {
        // Load the specific user session for this office
        test.use({ storageState: authFile })

        test(
          `creates sequential document numbers (Increment check)`,
          { tag: ['@RISDEV-10118'] },
          async ({ page }) => {
            // Create first document
            await page.goto('/literatur-selbstaendig')
            await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

            const expectedPattern = new RegExp(`${prefix}LS\\d{10}`)
            const headingLocator = page.getByRole('heading', { name: expectedPattern })

            await expect(headingLocator).toBeVisible()
            const firstDocId = await headingLocator.textContent()

            expect(firstDocId).toMatch(expectedPattern)

            // Create second document
            await page.goto('/literatur-selbstaendig')
            await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()

            await expect(headingLocator).toBeVisible()
            const secondDocId = await headingLocator.textContent()

            // Compare
            const firstSeq = extractSequenceNumber(firstDocId!)
            const secondSeq = extractSequenceNumber(secondDocId!)

            // The second number must be exactly one higher
            expect(secondSeq).toBe(firstSeq + 1)
          },
        )
      })
    })
  })
})
