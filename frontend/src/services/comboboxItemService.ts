import type { Ref } from 'vue'
import type { ComboboxItem } from '@/components/input/types'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'
import { computed, ref } from 'vue'

type ComboboxItemService = {
  getLegalPeriodicals: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
}

const service: ComboboxItemService = {
  // Once there is a backend, look into Caselaw for implementing loading of items (type UseFetchReturn).
  getLegalPeriodicals: (filter: Ref<string | undefined>) => {
    const banzLegalPeriodical = new LegalPeriodical({
      title: 'Bundesanzeiger',
      abbreviation: 'BAnz',
    })
    const banzItem: ComboboxItem = {
      label: 'BAnz | Bundesanzeiger',
      value: banzLegalPeriodical,
      sideInformation: 'amtlich',
    }
    const aaLegalPeriodical = new LegalPeriodical({
      title: 'Arbeitsrecht aktiv',
      abbreviation: 'AA',
    })
    const aaItem: ComboboxItem = {
      label: 'AA | Arbeitsrecht aktiv',
      value: aaLegalPeriodical,
      sideInformation: 'amtlich',
    }
    let items = ref([banzItem, aaItem])
    if (filter.value && (filter.value as string).startsWith('a')) {
      items = ref([aaItem])
    } else if (filter.value && (filter.value as string).startsWith('b')) {
      items = ref([banzItem])
    }
    const execute = async () => {
      return service.getLegalPeriodicals(filter)
    }
    const result: ComboboxResult<ComboboxItem[]> = {
      data: items,
      execute: execute,
      canAbort: computed(() => false),
      abort: () => {},
    }
    return result
  },
}

export default service
