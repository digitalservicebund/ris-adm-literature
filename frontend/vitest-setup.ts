import '@testing-library/jest-dom/vitest'
import { beforeAll, vi } from 'vitest'
import PrimeVue from 'primevue/config'
import { config } from '@vue/test-utils'

// Simple mock to satisfy devtools and any browser storage users
Object.defineProperty(globalThis, 'localStorage', {
  value: {
    getItem: vi.fn(function() { return null }),
    setItem: vi.fn(),
    removeItem: vi.fn(),
    clear: vi.fn(),
  },
  writable: true,
})

vi.mock('@/services/auth', () => {
  return {
    useAuthentication: () => ({
      addAuthorizationHeader: (init: HeadersInit) => ({ ...init }),
      tryRefresh: vi.fn().mockReturnValue(true),
      getUsername: () => 'vorname nachname',
    }),
  }
})

beforeAll(() => {
  config.global.plugins = [PrimeVue]
})
