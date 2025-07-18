import { OrderedList } from '@tiptap/extension-list'

export const CustomOrderedList = OrderedList.extend({
  addAttributes() {
    return {
      ...this.parent?.(),
      style: {
        default: null,
        parseHTML: (element) => element.getAttribute('style'),
        renderHTML: (attributes) => {
          return {
            style: attributes.style,
            class: 'list-decimal',
          }
        },
      },
    }
  },
  addInputRules() {
    return []
  },
})
