import type { Ref } from 'vue'
import type { ComboboxItem } from '@/components/input/types'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import type { Court, DocumentType } from '@/domain/documentUnit'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'
import type { CitationType } from '@/domain/citationType'
import { computed, ref } from 'vue'
import type { NormAbbreviation } from '@/domain/normAbbreviation.ts'

export type ComboboxItemService = {
  getLegalPeriodicals: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCourts: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getDocumentTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getRisAbbreviations: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCitationTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
}

const service: ComboboxItemService = {
  // Once there is a backend, look into Caselaw for implementing loading of items (type UseFetchReturn).
  getLegalPeriodicals: (filter: Ref<string | undefined>) => {
    const banzLegalPeriodical = new LegalPeriodical({
      title: 'Bundesanzeiger',
      abbreviation: 'BAnz',
      citationStyle: '2009, Seite 21',
    })
    const banzItem: ComboboxItem = {
      label: 'BAnz | Bundesanzeiger',
      value: banzLegalPeriodical,
      sideInformation: 'amtlich',
    }
    const aaLegalPeriodical = new LegalPeriodical({
      title: 'Arbeitsrecht aktiv',
      abbreviation: 'AA',
      citationStyle: '2011',
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
  getDocumentTypes: (filter: Ref<string | undefined>) => {
    const documentTypeValues = ['VR', 'VE', 'VV', 'ST']
    const documentTypes = documentTypeValues.map((v) => <DocumentType>{ label: v })
    const items = ref(documentTypes.map((dt) => <ComboboxItem>{ label: dt.label, value: dt }))
    const execute = async () => {
      return service.getDocumentTypes(filter)
    }
    const result: ComboboxResult<ComboboxItem[]> = {
      data: items,
      execute: execute,
      canAbort: computed(() => false),
      abort: () => {},
    }
    return result
  },
  getRisAbbreviations: (filter: Ref<string | undefined>) => {
    const risAbbreviationValues = [
      { abbreviation: 'SGB 5', officialLongTitle: 'Sozialgesetzbuch (SGB) Fünftes Buch (V)' },
      {
        abbreviation: 'KVLG',
        officialLongTitle:
          'Gesetz zur Weiterentwicklung des Rechts der gesetzlichen Krankenversicherung',
      },
    ]

    const risAbbreviations = risAbbreviationValues.map(
      (v) =>
        <NormAbbreviation>{ abbreviation: v.abbreviation, officialLongTitle: v.officialLongTitle },
    )
    const items = ref(
      risAbbreviations.map(
        (na) =>
          <ComboboxItem>{
            label: na.abbreviation,
            value: na,
            additionalInformation: na.officialLongTitle,
          },
      ),
    )
    const execute = async () => {
      return service.getRisAbbreviations(filter)
    }
    const result: ComboboxResult<ComboboxItem[]> = {
      data: items,
      execute: execute,
      canAbort: computed(() => false),
      abort: () => {},
    }
    return result
  },
  getCitationTypes: (filter: Ref<string | undefined>) => {
    const citationTypeValues = [
      {
        uuid: 'e52c14ac-1a5b-4ed2-9228-516489dd9f2a',
        jurisShortcut: 'Abgrenzung',
        label: 'Abgrenzung',
      },
      {
        uuid: '844b01a1-0c57-4889-98a2-281f613a77bb',
        jurisShortcut: 'Ablehnung',
        label: 'Ablehnung',
      },
      {
        uuid: 'd43855ac-7784-4e91-bd14-a19006f44683',
        jurisShortcut: 'Abweichung',
        label: 'Abweichung',
      },
      {
        uuid: 'c4ce64b5-b469-4430-8dfc-e3422ac4f873',
        jurisShortcut: 'Anschluss',
        label: 'Anschluss',
      },
      {
        uuid: '7610e678-3f71-4e6f-a502-fa8051ea1418',
        jurisShortcut: 'Anschluß',
        label: 'Anschluß',
      },
      {
        uuid: '08849ed9-6d2b-4016-a570-5920ef34a6ef',
        jurisShortcut: 'Aufgabe',
        label: 'Aufgabe',
      },
      {
        uuid: 'd4f67204-62ff-474d-9a32-7e11f1f9a640',
        jurisShortcut: 'Aufrechterhaltung',
        label: 'Aufrechterhaltung',
      },
      {
        uuid: '8477c9b8-1bd3-43e6-bc06-408b25d8255f',
        jurisShortcut: 'Bestätigung',
        label: 'Bestätigung',
      },
      {
        uuid: 'a5b0ada2-9b29-4b1b-90a4-16f07d0ec8f0',
        jurisShortcut: 'Entgegen',
        label: 'Entgegen',
      },
      {
        uuid: 'eb2dbf4d-4215-4c71-8691-8098095a20c1',
        jurisShortcut: 'Ergänzung',
        label: 'Ergänzung',
      },
      {
        uuid: 'ab989687-dd7b-4084-8485-bc2baad43cf3',
        jurisShortcut: 'Festhaltung',
        label: 'Festhaltung',
      },
      {
        uuid: '714ceaef-588b-4f1a-a78d-b891ce2bf794',
        jurisShortcut: 'Fortbildung',
        label: 'Fortbildung',
      },
      {
        uuid: '7f048d4d-bd73-43b8-84b0-18a8ff932c40',
        jurisShortcut: 'Fortentwicklung',
        label: 'Fortentwicklung',
      },
      {
        uuid: '0b0c9e25-cb5c-45e4-8c4a-ca1823fc2a1b',
        jurisShortcut: 'Fortführung',
        label: 'Fortführung',
      },
      {
        uuid: '591b6602-cb4a-47ca-903e-fd0d948f4514',
        jurisShortcut: 'Parallelentscheidung',
        label: 'Parallelentscheidung',
      },
      {
        uuid: '20283509-2bf2-48fe-8820-a44cb44f22fe',
        jurisShortcut: 'So auch',
        label: 'So auch',
      },
      {
        uuid: '55521227-237a-44c9-83a2-fe6ec0a7f882',
        jurisShortcut: 'Teilweise Anschluss',
        label: 'Teilweise Anschluss',
      },
      {
        uuid: '637a6ac1-796d-4cae-b98e-821ce6f93b2e',
        jurisShortcut: 'Teilweise Aufgabe',
        label: 'Teilweise Aufgabe',
      },
      {
        uuid: '3f0d98ca-4f13-4a95-bfa9-e31f12033914',
        jurisShortcut: 'Teilweise Entgegen',
        label: 'Teilweise Entgegen',
      },
      {
        uuid: '46df537f-47ef-40f5-a28f-fc70c69c183d',
        jurisShortcut: 'Teilweise Parallelentscheidung',
        label: 'Teilweise Parallelentscheidung',
      },
      {
        uuid: 'b6e7801d-a306-4fbd-8c2e-d8b8ed54298f',
        jurisShortcut: 'Verbunden',
        label: 'Verbunden',
      },
      {
        uuid: '40e00f89-2773-48d8-a7f0-e159b2d449bf',
        jurisShortcut: 'Vergleiche',
        label: 'Vergleiche',
      },
      {
        uuid: 'f90ac03c-5091-4d59-af0f-edea989aa4be',
        jurisShortcut: 'Weiterentwicklung',
        label: 'Weiterentwicklung',
      },
      {
        uuid: '5b685185-db44-4d91-a9d2-57c7aa49f622',
        jurisShortcut: 'Weiterführung',
        label: 'Weiterführung',
      },
      {
        uuid: 'f011564e-181b-433e-85d6-ae58361c407f',
        jurisShortcut: 'XX',
        label: 'XX',
      },
      {
        uuid: '6bca6098-8f9b-4cbe-b251-5fc6e7d4e9f1',
        jurisShortcut: 'Zustimmung',
        label: 'Zustimmung',
      },
      {
        uuid: '78008504-c3e7-43f8-bcb3-2618d8a24439',
        jurisShortcut: 'T',
        label: 'im Text',
      },
      {
        uuid: 'c030c7d0-69da-4303-b3cb-c59056239435',
        jurisShortcut: 'Änderung',
        label: 'Änderung',
      },
      {
        uuid: 'cb8a0a8d-93d1-41ca-8279-f1c35083da8d',
        jurisShortcut: 'Übernahme',
        label: 'Übernahme',
      },
    ] as CitationType[]
    let filteredItems = citationTypeValues
    if (filter && filter.value && filter.value.length > 0) {
      filteredItems = citationTypeValues.filter((item) =>
        item.label.toLowerCase().startsWith((filter.value as string).toLowerCase()),
      )
    }
    const citationTypes = ref(
      filteredItems.map((item) => <ComboboxItem>{ label: item.label, value: item }),
    )
    const execute = async () => {
      return service.getCitationTypes(filter)
    }
    const result: ComboboxResult<ComboboxItem[]> = {
      data: citationTypes,
      execute: execute,
      canAbort: computed(() => false),
      abort: () => {},
    }
    return result
  },
}

export default service
