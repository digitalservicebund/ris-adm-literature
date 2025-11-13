import { requiredAdmDocUnitFields, type AdmDocumentationUnit } from '@/domain/adm/admDocumentUnit'
import type { UliDocumentationUnit } from '@/domain/uli/uliDocumentUnit'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
dayjs.extend(customParseFormat)

// Checks if all provided date strings are valid according to the 'DD.MM.YYYY' format
export function areDatesValid(dates: string[]): boolean {
  return dates.every((d) => dayjs(d, 'DD.MM.YYYY', true).isValid())
}

// Returns an error message listing any invalid dates in the array
export function getInvalidDateErrMessage(dates: string[]): string {
  const invalidDates = dates.filter((d) => !dayjs(d, 'DD.MM.YYYY', true).isValid())
  return invalidDates.length > 0 ? `Kein valides Datum: ${invalidDates.join(', ')}` : ''
}

// Returns an error message listing any future dates in the array
export function getFutureDateErrMessage(dates: string[]): string {
  const futureDates = dates.filter((d) => dayjs(d, 'DD.MM.YYYY', true).isAfter(dayjs()))
  return futureDates.length > 0
    ? `Das Datum darf nicht in der Zukunft liegen: ${futureDates.join(', ')}`
    : ''
}

export function isBlank(text?: string): boolean {
  return !text || text.trim() === ''
}

// Returns a list of missing adm required fields
export function missingAdmDocumentUnitFields(doc: AdmDocumentationUnit): string[] {
  return requiredAdmDocUnitFields.filter((field) => {
    const value = doc[field]
    return !value || (Array.isArray(value) && value.length === 0)
  })
}

// Returns a list of missing uli required fields
export function missingUliDocumentUnitFields(doc: UliDocumentationUnit): string[] {
  const missingFields: string[] = []

  if (!doc.dokumenttypen?.length) {
    missingFields.push('dokumenttypen')
  }

  if (isBlank(doc.veroeffentlichungsjahr)) {
    missingFields.push('veroeffentlichungsjahr')
  }

  if (isBlank(doc.hauptsachtitel) && isBlank(doc.dokumentarischerTitel)) {
    missingFields.push('hauptsachtitel')
  }

  return missingFields
}
