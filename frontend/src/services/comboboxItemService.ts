import { useFetch, type UseFetchReturn } from '@vueuse/core'
import type { Ref } from 'vue'
import { API_PREFIX } from './httpClient'
import type { ComboboxInputModelType, ComboboxItem } from '@/components/input/types'
import LegalPeriodical from '@/domain/legalPeriodical.ts'
import type { DocumentType } from '@/domain/documentUnit'
import type { ComboboxResult } from '@/domain/comboboxResult.ts'
import type { CitationType } from '@/domain/citationType'
import { computed, ref } from 'vue'
import type { NormAbbreviation } from '@/domain/normAbbreviation.ts'
import ActiveReference, { ActiveReferenceType } from '@/domain/activeReference.ts'
import type { FieldOfLaw } from '@/domain/fieldOfLaw'
import errorMessages from '@/i18n/errors.json'
import { type RegionApiResponse, type InstitutionApiResponse } from '@/domain/normgeber'
import type { Court } from '@/domain/court'

enum Endpoint {
  documentTypes = 'lookup-tables/document-types',
  fieldsOfLaw = 'lookup-tables/fields-of-law',
  legalPeriodicals = 'lookup-tables/legal-periodicals',
  institutions = 'lookup-tables/institutions',
  regions = 'lookup-tables/regions',
}

function formatDropdownItems(
  responseData: ComboboxInputModelType[],
  endpoint: Endpoint,
): ComboboxItem[] {
  switch (endpoint) {
    case Endpoint.documentTypes: {
      return (responseData as DocumentType[]).map((item) => ({
        label: item.name,
        value: {
          label: item.name,
          abbreviation: item.abbreviation,
        },
        additionalInformation: item.abbreviation,
      }))
    }
    case Endpoint.fieldsOfLaw: {
      return (responseData as FieldOfLaw[]).map((item) => ({
        label: item.identifier,
        value: item,
        additionalInformation: item.text,
      }))
    }
    case Endpoint.legalPeriodicals: {
      return (responseData as LegalPeriodical[]).map((item) => ({
        label: '' + item.abbreviation + ' | ' + item.title,
        value: {
          abbreviation: item.abbreviation,
          title: item.title,
          subtitle: item.subtitle,
          citationStyle: item.citationStyle,
        },
        additionalInformation: item.subtitle,
      }))
    }
    case Endpoint.institutions: {
      return (responseData as InstitutionApiResponse[]).map((item) => ({
        label: item.name,
        value: {
          label: item.name,
          officialName: item.officialName,
          type: item.type,
          regions: item.regions.map((r) => {
            return { label: r.code, longText: r.longText }
          }),
        },
        additionalInformation: item.officialName,
      }))
    }
    case Endpoint.regions: {
      return (responseData as RegionApiResponse[]).map((item) => ({
        label: item.code,
        value: {
          label: item.code,
          longText: item.longText,
        },
      }))
    }
  }

  return []
}

function fetchFromEndpoint(
  endpoint: Endpoint,
  filter: Ref<string | undefined>,
  options?: { pageSize?: number; usePagination?: boolean },
) {
  const requestParams = computed<{ searchTerm?: string; size?: string; paged?: string }>(() => ({
    ...(filter.value ? { searchTerm: filter.value } : {}),
    ...(options?.pageSize != undefined ? { pageSize: options.pageSize.toString() } : {}),
    ...(options?.usePagination != undefined
      ? { usePagination: options?.usePagination?.toString() }
      : {}),
  }))
  const url = computed(() => {
    let queryParams = new URLSearchParams(requestParams.value).toString()
    if (endpoint == Endpoint.fieldsOfLaw) {
      queryParams = queryParams.replace('searchTerm', 'identifier')
    }
    return `${API_PREFIX}${endpoint}?${queryParams}`
  })

  return useFetch<ComboboxItem[]>(url, {
    afterFetch: (ctx) => {
      switch (endpoint) {
        case Endpoint.documentTypes:
          ctx.data = formatDropdownItems(ctx.data.documentTypes, endpoint)
          break
        case Endpoint.fieldsOfLaw:
          ctx.data = formatDropdownItems(ctx.data.fieldsOfLaw, endpoint)
          break
        case Endpoint.legalPeriodicals:
          ctx.data = formatDropdownItems(ctx.data.legalPeriodicals, endpoint)
          break
        case Endpoint.institutions:
          ctx.data = formatDropdownItems(ctx.data.institutions, endpoint)
          break
        case Endpoint.regions:
          ctx.data = formatDropdownItems(ctx.data.regions, endpoint)
          break
      }
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
  getLegalPeriodicals: (filter: Ref<string | undefined>) => UseFetchReturn<ComboboxItem[]>
  getCourts: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getInstitutions: (filter: Ref<string | undefined>) => UseFetchReturn<ComboboxItem[]>
  getRegions: (filter: Ref<string | undefined>) => UseFetchReturn<ComboboxItem[]>
  getDocumentTypes: (filter: Ref<string | undefined>) => UseFetchReturn<ComboboxItem[]>
  getRisAbbreviations: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getActiveReferenceTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getCitationTypes: (filter: Ref<string | undefined>) => ComboboxResult<ComboboxItem[]>
  getFieldOfLawSearchByIdentifier: (
    filter: Ref<string | undefined>,
  ) => UseFetchReturn<ComboboxItem[]>
}

const service: ComboboxItemService = {
  getLegalPeriodicals: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.legalPeriodicals, filter, { usePagination: false }),
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
  getInstitutions: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.institutions, filter, { usePagination: false }),
  getRegions: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.regions, filter, { usePagination: false }),
  getDocumentTypes: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.documentTypes, filter, { usePagination: false }),
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
  getFieldOfLawSearchByIdentifier: (filter: Ref<string | undefined>) =>
    fetchFromEndpoint(Endpoint.fieldsOfLaw, filter, { pageSize: 30 }),
}

export default service
