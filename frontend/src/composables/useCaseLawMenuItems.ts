import type { LocationQuery } from "vue-router"
// import { useInternalUser } from "@/composables/useInternalUser"
import type MenuItem from "@/domain/menuItem"

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
      label: "Fundstellen",
      route: {
        ...baseRoute,
        name: "DocumentUnit",
      },
    },
    {
      label: "Rubriken",
      route: {
        name: "DocumentUnit",
        ...baseRoute,
      },
    },
    {
      label: "Abgabe",
      route: {
        ...baseRoute,
        name: "DocumentUnit",
      },
    },
  ]
}
