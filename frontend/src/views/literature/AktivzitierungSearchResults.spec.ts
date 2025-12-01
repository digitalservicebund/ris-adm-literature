import { render, screen, within } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import AktivzitierungSearchResults from './AktivzitierungSearchResults.vue'
import { sliDocUnitListItemFixture } from '@/testing/fixtures/sliDocumentUnit.fixture'

describe('Aktivzitierung search results', () => {
  it('renders 1 row', () => {
    render(AktivzitierungSearchResults, {
      props: {
        searchResults: [sliDocUnitListItemFixture],
        isLoading: false,
      },
    })

    const resultsList = screen.getByRole('list')
    expect(resultsList).toBeInTheDocument()

    const listItems = within(resultsList).getAllByRole('listitem')
    expect(listItems).toHaveLength(1)
  })

  it('renders an empty message', () => {
    render(AktivzitierungSearchResults, {
      props: {
        searchResults: [],
        isLoading: false,
      },
    })

    expect(screen.getByText('Keine Suchergebnisse gefunden')).toBeInTheDocument()
  })

  it('renders a loading spinner', () => {
    render(AktivzitierungSearchResults, {
      props: {
        searchResults: [],
        isLoading: true,
      },
    })

    const spinnerElement = screen.getByRole('progressbar')
    expect(spinnerElement).toBeInTheDocument()
  })
})
