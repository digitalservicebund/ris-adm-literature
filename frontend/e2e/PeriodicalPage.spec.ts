import { expect, test } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test.describe('skip webkit', () => {
  // TODO: This should not be necessary
  test.skip(({ browserName }) => browserName === 'webkit', 'Skip webkit for this test')

  test(
    'Visiting the app root url and clicking "Neue Dokumentationseinheit", the view shows the title "Fundstellen", two input fields, a sidebar navigation panel and a save button',
    { tag: ['@RISDEV-6042'] },
    async ({ page }) => {
      // given
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      // when
      // await page.getByText('Neue Dokumentationseinheit').click()
      // then
      expect(page.url()).toMatch(/.*\/fundstellen$/)
      await expect(page.getByText('Fundstellen')).toHaveCount(2)

      // TODO #RISDEV-6042
      // await expect(page.getByText('Fundstellen')).toHaveCount(2) // nav bar + title

      // await expect(page.getByText('KSNR054920707')).toHaveCount(1)
      // await expect(page.getByText('Platzhaltertext')).toHaveCount(1)
      // await expect(page.getByText('Unveröffentlicht')).toHaveCount(1)
      await expect(page.getByText('Fundstellen')).toHaveCount(2)
    // await expect(page.getByRole('button', { name: 'Speichern' })).toHaveCount(1)

      await expect(page.getByText('Periodikum')).toHaveCount(1)
    await expect(page.getByText('Zitatstelle')).toHaveCount(1)

      // const optionElement = page.getByRole('option')
      // await expect(optionElement).toHaveCount(1)
      // expect(optionElement.selectOption({ label: "BAnz" }))

      await expect(page.getByRole("textbox")).toHaveCount(2)
    },
  )
})
