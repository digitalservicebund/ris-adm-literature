import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import NavbarTop from '@/components/NavbarTop.vue'
import { render, screen } from '@testing-library/vue'

describe('NavbarTop', () => {
  it('renders properly', () => {
    const wrapper = mount(NavbarTop, { props: {} })
    expect(wrapper.text()).toContain('Rechtsinformationen')
  })

  it('renders the main navigation link to the homepage with correct text and href', async () => {
    render(NavbarTop)
    const homepageLink = screen.getByRole('link', { name: /Rechtsinformationen des Bundes/i })

    expect(homepageLink).toBeVisible()
    expect(homepageLink).toHaveAttribute('href', '/')
  })
})
