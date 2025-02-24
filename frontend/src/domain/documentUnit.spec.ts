import { describe, it, expect } from 'vitest'
import { type DocumentUnit } from './documentUnit'
import Reference from './reference'
import LegalPeriodical from './legalPeriodical'

describe('DocumentUnit', () => {
  it('instantiates with id and documentNumber', () => {
    // given when
    const documentUnit: DocumentUnit = {
      id: 'foo',
      documentNumber: 'KSNR054920707',
      fieldsOfLaw: [],
      references: [],
    }

    // then
    expect(documentUnit.id).toEqual('foo')
    expect(documentUnit.documentNumber).toEqual('KSNR054920707')
  })

  it('sets a reference', () => {
    // given when
    const documentUnit: DocumentUnit = {
      id: 'foo',
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
      fieldsOfLaw: [],
    }

    // then
    expect(documentUnit.references).toHaveLength(1)
    expect(documentUnit.references?.at(0)?.legalPeriodical?.title).toEqual('Arbeitsrecht aktiv')
    expect(documentUnit.references?.at(0)?.legalPeriodical?.abbreviation).toEqual('AA')
    expect(documentUnit.references?.at(0)?.citation).toEqual('12345')
  })
})
