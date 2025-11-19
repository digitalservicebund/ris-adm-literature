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

  test(
    'Hauptsachtitel/Dokumentarischer Titel toggle and persistence',
    { tag: ['@RISDEV-10121'] },
    async ({ page }) => {
      // given – new SLI document edit view
      await page.goto('/literatur-selbstaendig')
      await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
      await page.waitForURL(/dokumentationseinheit/)

      const hauptInput = page.getByRole('textbox', { name: 'Hauptsachtitel' })
      await expect(hauptInput).toBeEnabled()
      await expect(page.getByRole('button', { name: 'Dokumentarischer Titel' })).toBeVisible()

      // when – user opens documentary title and fills it
      await page.getByRole('button', { name: 'Dokumentarischer Titel' }).click()
      const dokumentarischerInput = page.getByRole('textbox', { name: 'Dokumentarischer Titel' })
      await expect(dokumentarischerInput).toBeVisible()
      await page.getByRole('textbox', { name: 'Dokumentarischer Titel' }).fill('Beispieltitel')
      await expect(hauptInput).toBeDisabled()

      const docValue = 'Dokumentarischer Titel 123$%'
      await dokumentarischerInput.fill(docValue)
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – value persists and Hauptsachtitel stays disabled
      await page.reload()
      await expect(page.getByRole('textbox', { name: 'Dokumentarischer Titel' })).toHaveValue(
        docValue,
      )
      await expect(page.getByRole('textbox', { name: 'Hauptsachtitel' })).toBeDisabled()
      // when – user clears documentary title and saves again
      const dokumentarischerInputAfterReload = page.getByRole('textbox', {
        name: 'Dokumentarischer Titel',
      })
      await dokumentarischerInputAfterReload.clear()
      await dokumentarischerInputAfterReload.blur()
      await page.getByRole('button', { name: 'Speichern' }).click()
      await expect(page.getByText(/Gespeichert: .* Uhr/)).toBeVisible()

      // then – button returns and Hauptsachtitel is enabled

      await page.reload()
      await expect(page.getByRole('button', { name: 'Dokumentarischer Titel' })).toBeVisible()
      await expect(page.getByRole('textbox', { name: 'Hauptsachtitel' })).toBeEnabled()
      await expect(page.getByRole('textbox', { name: 'Dokumentarischer Titel' })).toHaveCount(0)
    },
  )
})
