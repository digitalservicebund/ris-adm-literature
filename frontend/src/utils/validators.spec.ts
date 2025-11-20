import { describe, it, expect } from 'vitest'
import {
  areDatesValid,
  getFutureDateErrMessage,
  getInvalidDateErrMessage,
  missingAdmDocumentUnitFields,
  missingUliDocumentUnitFields,
  missingSliDocumentUnitFields,
} from './validators'
import dayjs from 'dayjs'
import type { AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import { docTypeAnordnungFixture } from '@/testing/fixtures/documentType.fixture'
import type { SliDocumentationUnit } from '@/domain/sli/sliDocumentUnit'

describe('Validators functions', () => {
  describe('areDatesValid', () => {
    it('should return true for an array of valid dates', () => {
      const validDates = ['01.01.2023', '31.12.2023', '15.06.2023']
      expect(areDatesValid(validDates)).toBe(true)
    })

    it('should return false for an array containing invalid dates', () => {
      const invalidDates = ['01.01.2023', '31-12-2023', '15.06.2023']
      expect(areDatesValid(invalidDates)).toBe(false)
    })

    it('should return true for an empty array', () => {
      expect(areDatesValid([])).toBe(true)
    })
  })

  describe('getInvalidDateMessage', () => {
    it('should return an error message for an array containing invalid dates', () => {
      const datesWithInvalid = ['01.01.2023', '31-12-2023', '15.06.2023']
      const expectedMessage = 'Kein valides Datum: 31-12-2023'
      expect(getInvalidDateErrMessage(datesWithInvalid)).toBe(expectedMessage)
    })

    it('should return an empty string for an array of valid dates', () => {
      const validDates = ['01.01.2023', '31.12.2023', '15.06.2023']
      expect(getInvalidDateErrMessage(validDates)).toBe('')
    })

    it('should return an empty string for an empty array', () => {
      expect(getInvalidDateErrMessage([])).toBe('')
    })
  })

  describe('getFutureDateErrMessage', () => {
    it('should return an error message for an array containing future dates', () => {
      const futureDate = dayjs().add(1, 'day').format('DD.MM.YYYY')
      const dates = ['01.01.1970', futureDate]

      const expectedMessage = `Das Datum darf nicht in der Zukunft liegen: ${futureDate}`
      expect(getFutureDateErrMessage(dates)).toBe(expectedMessage)
    })

    it('should return an empty string for an array of past dates', () => {
      const today = dayjs()
      const pastDate1 = today.subtract(1, 'day').format('DD.MM.YYYY')
      const pastDate2 = today.subtract(2, 'day').format('DD.MM.YYYY')
      const dates = [pastDate1, pastDate2]

      expect(getFutureDateErrMessage(dates)).toBe('')
    })

    it('should return an empty string for an empty array', () => {
      expect(getFutureDateErrMessage([])).toBe('')
    })

    it('should return an error message for an array with mixed past and future dates', () => {
      const today = dayjs()
      const pastDate = today.subtract(1, 'day').format('DD.MM.YYYY')
      const futureDate1 = today.add(1, 'day').format('DD.MM.YYYY')
      const futureDate2 = today.add(2, 'day').format('DD.MM.YYYY')
      const dates = [pastDate, futureDate1, futureDate2]

      const expectedMessage = `Das Datum darf nicht in der Zukunft liegen: ${futureDate1}, ${futureDate2}`
      expect(getFutureDateErrMessage(dates)).toBe(expectedMessage)
    })
  })

  describe('validateDocumentUnit', () => {
    it('should return a list of missing fields', () => {
      const doc: AdmDocumentationUnit = {
        id: 'testDocId1',
        documentNumber: 'testDocNumber1',
        note: '',
        langueberschrift: 'this is a langueberschrift',
        inkrafttretedatum: '',
        zitierdaten: [],
      }

      const actual = missingAdmDocumentUnitFields(doc)
      const expected = ['inkrafttretedatum', 'dokumenttyp', 'zitierdaten', 'normgeberList']

      expect(expected.sort()).toEqual(actual.sort())
    })
  })

  describe('missingUliDocumentUnitFields', () => {
    it('returns empty array if all fields are present', () => {
      const doc = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSLU054920707',
        note: '',
        veroeffentlichungsjahr: '2025',
        dokumenttypen: [docTypeAnordnungFixture],
        hauptsachtitel: 'Title',
        dokumentarischerTitel: 'DocTitle',
      }
      expect(missingUliDocumentUnitFields(doc)).toEqual([])
    })

    it('detects missing veroeffentlichungsjahr', () => {
      const doc = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSLU054920707',
        note: '',
        veroeffentlichungsjahr: '   ',
        dokumenttypen: [docTypeAnordnungFixture],
        hauptsachtitel: 'Title',
        dokumentarischerTitel: 'DocTitle',
      }
      expect(missingUliDocumentUnitFields(doc)).toEqual(['veroeffentlichungsjahr'])
    })

    it('detects missing dokumenttypen', () => {
      const doc = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSLU054920707',
        note: '',
        veroeffentlichungsjahr: '2025',
        dokumenttypen: [],
        hauptsachtitel: 'Title',
        dokumentarischerTitel: 'DocTitle',
      }
      expect(missingUliDocumentUnitFields(doc)).toEqual(['dokumenttypen'])
    })

    it('detects missing hauptsachtitel and dokumentarischerTitel', () => {
      const doc = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSLU054920707',
        note: '',
        veroeffentlichungsjahr: '2025',
        dokumenttypen: [docTypeAnordnungFixture],
        hauptsachtitel: ' ',
        dokumentarischerTitel: '',
      }
      expect(missingUliDocumentUnitFields(doc)).toEqual(['hauptsachtitel'])
    })

    it('detects multiple missing fields', () => {
      const doc = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KSLU054920707',
        note: '',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        hauptsachtitel: '',
        dokumentarischerTitel: '',
      }
      expect(missingUliDocumentUnitFields(doc)).toEqual([
        'dokumenttypen',
        'veroeffentlichungsjahr',
        'hauptsachtitel',
      ])
    })
  })

  describe('missingSliDocumentUnitFields', () => {
    describe('veroeffentlichungsjahr', () => {
      it('returns empty array if veroeffentlichungsjahr is present', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual([])
      })

      it('detects missing veroeffentlichungsjahr when empty string', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['veroeffentlichungsjahr'])
      })

      it('detects missing veroeffentlichungsjahr when whitespace only', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '   ',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['veroeffentlichungsjahr'])
      })

      it('detects missing veroeffentlichungsjahr when undefined', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['veroeffentlichungsjahr'])
      })
    })

    describe('dokumenttypen', () => {
      it('detects missing dokumenttypen when empty array', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['dokumenttypen'])
      })

      it('detects missing dokumenttypen when undefined', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['dokumenttypen'])
      })

      it('returns empty array if dokumenttypen is present', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual([])
      })
    })

    describe('hauptsachtitel and dokumentarischerTitel', () => {
      it('detects missing when both are empty', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: '',
          dokumentarischerTitel: '',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['hauptsachtitel'])
      })

      it('detects missing when both are whitespace only', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: '   ',
          dokumentarischerTitel: '  ',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual(['hauptsachtitel'])
      })

      it('returns empty array if hauptsachtitel is present', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual([])
      })

      it('returns empty array if dokumentarischerTitel is present', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          dokumentarischerTitel: 'Doc Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual([])
      })

      it('returns empty array if both are present', () => {
        const doc: SliDocumentationUnit = {
          id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
          documentNumber: 'KALS2025000001',
          note: '',
          veroeffentlichungsjahr: '2025',
          dokumenttypen: [docTypeAnordnungFixture],
          hauptsachtitel: 'Title',
          dokumentarischerTitel: 'Doc Title',
        }
        expect(missingSliDocumentUnitFields(doc)).toEqual([])
      })
    })

    it('detects multiple missing fields', () => {
      const doc: SliDocumentationUnit = {
        id: '8de5e4a0-6b67-4d65-98db-efe877a260c4',
        documentNumber: 'KALS2025000001',
        note: '',
        veroeffentlichungsjahr: '',
        dokumenttypen: [],
        hauptsachtitel: '',
        dokumentarischerTitel: '',
      }
      expect(missingSliDocumentUnitFields(doc)).toEqual([
        'dokumenttypen',
        'veroeffentlichungsjahr',
        'hauptsachtitel',
      ])
    })
  })
})
