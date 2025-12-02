import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import AktivzitierungSearchResult from './AktivzitierungSearchResult.vue'
import { sliDocUnitListItemFixture } from '@/testing/fixtures/sliDocumentUnit.fixture'

describe('Aktivzitierung search result', () => {
  it('renders correctly', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: sliDocUnitListItemFixture,
      },
    })

    expect(screen.getByText('2025 | KSLS054920710')).toBeInTheDocument()
  })

  it('renders correctly all fields (Year, Authors, DocNum, Title)', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: 'searchResultTestId',
          documentNumber: 'KSLS054920710',
          veroeffentlichungsjahr: '2025',
          hauptsachtitel: 'TheHauptTitle',
          verfasser: ['Müller', 'Zimmermann'],
        },
      },
    })

    expect(screen.getByText('2025, Müller, Zimmermann | KSLS054920710')).toBeInTheDocument()
    expect(screen.getByText('TheHauptTitle')).toBeInTheDocument()
  })

  it('renders correctly when authors (verfasser) are missing', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: 'id1',
          documentNumber: 'DOC-12345',
          veroeffentlichungsjahr: '2023',
          hauptsachtitel: 'Book without known author',
          verfasser: [],
        },
      },
    })

    expect(screen.getByText('2023 | DOC-12345')).toBeInTheDocument()
    expect(screen.getByText('Book without known author')).toBeInTheDocument()
  })

  it('renders correctly when publication year (veroeffentlichungsjahr) is missing', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: 'id2',
          documentNumber: 'DOC-67890',
          hauptsachtitel: 'Report by a known group',
          verfasser: ['Research Team A'],
        },
      },
    })

    expect(screen.getByText('Research Team A | DOC-67890')).toBeInTheDocument()
    expect(screen.getByText('Report by a known group')).toBeInTheDocument()
  })

  it('renders only documentNumber when year and authors are missing', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: 'id3',
          documentNumber: 'DOC-ONLY',
          hauptsachtitel: 'Minimal Entry',
        },
      },
    })

    expect(screen.getByText('DOC-ONLY')).toBeInTheDocument()
    expect(screen.getByText('Minimal Entry')).toBeInTheDocument()
  })

  it('renders documentarischerTitel when hauptsachtitel is missing', () => {
    render(AktivzitierungSearchResult, {
      props: {
        searchResult: {
          id: 'id4',
          documentNumber: 'DOC-FALLBACK',
          veroeffentlichungsjahr: '2020',
          dokumentarischerTitel: 'A Backup Documentary Title',
        },
      },
    })

    expect(screen.getByText('2020 | DOC-FALLBACK')).toBeInTheDocument()
    expect(screen.getByText('A Backup Documentary Title')).toBeInTheDocument()
  })
})
