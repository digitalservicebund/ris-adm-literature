import { onMounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'

/**
 * Handles smooth scrolling to hash anchors
 */
export function useScrollToHash(offset = 80) {
  const route = useRoute()

  async function scrollToHash() {
    const hash = window.location.hash || route.hash
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
