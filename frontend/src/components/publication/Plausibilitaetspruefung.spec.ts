import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import SanityCheck from '@/components/publication/PlausibilitaetsPruefung.vue'

describe('SanityCheck', () => {
  it('renders positive message when there is no missing fields', () => {
    render(SanityCheck, {
      props: {
        missingFields: [],
      },
    })

    expect(screen.getByText('Plausibilitätsprüfung')).toBeInTheDocument()
    expect(screen.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeInTheDocument()
  })

  it('renders 5 missing fields and a link to rubriken', () => {
    render(SanityCheck, {
      props: {
        missingFields: [
          'langueberschrift',
          'inkrafttretedatum',
          'dokumenttyp',
          'normgeberList',
          'zitierdaten',
        ],
      },
    })

    expect(screen.getByText('Plausibilitätsprüfung')).toBeInTheDocument()
    expect(screen.getByText('Folgende Pflichtfelder sind nicht befüllt:')).toBeInTheDocument()
    expect(screen.getByText('Amtl. Langüberschrift')).toBeInTheDocument()
    expect(screen.getByText('Datum des Inkrafttretens')).toBeInTheDocument()
    expect(screen.getByText('Dokumenttyp')).toBeInTheDocument()
    expect(screen.getByText('Normgeber')).toBeInTheDocument()
    expect(screen.getByText('Zitierdatum')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Rubriken bearbeiten' })).toBeInTheDocument()
  })

  it('renders the field key if not mapped', () => {
    render(SanityCheck, {
      props: {
        missingFields: ['unmappedField'],
      },
    })

    expect(screen.getByText('unmappedField')).toBeInTheDocument()
  })
})
