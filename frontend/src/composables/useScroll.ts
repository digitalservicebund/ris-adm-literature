import { onMounted, watch, nextTick, type Ref } from 'vue'
import { useRoute } from 'vue-router'

/**
 * Handles smooth scrolling to hash anchors
 */
export function useScrollToHash(offset = 120) {
  const route = useRoute()

  async function scrollToHash() {
    const hash = route.hash
    if (!hash) return

    await nextTick() // ensure DOM is ready

    const id = CSS.escape(hash.slice(1))
    const el = document.getElementById(id)
    if (!el) return

    const top = el.getBoundingClientRect().top + window.scrollY - offset
    window.scrollTo({ top, behavior: 'smooth' })
  }

  onMounted(scrollToHash)
  watch(() => route.hash, scrollToHash)
}

/**
 * Performs the smooth page scroll to the element referenced by the Ref,
 * applying an offset for the fixed header.
 * * @param elementRef The Vue Ref pointing to the HTML element to scroll to.
 */
export function useScrollToElement(elementRef: Ref<HTMLElement | null>, offset = 150) {
  // Wait for Vue's DOM update
  nextTick(() => {
    // Wait for the browser to finalize layout calculation
    requestAnimationFrame(() => {
      const element = elementRef.value

      if (element) {
        const viewportTop = element.getBoundingClientRect().top

        // Calculate the absolute position in the document
        const documentTop = viewportTop + window.scrollY

        // Apply the fixed header offset
        const offsetPosition = documentTop - offset

        window.scrollTo({
          top: offsetPosition,
          behavior: 'smooth',
        })
      }
    })
  })
}
