import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/vue'
import DocumentUnitReferenceInput from '@/components/periodical/DocumentUnitReferenceInput.vue'

function renderComponent() {
  return {
    ...render(DocumentUnitReferenceInput),
  }
}

describe('DocumentUnitReferenceInput', () => {
  it('component is rendered', async () => {
    renderComponent()

    expect(screen.getByLabelText('Periodikum')).toBeInTheDocument()
    expect(screen.getByLabelText('Zitatstelle')).toBeInTheDocument()
  })
})
