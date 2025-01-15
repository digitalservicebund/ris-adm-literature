import {expect, test} from '@playwright/test'

// See here how to get started:
// https://playwright.dev/docs/intro
test.describe('FundstellenPage', () => {

  test(
    'Visiting the app root url and clicking "Neue Dokumentationseinheit", the view shows the title "Fundstellen", two input fields, a sidebar navigation panel and a save button',
    {tag: ['@RISDEV-6042']},
    async ({page}) => {
      // Arrange
      await page.goto('/')

      // Action
      await page.getByRole('button', {name: 'Neue Dokumentationseinheit'}).click()

      // Assert
      expect(page.url()).toMatch(/.*\/fundstellen$/)
      await expect(page.getByText('Fundstellen')).toHaveCount(2)
      await expect(page.getByText('Fundstellen')).toHaveCount(2) // nav bar + title
      await expect(page.getByText('KSNR054920707')).toHaveCount(1)
      await expect(page.getByText('Platzhaltertext')).toHaveCount(1)
      await expect(page.getByText('Unveröffentlicht')).toHaveCount(1)
      await expect(page.getByText('Fundstellen')).toHaveCount(2)
      await expect(page.getByTestId('save-button')).toHaveCount(1)
      await expect(page.getByText('Periodikum')).toHaveCount(1)
      await expect(page.getByText('Zitatstelle')).toHaveCount(1)
      await expect(page.getByRole("textbox")).toHaveCount(2)
    })

  test(
    'Visiting the "Fundstellen" page and add a fundstelle',
    {tag: ['@RISDEV-6042']},
    async ({page}) => {
      // Arrange
      await page.goto('/documentUnit/KSNR054920707/fundstellen')

      // Action
      await page.getByRole('button', {name: 'Dropdown öffnen'}).click()
      await page.getByText('BAnz | Bundesanzeiger').click()
      await page.getByLabel('Zitatstelle').fill('2024, Seite 24')
      await page.getByText('Übernehmen').click()

      // Assert
      await expect(page.getByText('BAnz 2024, Seite 24')).toHaveCount(1)
    },
  )

  test(
    'Visiting the "Fundstellen" page, add a fundstelle, edit and close',
    {tag: ['@RISDEV-6042']},
    async ({page}) => {
      // Arrange
      await page.goto('/documentUnit/KSNR054920707/fundstellen')

      // Action
      await page.getByRole('button', {name: 'Dropdown öffnen'}).click()
      await page.getByText('AA | Arbeitsrecht aktiv').click()
      await page.getByLabel('Zitatstelle').fill('1991, Seite 92')
      await page.getByText('Übernehmen').click()
      await page.getByTestId('list-entry-0').click()
      await page.getByText('Abbrechen').click()

      // Assert
      await expect(page.getByText('AA 1991, Seite 92')).toHaveCount(1)
    },
  )

  test(
    'Visiting the "Fundstellen" page, add two item of fundstelle, delete the first item',
    {tag: ['@RISDEV-6042']},
    async ({page}) => {
      // Arrange
      await page.goto('/documentUnit/KSNR054920707/fundstellen')

      // Action
      await page.getByRole('button', {name: 'Dropdown öffnen'}).click()
      await page.getByText('AA | Arbeitsrecht aktiv').click()
      await page.getByLabel('Zitatstelle').fill('1991, Seite 92')
      await page.getByText('Übernehmen').click()

      await page.getByRole('button', {name: 'Dropdown öffnen'}).click()
      await page.getByText('BAnz | Bundesanzeiger').click()
      await page.getByLabel('Zitatstelle').fill('2001, Seite 21')
      await page.getByText('Übernehmen').click()

      await page.getByTestId('list-entry-0').click()
      await page.getByText('Eintrag löschen').click()

      // Assert
      await expect.soft(page.getByText('AA 1991, Seite 92')).toHaveCount(0)
      await expect(page.getByText('BAnz 2001, Seite 21')).toHaveCount(1)
    },
  )
})
