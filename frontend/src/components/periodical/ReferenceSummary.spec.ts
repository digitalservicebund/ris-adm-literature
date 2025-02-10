import ReferenceSummary from '@/components/periodical/ReferenceSummary.vue'
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import Reference from '@/domain/reference.ts'
import LegalPeriodical from '@/domain/legalPeriodical.ts'

describe('ReferenceSummary', () => {
  it('show ambiguous legal periodical abbreviation', async () => {
    render(ReferenceSummary, {
      props: {
        data: new Reference({
          legalPeriodicalRawValue: 'Bundesanzeiger',
        }),
      },
    })

    expect(screen.getByText('Mehrdeutiger Verweis')).toBeInTheDocument()
  })

  it('does not show primary badge', async () => {
    render(ReferenceSummary, {
      props: {
        data: new Reference({
          legalPeriodical: new LegalPeriodical({
            title: 'Bundesanzeiger',
            abbreviation: 'BAnz',
            primaryReference: true,
          }),
          primaryReference: true,
        }),
      },
    })

    expect(screen.getByText('primär')).not.toBeInTheDocument()
  })
})
