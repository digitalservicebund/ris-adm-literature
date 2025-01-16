import type { Ref } from 'vue'
import type { ComboboxItem } from '@/components/input/types'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import type { Court } from '@/domain/documentUnit'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'
import { computed, ref } from 'vue'

type ComboboxItemService = {
  getLegalPeriodicals: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCourts: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
}

const service: ComboboxItemService = {
  // Once there is a backend, look into Caselaw for implementing loading of items (type UseFetchReturn).
  getLegalPeriodicals: (filter: Ref<string | undefined>) => {
    const banzLegalPeriodical = new LegalPeriodical({
      title: 'Bundesanzeiger',
      abbreviation: 'BAnz',
      citationStyle: '2009, Seite 21'
    })
    const banzItem: ComboboxItem = {
      label: 'BAnz | Bundesanzeiger',
      value: banzLegalPeriodical,
      sideInformation: 'amtlich',
    }
    const aaLegalPeriodical = new LegalPeriodical({
      title: 'Arbeitsrecht aktiv',
      abbreviation: 'AA',
      citationStyle: '2011'
    })
    const aaItem: ComboboxItem = {
      label: 'AA | Arbeitsrecht aktiv',
      value: aaLegalPeriodical,
      sideInformation: 'amtlich',
    }
    let items = ref([banzItem, aaItem])
    if (filter?.value?.startsWith('a')) {
      items = ref([aaItem])
    } else if (filter?.value?.startsWith('b')) {
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
  getCourts: (filter: Ref<string | undefined>) => {
    const agAachen = {
      label: 'AG Aachen',
    } as Court
    const agAachenItem: ComboboxItem = {
      label: agAachen.label,
      value: agAachen,
    }
    const bgBremen = {
      label: 'Berufsgericht für Architekten Bremen',
    } as Court
    const bgBremenItem: ComboboxItem = {
      label: bgBremen.label,
      value: bgBremen,
    }
    let items = ref([agAachenItem, bgBremenItem])
    if (filter?.value?.startsWith('a')) {
      items = ref([agAachenItem])
    } else if (filter?.value?.startsWith('b')) {
      items = ref([bgBremenItem])
    }
    const execute = async () => {
      return service.getCourts(filter)
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
