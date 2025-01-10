import type { LocationQuery } from 'vue-router'
// import { useInternalUser } from "@/composables/useInternalUser"
import type MenuItem from '@/domain/menuItem'

export function useCaseLawMenuItems(
  documentNumber: string | undefined,
  routeQuery: LocationQuery, // Replace with the appropriate type for route query
): MenuItem[] {
  const baseRoute = {
    params: { documentNumber },
    query: routeQuery,
  }

  // const isInternalUser = useInternalUser()

  return [
    {
      label: 'Fundstellen',
      route: {
        ...baseRoute,
        name: 'FundstellenPage',
      },
    },
    {
      label: 'Rubriken',
      route: {
        name: 'RubrikenPage',
        ...baseRoute,
      },
      children: [
        {
          label: 'Formaldaten',
          route: {
            ...baseRoute,
            name: 'RubrikenPage',
            hash: '#formaldaten',
          },
        },
        {
          label: 'Gliederung',
          route: {
            ...baseRoute,
            name: 'RubrikenPage',
            hash: '#gliederung',
          },
        },
        {
          label: 'Inhaltliche Erschließung',
          route: {
            ...baseRoute,
            name: 'RubrikenPage',
            hash: '#contentRelatedIndexing',
          },
        },
        {
          label: 'Kurzreferat',
          route: {
            ...baseRoute,
            name: 'RubrikenPage',
            hash: '#kurzreferat',
          },
        },
      ],
    },
    {
      label: 'Abgabe',
      route: {
        ...baseRoute,
        name: 'AbgabePage',
      },
    },
  ]
}
