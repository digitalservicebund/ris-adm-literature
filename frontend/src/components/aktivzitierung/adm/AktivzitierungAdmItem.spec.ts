import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm'
import AktivzitierungAdmItem from './AktivzitierungAdmItem.vue'

const baseItem = { id: 'test-id' }

const testCases = [
  {
    name: 'should render all fields separated by comma',
    data: {
      ...baseItem,
      citationType: 'VV',
      normgeber: 'BMJ',
      inkrafttretedatum: '2024-01-01',
      aktenzeichen: 'Az 123',
      periodikum: 'BGBl',
      zitatstelle: 'I S. 10',
      dokumenttyp: 'VO',
      documentNumber: '123-A',
    },
    expected: 'VV, BMJ, 01.01.2024, Az 123, BGBl I S. 10 (VO) | 123-A',
  },
  {
    name: 'should render only citationType and documentNumber (ignoring citationType if docNum missing)',
    data: {
      ...baseItem,
      citationType: 'VwV',
      documentNumber: '456-B',
    },
    expected: 'VwV | 456-B',
  },
  {
    name: 'should render documentNumber only if citationType is provided but docNum is empty',
    data: {
      ...baseItem,
      citationType: 'VwV',
      documentNumber: '',
    },
    expected: '',
  },
  {
    name: 'should handle fundstelle with periodikum only',
    data: {
      ...baseItem,
      documentNumber: '123',
      periodikum: 'NJW',
    },
    expected: 'NJW | 123',
  },
  {
    name: 'should handle fundstelle with zitatstelle only',
    data: {
      ...baseItem,
      documentNumber: '123',
      zitatstelle: 'S. 50',
    },
    expected: 'S. 50 | 123',
  },
  {
    name: 'should render dokumenttyp in parentheses when no fundstelle exists',
    data: {
      ...baseItem,
      documentNumber: '123',
      dokumenttyp: 'Gesetz',
    },
    expected: '(Gesetz) | 123',
  },
  {
    name: 'should render dokumenttyp and fundstelle combined',
    data: {
      ...baseItem,
      documentNumber: '123',
      periodikum: 'BGBl',
      dokumenttyp: 'Anordnung',
    },
    expected: 'BGBl (Anordnung) | 123',
  },
  {
    name: 'should render aktenzeichen without other basic parts',
    data: {
      ...baseItem,
      documentNumber: '123',
      aktenzeichen: 'II ZR 12/23',
    },
    expected: 'II ZR 12/23 | 123',
  },
  {
    name: 'should render empty string when all fields including docNum are missing',
    data: {
      id: 'uuid',
      documentNumber: '',
    },
    expected: '',
  },
  {
    name: 'should render documentNumber alone if no meta parts exist',
    data: {
      ...baseItem,
      documentNumber: 'ONLY-NUM',
    },
    expected: 'ONLY-NUM',
  },
]

describe('AktivzitierungAdmItem (metaSummary)', () => {
  it.each(testCases)('$name', ({ data, expected }) => {
    render(AktivzitierungAdmItem, {
      props: { aktivzitierung: data as AktivzitierungAdm },
    })

    if (expected) {
      expect(screen.getByText(expected)).toBeInTheDocument()
    }
  })

  it('shows the langueberschrift', () => {
    render(AktivzitierungAdmItem, {
      props: {
        aktivzitierung: { id: 'id-1', documentNumber: 'doc1', langueberschrift: 'TheTitle' },
      },
    })

    expect(screen.getByText('TheTitle')).toBeInTheDocument()
  })
})
