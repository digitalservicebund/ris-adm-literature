import { describe, it, expect, vi } from 'vitest'
import { ref } from 'vue'
import { useAdmVwvMenuItems } from '@/composables/useAdmVwvMenuItems'

const isInternalUser = ref(false)
vi.mock('@/composables/useInternalUser', () => {
  return {
    useInternalUser: () => isInternalUser,
  }
})

describe('useAdmVwvMenuItems', () => {
  it('adds document identifier as route parameter to each menu item', () => {
    isInternalUser.value = true
    const documentNumber = 'fake-number'

    const menuItems = useAdmVwvMenuItems(documentNumber, {})

    for (const menuItem of menuItems) {
      expect(menuItem.route.params).toMatchObject({
        documentNumber: 'fake-number',
      })
    }
  })

  it('clones current route query to menu item', () => {
    isInternalUser.value = true
    const route = {
      query: { foo: 'bar' },
    }

    const menuItems = useAdmVwvMenuItems('', route.query)

    for (const menuItem of menuItems) {
      expect(menuItem.route.query).toEqual({ foo: 'bar' })
    }
  })

  it('lists all expected menu items', () => {
    isInternalUser.value = true
    const menuItems = useAdmVwvMenuItems('', {})

    const topLabelNames = menuItems.map((item) => item.label)
    expect(topLabelNames).toContain('Rubriken')
    expect(topLabelNames).toContain('Fundstellen')
    expect(topLabelNames).toContain('Abgabe')
  })

  // it("lists all expected menu items for external user", () => {
  //   isInternalUser.value = false
  //   const menuItems = useAdmVwvMenuItems("", {})

  //   const topLabelNames = menuItems.map((item) => item.label)
  //   expect(topLabelNames).toContain("Rubriken")
  //   expect(topLabelNames).not.toContain("Fundstellen")
  //   expect(topLabelNames).toContain("Abgabe")

  //   const categoriesSubMenu = menuItems.find(
  //     (menu) => menu.label === "Rubriken",
  //   )?.children
  //   expect(categoriesSubMenu?.map((menu) => menu.label)).toEqual([
  //     "Rechtszug",
  //     "Inhaltliche Erschließung",
  //     "Kurz- & Langtexte",
  //   ])
  // })
})
