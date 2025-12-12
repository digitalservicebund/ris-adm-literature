import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'
import AktivzitierungAdmItem from './AktivzitierungAdmItem.vue'

describe('AktivzitierungAdmItem', () => {
  it('renders only documentNumber when citationType is missing', () => {
    const aktivzitierung: AktivzitierungAdm = {
      id: '1',
      documentNumber: 'DOC-123',
      citationType: undefined,
    }

    render(AktivzitierungAdmItem, {
      props: { aktivzitierung },
    })

    expect(screen.getByText('DOC-123')).toBeInTheDocument()
    expect(screen.queryByText('Citation A')).not.toBeInTheDocument()
  })

  it('renders only citationType when documentNumber is missing', () => {
    const aktivzitierung: AktivzitierungAdm = {
      id: '1',
      documentNumber: undefined,
      citationType: 'Citation A',
    }

    render(AktivzitierungAdmItem, {
      props: { aktivzitierung },
    })

    expect(screen.getByText('Citation A')).toBeInTheDocument()
    expect(screen.queryByText('DOC-123')).not.toBeInTheDocument()
  })

  it('renders both documentNumber and citationType', () => {
    const aktivzitierung: AktivzitierungAdm = {
      id: '1',
      documentNumber: 'DOC-123',
      citationType: 'Citation A',
    }

    render(AktivzitierungAdmItem, {
      props: { aktivzitierung },
    })

    expect(screen.getByText('DOC-123, Citation A')).toBeInTheDocument()
  })
})
