import { render } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import DocumentUnits from './DocumentUnits.vue'

function renderComponent(props = {}) {
  return {
    ...render(DocumentUnits, {
      props,
      global: {
        stubs: {
          DocumentUnitList: true, // stub child component
        },
      },
    }),
  }
}

describe('DocumentUnits', () => {
  it('renders', () => {
    const { container } = renderComponent()
    expect(container).toBeTruthy()
  })
})
