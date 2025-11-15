import { RisUiTheme } from '@digitalservicebund/ris-ui/primevue'
import { usePassThrough } from 'primevue/passthrough'
import type { TabPassThroughOptions } from 'primevue/tab'
import type { TabListPassThroughOptions } from 'primevue/tablist'

const tab: TabPassThroughOptions = {
  root: ({ context }) => {
    const base =
      'h-64 py-4 pl-20 pr-24 outline-blue-800 outline-0 -outline-offset-4 focus-visible:outline-4 inline-flex items-center gap-8'
    const active = 'ris-body2-bold text-black shadow-active-tab bg-blue-200 z-10'
    const inactive =
      'ris-body2-regular text-blue-800 bg-gray-100 hover:border-b-blue-800 cursor-pointer'

    return {
      class: {
        [base]: true,
        [active]: context.active,
        [inactive]: !context.active,
      },
    }
  },
}

const tabList: TabListPassThroughOptions = {
  tabList: {
    class: 'flex gap-4 relative',
  },
}

export default usePassThrough(
  RisUiTheme,
  {
    tab,
    tabList,
  },
  { mergeProps: false, mergeSections: true },
)
