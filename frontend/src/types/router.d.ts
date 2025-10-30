import 'vue-router'
import type { USER_ROLES } from '@/config/roles'
import type { DocumentCategory } from '@/domain/documentType'

declare module 'vue-router' {
  interface RouteMeta {
    requiresRole: (typeof USER_ROLES)[keyof typeof USER_ROLES][]
    documentCategory: DocumentCategory
  }
}
