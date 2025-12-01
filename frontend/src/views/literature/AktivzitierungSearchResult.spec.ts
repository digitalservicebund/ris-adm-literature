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
})
