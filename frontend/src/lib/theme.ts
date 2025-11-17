// This file is for making customizations to RIS UI specific to Valid. Ideally,
// these customizations don't remain here indefinitely, but will me transfered
// to RIS UI eventually.

import { RisUiTheme } from '@digitalservicebund/ris-ui/primevue'
import { usePassThrough } from 'primevue/passthrough'
import type { TabPassThroughOptions } from 'primevue/tab'
import type { TabListPassThroughOptions } from 'primevue/tablist'
import { tw } from '@/lib/tw'

const tab: TabPassThroughOptions = {
  root: ({ context }) => {
    const base = tw`h-64 px-24`
    const active = tw`ris-body2-bold bg-blue-200 shadow-none`
    const inactive = tw`ris-body2-regular text-blue-800 bg-gray-100 hover:border-b-blue-800`

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
    class: tw`flex gap-4 before:content-none`,
  },
}

export default usePassThrough(
  RisUiTheme,
  {
    tab,
    tabList,
  },
  { mergeProps: true, mergeSections: true },
)
