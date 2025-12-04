import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import AktivzitierungSearchResult from './AktivzitierungSearchResult.vue'
import { sliDocUnitListItemFixture } from '@/testing/fixtures/sliDocumentUnit.fixture'
import type { SliDocUnitListItem } from '@/domain/sli/sliDocumentUnit.ts'

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
          titel: 'TheHauptTitle',
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
          titel: 'Book without known author',
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
          titel: 'Report by a known group',
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
          titel: 'Minimal Entry',
        },
      },
    })

    expect(screen.getByText('DOC-ONLY')).toBeInTheDocument()
    expect(screen.getByText('Minimal Entry')).toBeInTheDocument()
  })

  it('emits add with the searchResult when clicking the + button', async () => {
    const user = userEvent.setup()
    const searchResult = {
      id: 'id-add',
      documentNumber: 'DOC-ADD',
      veroeffentlichungsjahr: '2025',
      titel: 'Some title',
      verfasser: ['Name 1'],
    }

    const { emitted } = render(AktivzitierungSearchResult, {
      props: { searchResult },
    })

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' }))

    expect(emitted().add).toBeTruthy()
    const payload = (emitted().add as [SliDocUnitListItem[]])[0][0]
    expect(payload!.documentNumber).toBe('DOC-ADD')
  })
})
