import { fireEvent, render, screen } from '@testing-library/vue'
import { describe, expect, it } from 'vitest'
import SearchPanel from './SearchPanel.vue'
import userEvent from '@testing-library/user-event'

function renderComponent(props: { loading?: boolean } = {}) {
  const user = userEvent.setup()

  const defaultProps = {
    loading: false,
  }

  return {
    user,
    ...render(SearchPanel, { props: { ...defaultProps, ...props } }),
  }
}

describe('SearchPanel', () => {
  it('renders', async () => {
    renderComponent()

    expect(screen.getByLabelText('Dokumentnummer')).toBeInTheDocument()
    expect(screen.getByLabelText('Amtl. Langüberschrift')).toBeInTheDocument()
    expect(screen.getByLabelText('Fundstelle')).toBeInTheDocument()
    expect(screen.getByLabelText('Zitierdatum')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Ergebnisse zeigen' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
  })

  it('should clear search params on click reset', async () => {
    const { user } = renderComponent()

    await user.type(screen.getByLabelText('Dokumentnummer'), 'KSNR')
    await user.type(screen.getByLabelText('Amtl. Langüberschrift'), 'text')
    await user.type(screen.getByLabelText('Fundstelle'), 'text')
    const zitierdatumInput = screen.getByLabelText('Zitierdatum')
    await fireEvent.update(zitierdatumInput, '01.01.2000')
    expect(screen.getByLabelText('Zitierdatum')).toHaveValue('01.01.2000')

    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeEnabled()
    await user.click(screen.getByRole('button', { name: 'Zurücksetzen' }))
    expect(screen.getByLabelText('Dokumentnummer')).toHaveValue('')
    expect(screen.getByLabelText('Amtl. Langüberschrift')).toHaveValue('')
    expect(screen.getByLabelText('Fundstelle')).toHaveValue('')
    expect(screen.getByLabelText('Zitierdatum')).toHaveValue('')
    expect(screen.getByRole('button', { name: 'Zurücksetzen' })).toBeDisabled()
  })

  it('should emit search params on click search', async () => {
    const { emitted, user } = renderComponent()

    await user.type(screen.getByLabelText('Dokumentnummer'), 'KSNR')
    await user.click(screen.getByRole('button', { name: 'Ergebnisse zeigen' }))
    expect(emitted()['search'].length).toBe(1)
  })

  it('disables the search button when the loading prop is true', () => {
    renderComponent({ loading: true })

    const searchButton = screen.getByRole('button', { name: 'Ergebnisse zeigen' })
    expect(searchButton).toBeDisabled()
  })

  it('disables the search button when the date input is incorrect', async () => {
    renderComponent({ loading: true })
    const zitierdatumInput = screen.getByLabelText('Zitierdatum')
    await fireEvent.update(zitierdatumInput, '50.50.5000')
    expect(screen.getByLabelText('Zitierdatum')).toHaveValue('50.50.5000')

    const searchButton = screen.getByRole('button', { name: 'Ergebnisse zeigen' })
    expect(searchButton).toBeDisabled()
  })

  it('disables the search button when the input is in the future', async () => {
    renderComponent({ loading: true })
    const zitierdatumInput = screen.getByLabelText('Zitierdatum')
    await fireEvent.update(zitierdatumInput, '01.01.2300')
    expect(screen.getByLabelText('Zitierdatum')).toHaveValue('01.01.2300')

    const searchButton = screen.getByRole('button', { name: 'Ergebnisse zeigen' })
    expect(searchButton).toBeDisabled()
  })
})
