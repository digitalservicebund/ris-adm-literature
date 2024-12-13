import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import NavbarTop from '../NavbarTop.vue'

describe('HelloWorld', () => {
  it('renders properly', () => {
    const wrapper = mount(NavbarTop, { props: {} })
    expect(wrapper.text()).toContain('Rechtsinformationen')
  })
})
