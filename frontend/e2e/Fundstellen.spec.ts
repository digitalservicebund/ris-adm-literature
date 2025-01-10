import { test } from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test(
  'Visiting the app root url and clicking "Neue Dokumentationseinheit", the view shows the title "Fundstellen", two input fields, a sidebar navigation panel and a save button',
  { tag: ['@RISDEV-6042'] },
  async ({ page }) => {
    // given
    await page.goto('/')
    // when
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
    // then

    // TODO #RISDEV-6042
    // await expect(page.getByText('Fundstellen')).toHaveCount(2) // nav bar + title

    // await expect(page.getByText('KSNR054920707')).toHaveCount(1)
    // await expect(page.getByText('Platzhaltertext')).toHaveCount(1)
    // await expect(page.getByText('Unveröffentlicht')).toHaveCount(1)
    // await expect(page.getByText('Fundstellen')).toHaveCount(1)
    // await expect(page.getByRole('button', { name: 'Speichern' })).toHaveCount(1)

    // await expect(page.getByText('Periodikum')).toHaveCount(1)
    // await expect(page.getByText('Zitierstelle')).toHaveCount(1)

    // const optionElement = page.getByRole('option')
    // await expect(optionElement).toHaveCount(1)
    // expect(optionElement.selectOption({ label: "BAnz" }))

    // await expect(page.getByRole("textbox")).toHaveCount(1)
  },
)
