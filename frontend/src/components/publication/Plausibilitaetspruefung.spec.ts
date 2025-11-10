import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/vue'
import Plausibilitaetspruefung from './Plausibilitaetspruefung.vue'
import { DocumentCategory } from '@/domain/documentType'

// Mock vue-router useRoute
let mockRoute: {
  meta: { documentCategory?: string }
  path: string
}
vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
}))

describe('Plausibilitaetspruefung', () => {
  it('renders positive message when there is no missing fields', () => {
    mockRoute = {
      meta: { documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN },
      path: '/abgabe',
    }

    render(Plausibilitaetspruefung, {
      props: {
        missingFields: [],
      },
    })

    expect(screen.getByText('Plausibilitätsprüfung')).toBeInTheDocument()
    expect(screen.getByText('Alle Pflichtfelder sind korrekt ausgefüllt.')).toBeInTheDocument()
  })

  it('renders 5 missing fields and a link to rubriken', () => {
    mockRoute = {
      meta: { documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN },
      path: '/abgabe',
    }

    render(Plausibilitaetspruefung, {
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
    mockRoute = {
      meta: { documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN },
      path: '/abgabe',
    }

    render(Plausibilitaetspruefung, {
      props: {
        missingFields: ['unmappedField'],
      },
    })

    expect(screen.getByText('unmappedField')).toBeInTheDocument()
  })
})
