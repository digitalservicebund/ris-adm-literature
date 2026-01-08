import { render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import AktivzitierungenSli from './AktivzitierungenSli.vue'

const globalStubs = {
  Aktivzitierung: {
    template: `<div data-testid="aktivzitierung">
      <slot name="item" :aktivzitierung="{ id: '1' }" />
      <slot name="input" :modelValue="{ id: '1' }" :onUpdateModelValue="() => {}" />
      <slot name="searchResult" :searchResult="{ documentNumber: '123' }" :isAdded="false" :onAdd="() => {}" />
    </div>`,
    props: ['modelValue', 'fetchResultsFn', 'transformResultFn'],
    emits: ['update:modelValue'],
  },
  AktivzitierungSliInput: { template: `<div data-testid="input"/>` },
  AktivzitierungSliItem: { template: `<div data-testid="item"/>` },
  AktivzitierungSliSearchResult: { template: `<div data-testid="search-result"/>` },
}

describe('AktivzitierungenSli', () => {
  it('renders correctly', () => {
    render(AktivzitierungenSli, {
      global: { stubs: globalStubs },
      props: { modelValue: [] },
    })

    expect(
      screen.getByRole('heading', { name: 'Aktivzitierung (selbst. Literatur)' }),
    ).toBeInTheDocument()

    expect(screen.getByTestId('aktivzitierung')).toBeInTheDocument()

    expect(screen.getByTestId('item')).toBeInTheDocument()
    expect(screen.getByTestId('input')).toBeInTheDocument()
    expect(screen.getByTestId('search-result')).toBeInTheDocument()
  })
})
