import type {Ref} from "vue"
import type {ComboboxItem} from "@/components/input/types"
import LegalPeriodical from "@/domain/legalPeriodical.ts";
import type {ComboboxResult} from "@/domain/comboboxResult.ts";
import {computed, ref} from "vue";

enum Endpoint {
  legalPeriodicals = `legalperiodicals`,
}

type ComboboxItemService = {
  [key in keyof typeof Endpoint as `get${Capitalize<key>}`]: (
    filter: Ref<string | undefined>,
  ) => ComboboxResult<ComboboxItem[]>
}

const service: ComboboxItemService = {
  // Once there is a backend, look into Caselaw for implementing loading of items (type UseFetchReturn).
  getLegalPeriodicals: (filter: Ref<string | undefined>) =>
  {
    const legalPeriodical = new LegalPeriodical({title: 'Bundesanzeiger', abbreviation: 'BAnz'})
    const item1: ComboboxItem = { label: 'BAnz | Bundesanzeiger', value: legalPeriodical, sideInformation: 'amtlich' }
    const item2: ComboboxItem = { label: 'DOK | Dokument', value: legalPeriodical, sideInformation: 'amtlich' }

    const result: ComboboxResult<ComboboxItem[]> = {
      data: ref([item1, item2]), execute: () => Promise.resolve(), canAbort: computed(() => false), abort: () => {
      }}
    return result
  }
}

export default service
