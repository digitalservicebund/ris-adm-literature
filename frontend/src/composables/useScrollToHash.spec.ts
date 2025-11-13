import { describe, it, expect, afterEach, vi } from 'vitest'
import { render } from '@testing-library/vue'
import { createRouter, createMemoryHistory } from 'vue-router'
import { nextTick, defineComponent } from 'vue'
import { useScrollToHash } from './useScrollToHash.ts'

const DEFAULT_OFFSET = 120
const MOCK_SCROLL_Y = 500 // Mock current scroll position

// --- Test Setup ---

/**
 * Creates a simple host component to run the composable.
 */
const TestHost = defineComponent({
  props: {
    offset: Number, // To test custom offset
  },
  setup(props) {
    // Call the composable under test
    useScrollToHash(props.offset)
    return () => null // Render nothing
  },
})

/**
 * Helper function to mount the host component with a router.
 * @param {string} initialPath - The initial URL (e.g., "/page#my-hash")
 * @param {number} [offset] - Optional custom offset
 */
async function mountComposable(initialPath: string, offset?: number) {
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [{ path: '/:pathMatch(.*)*', component: { template: '' } }],
  })

  await router.push(initialPath)
  await router.isReady() // Wait for navigation to complete

  // Render the host component, passing the router
  const { unmount } = render(TestHost, {
    props: { offset },
    global: {
      plugins: [router],
    },
  })

  return { unmount, router }
}

/**
 * Helper to create a target element in the test's DOM.
 * @param {string} id - The ID of the element
 */
function createTargetElement(id: string) {
  const el = document.createElement('div')
  el.id = id
  document.body.appendChild(el)
  return el
}

describe('useScrollToHash', () => {
  afterEach(() => {
    vi.resetAllMocks()
    vi.unstubAllGlobals()
    document.body.innerHTML = '' // Clean up the DOM
  })

  it('scrolls to the hash element on initial mount', async () => {
    const scrollToMock = vi.fn()
    vi.stubGlobal('scrollTo', scrollToMock)
    vi.stubGlobal('scrollY', MOCK_SCROLL_Y)

    createTargetElement('my-hash')
    await mountComposable('/page#my-hash')

    // Wait for onMounted hook and the internal nextTick
    await nextTick()

    // then
    // Calculation: el.top (0) + window.scrollY (500) - defaultOffset (120)
    const expectedTop = 0 + MOCK_SCROLL_Y - DEFAULT_OFFSET // 380
    expect(scrollToMock).toHaveBeenCalledTimes(1)
    expect(scrollToMock).toHaveBeenCalledWith({
      top: expectedTop,
      behavior: 'smooth',
    })
  })

  it('does not scroll if no hash is present', async () => {
    const scrollToMock = vi.fn()
    vi.stubGlobal('scrollTo', scrollToMock)
    vi.stubGlobal('scrollY', MOCK_SCROLL_Y)

    // when
    await mountComposable('/page-without-hash')
    await nextTick()

    // then
    expect(scrollToMock).not.toHaveBeenCalled()
  })

  it('does not scroll if target element is not found', async () => {
    const scrollToMock = vi.fn()
    vi.stubGlobal('scrollTo', scrollToMock)
    vi.stubGlobal('scrollY', MOCK_SCROLL_Y)

    // We do *not* create an element with id="missing-hash"
    await mountComposable('/page#missing-hash')

    // when mounted
    await nextTick()

    // then
    expect(scrollToMock).not.toHaveBeenCalled()
  })

  it('scrolls to the hash element on hash change', async () => {
    const scrollToMock = vi.fn()
    vi.stubGlobal('scrollTo', scrollToMock)
    vi.stubGlobal('scrollY', MOCK_SCROLL_Y)

    createTargetElement('new-hash')
    // Mount *without* a hash first
    const { router } = await mountComposable('/page')
    await nextTick()

    expect(scrollToMock).not.toHaveBeenCalled()

    // When changing the hash
    await router.push('/page#new-hash')
    await nextTick() // Wait for watch

    // then
    const expectedTop = 0 + MOCK_SCROLL_Y - DEFAULT_OFFSET // 380
    expect(scrollToMock).toHaveBeenCalledTimes(1)
    expect(scrollToMock).toHaveBeenCalledWith({
      top: expectedTop,
      behavior: 'smooth',
    })
  })
})
