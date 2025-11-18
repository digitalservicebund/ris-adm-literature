import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/vue'
import NavbarTop from '@/components/NavbarTop.vue'
import { USER_ROLES } from '@/config/roles'
import { DocumentCategory } from '@/domain/documentType'
import { ROUTE_NAMES } from '@/constants/routes'

const mockAuth: {
  logout: () => void
  getUsername: () => string | undefined
  getGroup: () => string
  getRealmRoles: () => string[]
} = {
  logout: vi.fn(),
  getUsername: vi.fn(() => 'vorname nachname'),
  getGroup: vi.fn(() => 'BAG'),
  getRealmRoles: vi.fn(() => [USER_ROLES.ADM_USER]),
}

vi.mock('@/services/auth', () => ({
  useAuthentication: () => mockAuth,
}))

const mockRoute = {
  meta: { documentCategory: DocumentCategory.LITERATUR_UNSELBSTAENDIG },
  name: 'SomeRoute',
}

vi.mock('vue-router', () => ({
  useRoute: vi.fn(() => mockRoute),
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
    expect(screen.getByText('BAG | Staging')).toBeInTheDocument()
    expect(screen.getByLabelText('Log out')).toBeInTheDocument()
  })

  it('Log out calls logout when the logout icon is clicked', async () => {
    renderComponent()

    const logoutButton = screen.getByLabelText('Log out')
    await fireEvent.click(logoutButton)

    expect(mockAuth.logout).toHaveBeenCalledTimes(1)
  })

  it('Multiple roles: Suche link redirects to ULI startpage in literature context', () => {
    mockAuth.getRealmRoles = vi.fn(() => [USER_ROLES.ADM_USER, USER_ROLES.LITERATURE_USER])
    mockRoute.meta.documentCategory = DocumentCategory.LITERATUR_UNSELBSTAENDIG

    renderComponent()

    const searchLink = screen.getByRole('link', { name: 'Suche' })

    expect(searchLink).toBeInTheDocument()
    expect(searchLink).toHaveAttribute('href', '/literatur-unselbstaendig')
  })

  it('Multiple roles: Suche link redirects to ADM startpage in adm context', () => {
    mockAuth.getRealmRoles = vi.fn(() => [USER_ROLES.ADM_USER, USER_ROLES.LITERATURE_USER])
    mockRoute.meta.documentCategory = DocumentCategory.VERWALTUNGSVORSCHRIFTEN

    renderComponent()

    const searchLink = screen.getByRole('link', { name: 'Suche' })

    expect(searchLink).toBeInTheDocument()
    expect(searchLink).toHaveAttribute('href', '/verwaltungsvorschriften')
  })

  it('Suche link is not shown if user has multiple roles and we are on root page', () => {
    mockAuth.getRealmRoles = vi.fn(() => [USER_ROLES.ADM_USER, USER_ROLES.LITERATURE_USER])
    mockRoute.name = ROUTE_NAMES.ROOT_REDIRECT
    renderComponent()

    const searchLink = screen.queryByRole('link', { name: 'Suche' })
    expect(searchLink).not.toBeInTheDocument()
  })

  it('renders default user infos if not available', () => {
    vi.spyOn(mockAuth, 'getUsername').mockReturnValue(undefined)
    vi.spyOn(mockAuth, 'getGroup').mockReturnValue('')

    renderComponent()

    expect(screen.getByText('Vorname Nachname')).toBeInTheDocument()
    expect(screen.getByText('Staging')).toBeInTheDocument()
  })
})
