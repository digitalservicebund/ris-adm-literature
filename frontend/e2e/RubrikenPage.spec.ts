import { test, expect } from '@playwright/test'

test(
  'Visiting the Rubriken step of creating a documentUnit',
  { tag: ['@RISDEV-6043', '@RISDEV-6047'] },
  async ({ page }) => {
    // In the future the new documentUnit needs to be mocked
    // As this functionality is not there yet we can simply enter the desired page and finish the process
    await page.goto('/')
    await page.getByRole('button', { name: 'Neue Dokumentationseinheit' }).click()
    await page.getByText('Rubriken').click()
    await expect(page.getByText('Rubriken')).toHaveCount(1)

    //////////////
    // Formaldaten
    //////////////
    await expect(page.getByText('Formaldaten')).toHaveCount(2)
    const zitierdatumElement = page.getByText('Zitierdatum')
    await expect(zitierdatumElement).toHaveCount(1)
    zitierdatumElement.fill('thatshouldnotwork')
    await expect(zitierdatumElement).toHaveValue('')
    zitierdatumElement.fill('15.01.2025')
    await expect(zitierdatumElement).toHaveValue('15.01.2025')

    const normgeberElement = page.getByText('Normgeber')
    await expect(normgeberElement).toHaveCount(1)
    normgeberElement.fill('AG')
    await expect(page.getByText('AG Aachen')).toHaveCount(1)
    await page.getByText('AG Aachen').click()
    await expect(normgeberElement).toHaveValue('AG Aachen')

    const amtlicheLangüberschriftElement = page.getByText('Amtl. Langüberschrift')
    await expect(amtlicheLangüberschriftElement).toHaveCount(1)
    amtlicheLangüberschriftElement.fill('my long title')
    await expect(amtlicheLangüberschriftElement).toHaveValue('my long title')

    const dokumentTyp = page.getByText('Dokumenttyp *')
    await expect(dokumentTyp).toHaveCount(1)
    await dokumentTyp.fill('V')
    await expect(page.getByText('VR')).toHaveCount(1)
    await page.getByText('VR').click()
    await expect(dokumentTyp).toHaveValue('VR') // confirm selection by value

    const dokumentTypZusatz = page.getByText('Dokumenttyp Zusatz')
    await expect(dokumentTypZusatz).toHaveCount(1)
    await dokumentTypZusatz.fill('Bekanntmachung')
    await expect(dokumentTypZusatz).toHaveValue('Bekanntmachung')

    const inkrafttretedatumElement = page.getByText('Datum des Inkrafttretens *')
    await expect(inkrafttretedatumElement).toHaveCount(1)
    zitierdatumElement.fill('thatshouldnotwork')
    await expect(zitierdatumElement).toHaveValue('')
    inkrafttretedatumElement.fill('02.02.1970')
    await expect(inkrafttretedatumElement).toHaveValue('02.02.1970')

    const ausserkrafttretedatumElement = page.getByText('Datum des Ausserkrafttretens')
    await expect(ausserkrafttretedatumElement).toHaveCount(1)
    ausserkrafttretedatumElement.fill('thatshouldnotwork')
    await expect(ausserkrafttretedatumElement).toHaveValue('')
    ausserkrafttretedatumElement.fill('03.03.1970')
    await expect(ausserkrafttretedatumElement).toHaveValue('03.03.1970')

    const aktenzeichenElement = page.getByText('Aktenzeichen *')
    await expect(aktenzeichenElement).toHaveCount(1)
    await aktenzeichenElement.fill('Az1')
    await aktenzeichenElement.press('Enter')
    await aktenzeichenElement.fill('Az2')
    await aktenzeichenElement.press('Enter')
    // Created elements are list elements (<li>) so we need to select them explicitly
    await expect(page.getByText('Az1')).toHaveCount(1)
    await expect(page.getByText('Az2')).toHaveCount(1)

    const keinAktenzeichenElement = page.getByText('Kein Aktenzeichen')
    await expect(keinAktenzeichenElement).toHaveCount(1)
    keinAktenzeichenElement.check()
    await expect(keinAktenzeichenElement).toBeChecked()

    /////////////////
    // Schlagwörter
    /////////////////
    const schlagwoerterHeadingElement = page.getByText('Schlagwörter')
    await expect(schlagwoerterHeadingElement).toHaveCount(2) // two headings

    // enter single schlagwort
    const schlagwoerterListEditElement = page.getByTestId('Schlagwörter_ListInputEdit')
    await schlagwoerterListEditElement.click()
    await schlagwoerterListEditElement.fill('Schlagwort 1')
    const schlagwoerterUebernehmenElement = page.getByText('Übernehmen')
    await schlagwoerterUebernehmenElement.click()
    await expect(page.getByText('Schlagwort 1')).toHaveCount(1)

    // add another schlagwort
    const schlagwoerterBearbeitenElement = page.getByText('Schlagwörter bearbeiten')
    await schlagwoerterBearbeitenElement.click()

    await schlagwoerterListEditElement.click()
    await schlagwoerterListEditElement.press('End')
    await schlagwoerterListEditElement.press('Enter')
    await schlagwoerterListEditElement.pressSequentially('Schlagwort 2')
    await schlagwoerterListEditElement.press('Enter')
    await schlagwoerterUebernehmenElement.click()

    await expect(page.getByText('Schlagwort 1')).toHaveCount(1)
    await expect(page.getByText('Schlagwort 2')).toHaveCount(1)
  },
)
