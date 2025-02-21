import { test, expect } from '@playwright/test'

test.describe('Rubriken page: Langüberschrift', () => {
  test(
    'Data of Langüberschrift persists during reload',
    { tag: ['@RISDEV-6213'] },
    async ({ page }) => {
      // given
      const myLangueberschrift = 'my persisting Langüberschrift'
      await page.goto('/')
      await page.getByText('Neue Dokumentationseinheit').click()
      await page.getByText('Rubriken').click()
      await expect(page.getByText('Amtl. Langüberschrift')).toHaveCount(1)
      await page.getByText('Amtl. Langüberschrift').fill(myLangueberschrift)
      await expect(page.getByText('Amtl. Langüberschrift')).toHaveValue(myLangueberschrift)
      // when
      await page.reload()
      // then
      await expect(page.getByText('Amtl. Langüberschrift')).toHaveValue(myLangueberschrift)
    },
  )
})
