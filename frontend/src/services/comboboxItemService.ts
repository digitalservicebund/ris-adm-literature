import { useFetch, type UseFetchReturn } from '@vueuse/core'
import type { Ref } from 'vue'
import { API_PREFIX } from './httpClient'
import type { ComboboxInputModelType, ComboboxItem } from '@/components/input/types'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import type { Court } from '@/domain/documentUnit'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'
import type { CitationType } from '@/domain/citationType'
import { computed, ref } from 'vue'
import type { NormAbbreviation } from '@/domain/normAbbreviation.ts'
import ActiveReference, { ActiveReferenceType } from '@/domain/activeReference.ts'
import type { FieldOfLaw } from '@/domain/fieldOfLaw'
import errorMessages from '@/i18n/errors.json'

enum Endpoint {
  documentTypes = 'lookup-tables/document-types',
}

function formatDropdownItems(
  responseData: ComboboxInputModelType[],
  endpoint: Endpoint,
): ComboboxItem[] {
  if (endpoint == Endpoint.documentTypes) {
    return (responseData as { abbreviation: string; name: string }[]).map((item) => ({
      label: item.name,
      value: {
        label: item.name,
        abbreviation: item.abbreviation,
      },
      additionalInformation: item.abbreviation,
    }))
  }
  return []
}

function fetchFromEndpoint(
  endpoint: Endpoint,
  filter: Ref<string | undefined>,
  options?: { size?: number; paged?: boolean },
) {
  const requestParams = computed<{ searchQuery?: string; size?: string; paged?: string }>(() => ({
    ...(filter.value ? { searchQuery: filter.value } : {}),
    ...(options?.size != undefined ? { size: options.size.toString() } : {}),
    ...(options?.paged != undefined ? { paged: options?.paged?.toString() } : {}),
  }))
  const url = computed(() => {
    const queryParams = new URLSearchParams(requestParams.value).toString()
    return `${API_PREFIX}${endpoint}?${queryParams}`
  })

  return useFetch<ComboboxItem[]>(url, {
    afterFetch: (ctx) => {
      ctx.data = formatDropdownItems(ctx.data.documentTypes, endpoint)
      return ctx
    },
    onFetchError: ({ response }) => ({
      status: response?.status,
      error: {
        title: errorMessages.SERVER_ERROR_DROPDOWN.title,
        description: errorMessages.SERVER_ERROR_DROPDOWN.description,
      },
    }),
  }).json()
}

export type ComboboxItemService = {
  getLegalPeriodicals: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCourts: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getDocumentTypes: (filter: Ref<string | undefined>) => UseFetchReturn<ComboboxItem[]>
  getRisAbbreviations: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getActiveReferenceTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCitationTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getFieldOfLawSearchByIdentifier: (
    filter: Ref<string | undefined>,
  ) => ComboboxResult<ComboboxItem[]>
}

const service: ComboboxItemService = {
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
  getDocumentTypes: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.documentTypes, filter, { paged: false }),
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
  getActiveReferenceTypes: (filter: Ref<string | undefined>) => {
    const items = ref(
      Object.values(ActiveReferenceType).map(
        (referenceType) =>
          <ComboboxItem>{
            label: ActiveReference.referenceTypeLabels.get(referenceType),
            value: referenceType,
          },
      ),
    )
    const execute = async () => {
      return service.getActiveReferenceTypes(filter)
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
    const citationTypes = ref(
      citationTypeValues.map((item) => <ComboboxItem>{ label: item.label, value: item }),
    )
    const execute = async () => {
      if (filter?.value && filter.value.length > 0) {
        const filteredItems = citationTypeValues.filter((item) =>
          item.label.toLowerCase().startsWith((filter.value as string).toLowerCase()),
        )
        const filteredComboBoxItems = filteredItems.map(
          (item) => <ComboboxItem>{ label: item.label, value: item },
        )
        citationTypes.value = [...filteredComboBoxItems]
      } else {
        citationTypes.value = citationTypeValues.map(
          (item) => <ComboboxItem>{ label: item.label, value: item },
        )
      }
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
  // Once there is a backend, look into Caselaw for implementing loading of items (type UseFetchReturn).
  getFieldOfLawSearchByIdentifier: (filter: Ref<string | undefined>) => {
    const fieldOfLawValues: FieldOfLaw[] = [
      {
        hasChildren: true,
        identifier: 'AR',
        text: 'Arbeitsrecht',
        linkedFields: [],
        norms: [],
        children: [],
        parent: undefined,
      },
      {
        hasChildren: true,
        identifier: 'AR-01',
        text: 'Arbeitsvertrag: Abschluss, Klauseln, Arten, Betriebsübergang',
        linkedFields: [],
        norms: [
          {
            abbreviation: 'BGB',
            singleNormDescription: '§ 611a',
          },
          {
            abbreviation: 'GewO',
            singleNormDescription: '§ 105',
          },
        ],
        children: [],
        parent: {
          hasChildren: true,
          identifier: 'AR',
          text: 'Arbeitsrecht',
          linkedFields: [],
          norms: [],
          children: [],
          parent: undefined,
        },
      },
    ]
    const fieldOfLaw = ref(
      fieldOfLawValues.map((item) => <ComboboxItem>{ label: item.text, value: item }),
    )
    const execute = async () => {
      if (filter?.value && filter.value.length > 0) {
        const filteredItems = fieldOfLawValues.filter((item) =>
          item.text.toLowerCase().startsWith((filter.value as string).toLowerCase()),
        )
        const filteredComboBoxItems = filteredItems.map(
          (item) => <ComboboxItem>{ label: item.text, value: item },
        )
        fieldOfLaw.value = [...filteredComboBoxItems]
      } else {
        fieldOfLaw.value = fieldOfLawValues.map(
          (item) => <ComboboxItem>{ label: item.text, value: item },
        )
      }
      return service.getFieldOfLawSearchByIdentifier(filter)
    }
    const result: ComboboxResult<ComboboxItem[]> = {
      data: fieldOfLaw,
      execute: execute,
      canAbort: computed(() => false),
      abort: () => {},
    }
    return result
  },
}

export default service
