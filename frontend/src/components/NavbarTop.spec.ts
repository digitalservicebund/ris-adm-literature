import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/vue'
import NavbarTop from '@/components/NavbarTop.vue'

const mockAuth = {
  logout: vi.fn(),
  getUsername: vi.fn(() => 'vorname nachname'),
}

vi.mock('@/services/auth', () => ({
  useAuthentication: () => mockAuth,
}))

describe('NavbarTop', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders properly', () => {
    render(NavbarTop)

    expect(screen.getByText('Rechtsinformationen')).toBeInTheDocument()
    expect(screen.getByText('vorname nachname')).toBeInTheDocument()
    expect(screen.getByTestId('iconPermIdentity')).toBeInTheDocument()
    expect(screen.getByLabelText('Log out')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Suche' })).toBeInTheDocument()
  })

  it('Log out calls logout when the logout icon is clicked', async () => {
    render(NavbarTop)

    const logoutButton = screen.getByLabelText('Log out')
    await fireEvent.click(logoutButton)

    expect(mockAuth.logout).toHaveBeenCalledTimes(1)
  })

  it('Suche is a link that navigates to the main page', () => {
    render(NavbarTop)

    const searchLink = screen.getByRole('link', { name: 'Suche' })

    expect(searchLink).toBeInTheDocument()
    expect(searchLink).toHaveAttribute('href', '/')
  })
})
