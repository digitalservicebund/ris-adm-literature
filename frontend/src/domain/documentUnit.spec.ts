import { describe, it, expect } from 'vitest'
import DocumentUnit from './documentUnit'
import Reference from './reference'
import LegalPeriodical from './legalPeriodical'

describe('DocumentUnit', () => {
  it('instantiates with uuid and documentNumber', () => {
    // given when
    const documentUnit = new DocumentUnit({ uuid: 'foo', documentNumber: 'KSNR054920707' })

    // then
    expect(documentUnit.uuid).toEqual('foo')
    expect(documentUnit.documentNumber).toEqual('KSNR054920707')
  })

  it('sets a reference', () => {
    // given when
    const documentUnit = new DocumentUnit({
      uuid: 'foo',
      documentNumber: 'KSNR054920707',
      references: [
        new Reference({
          legalPeriodical: new LegalPeriodical({
            title: 'Arbeitsrecht aktiv',
            abbreviation: 'AA',
          }),
          citation: '12345',
        }),
      ],
    })

    // then
    expect(documentUnit.references).toHaveLength(1)
    expect(documentUnit.references?.at(0)?.legalPeriodical?.title).toEqual('Arbeitsrecht aktiv')
    expect(documentUnit.references?.at(0)?.legalPeriodical?.abbreviation).toEqual('AA')
    expect(documentUnit.references?.at(0)?.citation).toEqual('12345')
  })
})
