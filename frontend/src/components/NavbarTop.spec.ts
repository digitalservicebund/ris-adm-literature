import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/vue'
import NavbarTop from '@/components/NavbarTop.vue'

const mockAuth = {
  logout: vi.fn(),
  getUsername: vi.fn(() => 'vorname nachname'),
  getGroup: vi.fn(() => 'BAG'),
}

vi.mock('@/services/auth', () => ({
  useAuthentication: () => mockAuth,
}))

const renderComponent = () => {
  return render(NavbarTop, {
    global: {
      stubs: {
        'router-link': {
          template: '<a :href="to" v-bind="$attrs"><slot /></a>',
          props: ['to'],
        },
      },
    },
  })
}

describe('NavbarTop', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders properly', () => {
    renderComponent()

    expect(screen.getByText('Rechtsinformationen')).toBeInTheDocument()
    expect(screen.getByText('vorname nachname')).toBeInTheDocument()
    expect(screen.getByTestId('iconPermIdentity')).toBeInTheDocument()
    expect(screen.getByText('BAG | staging')).toBeInTheDocument()
    expect(screen.getByLabelText('Log out')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Suche' })).toBeInTheDocument()
  })

  it('Log out calls logout when the logout icon is clicked', async () => {
    renderComponent()

    const logoutButton = screen.getByLabelText('Log out')
    await fireEvent.click(logoutButton)

    expect(mockAuth.logout).toHaveBeenCalledTimes(1)
  })

  it('Suche is a link that navigates to the main page', () => {
    renderComponent()

    const searchLink = screen.getByRole('link', { name: 'Suche' })

    expect(searchLink).toBeInTheDocument()
    expect(searchLink).toHaveAttribute('href', '/')
  })
})
