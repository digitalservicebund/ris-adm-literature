import type { Fundstelle } from '@/domain/fundstelle'
import { bundesanzeigerFixture } from './periodikum.fixture'

export const fundstelleFixture: Fundstelle = {
  id: 'fundstelleTestId',
  zitatstelle: '1973, 608',
  periodikum: bundesanzeigerFixture,
}

export const ambiguousFundstelleFixture: Fundstelle = {
  id: 'fundstelleTestId',
  zitatstelle: '1973, 608',
  periodikum: undefined,
  ambiguousPeriodikum: 'BAnz',
}
