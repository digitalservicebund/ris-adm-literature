import { describe, it, expect } from 'vitest'
import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import DropdownInput from '@/components/input/DropdownInput.vue'

type DropdownInputProps = InstanceType<typeof DropdownInput>['$props']

function renderComponent(props?: Partial<DropdownInputProps>, attrs?: Record<string, unknown>) {
  let modelValue: string | undefined = props?.modelValue ?? ''

  const effectiveProps: DropdownInputProps = {
    modelValue,
    'onUpdate:modelValue':
      props?.['onUpdate:modelValue'] ?? ((value: string | undefined) => (modelValue = value)),
    items: props?.items ?? [
      { label: 'testItem1', value: 't1' },
      { label: 'testItem2', value: 't2' },
      { label: 'testItem3', value: 't3' },
    ],
    ...props,
  }

  return render(DropdownInput, { props: effectiveProps, attrs })
}

describe('DropdownInput', () => {
  it('renders a dropdown', () => {
    renderComponent()
    const input = screen.getByRole('combobox')
    expect(input).toBeInTheDocument()
  })

  it('renders the ID', () => {
    renderComponent(undefined, { id: 'test-id' })
    const input = screen.getByRole('combobox')
    expect(input).toHaveAttribute('id', 'test-id')
  })

  it('renders an aria label', () => {
    renderComponent(undefined, { 'aria-label': 'test-label' })
    const input = screen.getByLabelText('test-label')
    expect(input).toBeInTheDocument()
  })

  it('renders the items', () => {
    renderComponent()
    const items = screen.getAllByRole('option')
    expect(items).toHaveLength(3)
  })

  it('renders the selected item', () => {
    renderComponent({ modelValue: 't2' })
    const input = screen.getByRole('combobox')
    expect(input).toHaveValue('t2')
  })

  it('renders a placeholder', () => {
    renderComponent({ placeholder: 'test placeholder', modelValue: '' })
    const input = screen.getByRole('combobox')
    expect(input).toHaveAttribute('data-placeholder', 'true')
  })

  it('does not render a placeholder if an item is selected', () => {
    renderComponent({ placeholder: 'test placeholder', modelValue: 't2' })
    const input = screen.getByRole('combobox')
    expect(input).not.toHaveAttribute('data-placeholder')
  })

  it('does not render a placeholder if none exists', () => {
    renderComponent({ modelValue: '' })
    const input = screen.getByRole('combobox')
    expect(input).not.toHaveAttribute('data-placeholder')
  })

  it('does not contain a placeholder option if an item is selected', async () => {
    const { rerender } = renderComponent({
      placeholder: 'test placeholder',
      modelValue: '',
    })

    const placeholder = screen.getByRole('option', { name: 'test placeholder' })
    expect(placeholder).toBeInTheDocument()

    await rerender({ modelValue: 't2', placeholder: 'test placeholder' })
    expect(placeholder).not.toBeInTheDocument()
  })

  it('placeholder is disabled', () => {
    renderComponent({ placeholder: 'test placeholder' })
    expect(screen.getByRole('option', { name: 'test placeholder' })).toBeDisabled()
  })

  it('emits a model update', async () => {
    const user = userEvent.setup()
    const { emitted } = renderComponent({ modelValue: 't1' })

    const input = screen.getByRole('combobox')
    expect(input).toHaveValue('t1')

    await user.selectOptions(input, 't2')
    expect(emitted('update:modelValue')).toEqual([['t2']])
  })
})
