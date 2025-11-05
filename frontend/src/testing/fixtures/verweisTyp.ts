import { VerweisTypEnum } from '@/domain/activeReference'
import type { VerweisTyp } from '@/domain/verweisTyp'

export const anwendungFixture: VerweisTyp = {
  id: 'anwendungTestId',
  name: VerweisTypEnum.ANWENDUNG,
}

export const neuregelungFixture: VerweisTyp = {
  id: 'neuregelungTestId',
  name: VerweisTypEnum.NEUREGELUNG,
}

export const rechtsgrundlageFixture: VerweisTyp = {
  id: 'rechtsgrundlageTestId',
  name: VerweisTypEnum.RECHTSGRUNDLAGE,
}
