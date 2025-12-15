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
      periodikum: 'BGBI',
      dokumenttyp: 'VO',
      documentNumber: '123-A',
    },
    expected: 'VV, BMJ, 01.01.2024, BGBI, VO, 123-A',
  },
  {
    name: 'should render only citationType and documentNumber',
    data: {
      ...baseItem,
      citationType: 'VwV',
      documentNumber: '456-B',
      normgeber: null,
      periodikum: undefined,
    },
    expected: 'VwV, 456-B',
  },
  {
    name: 'should render only date and periodikum',
    data: {
      ...baseItem,
      inkrafttretedatum: '2023-05-15',
      periodikum: 'GBl',
      documentNumber: '',
    },
    expected: '15.05.2023, GBl',
  },
  {
    name: 'should render empty string when all fields are missing',
    data: {
      ...baseItem,
      citationType: null,
      normgeber: undefined,
      documentNumber: '',
    },
    expected: '',
  },
  {
    name: 'should render only normgeber',
    data: {
      ...baseItem,
      normgeber: 'BMBF',
    },
    expected: 'BMBF',
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
})
