import { render, screen } from '@testing-library/vue'
import { describe, it, expect } from 'vitest'
import type { AktivzitierungAdm } from '@/domain/AktivzitierungAdm.ts'
import AktivzitierungAdmItem from './AktivzitierungAdmItem.vue'

describe('AktivzitierungAdmItem', () => {
  it('renders metaSummary correctly when documentNumber is set', () => {
    const aktivzitierung: AktivzitierungAdm = {
      id: '1',
      uuid: undefined,
      documentNumber: 'DOC123',
    }

    render(AktivzitierungAdmItem, {
      props: { aktivzitierung },
    })

    const div = screen.getByText('DOC123')
    expect(div).toBeInTheDocument()
  })
})
