import { BulletList } from '@tiptap/extension-list'

export const CustomBulletList = BulletList.extend({
  addAttributes() {
    return {
      ...this.parent?.(),
      style: {
        default: null,
        parseHTML: (element) => element.getAttribute('style'),
        renderHTML: (attributes) => {
          return {
            style: attributes.style,
            class: 'list-disc',
          }
        },
      },
    }
  },
  addInputRules() {
    return []
  },
})
