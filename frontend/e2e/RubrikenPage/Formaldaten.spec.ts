import { test, expect } from '@playwright/test'

test.describe('RubrikenPage', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/documentation-units/KSNR054920707', async (route) => {
      const json = {
        documentNumber: 'KSNR054920707',
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        json: null,
      }
      await route.fulfill({ json })
    })
  })

  test(
    'Visiting the Formaldaten step of creating a documentUnit',
    { tag: ['@RISDEV-6043'] },
    async ({ page }) => {
      await page.goto('/documentUnit/KSNR054920707/fundstellen')
      await page.getByText('Rubriken').click()
      await expect(page.getByText('Rubriken')).toHaveCount(1)

      await expect(page.getByText('Formaldaten')).toHaveCount(2)
      await expect(page.getByText('Zitierdatum')).toHaveCount(1)
      await page.getByText('Zitierdatum').fill('thatshouldnotwork')
      await expect(page.getByText('Zitierdatum')).toHaveValue('')
      await page.getByText('Zitierdatum').fill('15.01.2025')
      await expect(page.getByText('Zitierdatum')).toHaveValue('15.01.2025')

      await expect(page.getByText('Normgeber')).toHaveCount(1)
      await page.getByText('Normgeber').fill('AG')
      await expect(page.getByText('AG Aachen')).toHaveCount(1)
      await page.getByText('AG Aachen').click()
      await expect(page.getByText('Normgeber')).toHaveValue('AG Aachen')

      await expect(page.getByText('Amtl. Langüberschrift')).toHaveCount(1)
      await page.getByText('Amtl. Langüberschrift').fill('my long title')
      await expect(page.getByText('Amtl. Langüberschrift')).toHaveValue('my long title')

      await expect(page.getByText('Dokumenttyp *')).toHaveCount(1)
      await page.getByText('Dokumenttyp *').fill('V')
      await expect(page.getByText('VR')).toHaveCount(1)
      await page.getByText('VR').click()
      await expect(page.getByText('Dokumenttyp *')).toHaveValue('VR') // confirm selection by value

      await expect(page.getByText('Dokumenttyp Zusatz')).toHaveCount(1)
      await page.getByText('Dokumenttyp Zusatz').fill('Bekanntmachung')
      await expect(page.getByText('Dokumenttyp Zusatz')).toHaveValue('Bekanntmachung')

      await expect(page.getByText('Datum des Inkrafttretens *')).toHaveCount(1)
      await page.getByText('Zitierdatum').fill('thatshouldnotwork')
      await expect(page.getByText('Zitierdatum')).toHaveValue('')
      await page.getByText('Datum des Inkrafttretens *').fill('02.02.1970')
      await expect(page.getByText('Datum des Inkrafttretens *')).toHaveValue('02.02.1970')

      await expect(page.getByText('Datum des Ausserkrafttretens')).toHaveCount(1)
      await page.getByText('Datum des Ausserkrafttretens').fill('thatshouldnotwork')
      await expect(page.getByText('Datum des Ausserkrafttretens')).toHaveValue('')
      await page.getByText('Datum des Ausserkrafttretens').fill('03.03.1970')
      await expect(page.getByText('Datum des Ausserkrafttretens')).toHaveValue('03.03.1970')

      await expect(page.getByText('Aktenzeichen *')).toHaveCount(2)
      await page.getByText('Aktenzeichen *').first().fill('Az1')
      await page.getByText('Aktenzeichen *').first().press('Enter')
      await page.getByText('Aktenzeichen *').first().fill('Az2')
      await page.getByText('Aktenzeichen *').first().press('Enter')
      // Created elements are list elements (<li>) so we need to select them explicitly
      await expect(page.getByText('Az1')).toHaveCount(1)
      await expect(page.getByText('Az2')).toHaveCount(1)

      await expect(page.getByText('Kein Aktenzeichen')).toHaveCount(1)
      await page.getByText('Kein Aktenzeichen').check()
      await expect(page.getByText('Kein Aktenzeichen')).toBeChecked()
    },
  )
})
