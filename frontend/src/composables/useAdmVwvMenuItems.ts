import type { LocationQuery } from 'vue-router'
// import { useInternalUser } from "@/composables/useInternalUser"
import type MenuItem from '@/domain/menuItem'

export function useAdmVwvMenuItems(
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
        name: 'vwv-documentUnit-documentNumber-fundstellen',
      },
    },
    {
      label: 'Rubriken',
      route: {
        name: 'vwv-documentUnit-documentNumber-rubriken',
        ...baseRoute,
      },
      children: [
        {
          label: 'Formaldaten',
          route: {
            ...baseRoute,
            name: 'vwv-documentUnit-documentNumber-rubriken',
            hash: '#formaldaten',
          },
        },
        {
          label: 'Gliederung',
          route: {
            ...baseRoute,
            name: 'vwv-documentUnit-documentNumber-rubriken',
            hash: '#gliederung',
          },
        },
        {
          label: 'Inhaltliche Erschließung',
          route: {
            ...baseRoute,
            name: 'vwv-documentUnit-documentNumber-rubriken',
            hash: '#inhaltlicheErschliessung',
          },
        },
        {
          label: 'Kurzreferat',
          route: {
            ...baseRoute,
            name: 'vwv-documentUnit-documentNumber-rubriken',
            hash: '#kurzreferat',
          },
        },
      ],
    },
    {
      label: 'Abgabe',
      route: {
        ...baseRoute,
        name: 'vwv-documentUnit-documentNumber-abgabe',
      },
    },
  ]
}
