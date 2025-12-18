import { render, screen } from '@testing-library/vue'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import AktivzitieurungAdmSearchResult from './AktivzitieurungAdmSearchResult.vue'

describe('Aktivzitierung search result', () => {
  const defaultSearchResult = {
    id: 'id-1',
    documentNumber: 'DOC-123',
    langueberschrift: 'Standard Titel',
  }

  it('renders correctly with all fields (Normgeber, Date, Aktenzeichen, Fundstelle, Typ)', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          ...defaultSearchResult,
          normgeber: 'BVerfG',
          inkrafttretedatum: '2025-12-18',
          aktenzeichen: '1 BvR 123/25',
          periodikum: 'BGBl I',
          zitatstelle: 'S. 456',
          dokumenttyp: 'Urteil',
        },
        isAdded: false,
      },
    })

    // parseIsoDateToLocal(2025-12-18) -> 18.12.2025
    expect(
      screen.getByText('BVerfG, 18.12.2025, 1 BvR 123/25, BGBl I S. 456 (Urteil) | DOC-123'),
    ).toBeInTheDocument()
    expect(screen.getByText('Standard Titel')).toBeInTheDocument()
  })

  it('renders correctly when date and file number (aktenzeichen) are missing', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          ...defaultSearchResult,
          normgeber: 'BMJ',
          dokumenttyp: 'Verordnung',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('BMJ, (Verordnung) | DOC-123')).toBeInTheDocument()
  })

  it('joins periodikum and zitatstelle into fundstelle with a space', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          ...defaultSearchResult,
          periodikum: 'NJW',
          zitatstelle: '2025, 10',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('NJW 2025, 10 | DOC-123')).toBeInTheDocument()
  })

  it('renders only documentNumber with fallback dash when metadata is missing', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          id: 'id-minimal',
          documentNumber: 'DOC-MINIMAL',
          langueberschrift: 'Minimaler Titel',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('— | DOC-MINIMAL')).toBeInTheDocument()
    expect(screen.getByText('Minimaler Titel')).toBeInTheDocument()
  })

  it('renders "unbekannt" when langueberschrift is missing', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          id: 'id-no-title',
          documentNumber: 'DOC-999',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('unbekannt')).toBeInTheDocument()
  })

  it('emits add with the searchResult when clicking the button', async () => {
    const user = userEvent.setup()
    const searchResult = {
      ...defaultSearchResult,
      normgeber: 'TEST-GEBER',
    }

    const { emitted } = render(AktivzitieurungAdmSearchResult, {
      props: { searchResult, isAdded: false },
    })

    await user.click(screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' }))

    expect(emitted().add).toBeTruthy()
    // The payload should be the searchResult object
    expect(emitted().add?.[0]).toEqual([searchResult])
  })

  it('disables button and shows "Bereits hinzugefügt" when isAdded is true', async () => {
    const user = userEvent.setup()
    const { emitted } = render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: defaultSearchResult,
        isAdded: true,
      },
    })

    expect(screen.getByText('Bereits hinzugefügt')).toBeInTheDocument()

    const button = screen.getByRole('button', { name: 'Aktivzitierung hinzufügen' })
    expect(button).toBeDisabled()

    await user.click(button)
    expect(emitted().add).toBeFalsy()
  })

  it('renders fundstelle without dokumenttyp correctly', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          ...defaultSearchResult,
          periodikum: 'OnlyFundstelle',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('OnlyFundstelle | DOC-123')).toBeInTheDocument()
  })

  it('renders dokumenttyp without fundstelle correctly in parentheses', () => {
    render(AktivzitieurungAdmSearchResult, {
      props: {
        searchResult: {
          ...defaultSearchResult,
          normgeber: 'Geber',
          dokumenttyp: 'TypOnly',
        },
        isAdded: false,
      },
    })

    expect(screen.getByText('Geber, (TypOnly) | DOC-123')).toBeInTheDocument()
  })
})
