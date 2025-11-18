import { createRouter, createWebHistory } from 'vue-router'
import { ROUTE_NAMES, ROUTE_PATHS } from '@/constants/routes'
import { useAuthentication } from '@/services/auth'
import { roleToHomeRouteMap, USER_ROLES } from '@/config/roles'
import ErrorNotFound from './routes/ErrorNotFound.vue'
import AbgabePage from './routes/AbgabePage.vue'
import AdmRubriken from './routes/adm/documentUnit/[documentNumber]/RubrikenPage.vue'
import FundstellenPage from '@/routes/adm/documentUnit/[documentNumber]/FundstellenPage.vue'
import NewDocument from '@/routes/NewDocument.vue'
import Forbidden from '@/routes/Forbidden.vue'
import StartPageTemplate from './routes/OverviewPage.vue'
import LiteratureOverviewPage from './routes/literature/Overview.view.vue'
import DocumentUnits from './components/document-units/DocumentUnits.vue'
import EditDocument from './routes/EditDocument.vue'
import UliRubriken from './routes/literature/uli/RubrikenPage.vue'
import { DocumentCategory } from './domain/documentType'
import RootRedirectPage from './routes/RootRedirectPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: ROUTE_PATHS.ROOT,
      name: ROUTE_NAMES.ROOT_REDIRECT,
      component: RootRedirectPage,
      beforeEnter: (to, from, next) => {
        const auth = useAuthentication()
        const userRoles = auth.getRealmRoles()

        // Not authenticated or no roles
        if (!auth.isAuthenticated() || userRoles.length === 0) {
          next({ path: ROUTE_PATHS.FORBIDDEN })
          return
        }

        // Exactly one role: redirect
        if (userRoles.length === 1 && userRoles[0]) {
          const routeName = roleToHomeRouteMap[userRoles[0]]
          if (routeName) {
            next({ name: routeName })
          } else {
            next({ path: ROUTE_PATHS.FORBIDDEN })
          }
          return
        }

        // Multiple roles: stay on RootRedirectPage
        next()
      },
    },
    {
      path: ROUTE_PATHS.ADM.BASE,
      meta: {
        requiresRole: [USER_ROLES.ADM_USER, USER_ROLES.ADM_VWV_USER],
        documentCategory: DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
      },
      children: [
        {
          path: '',
          component: StartPageTemplate,
          props: { title: 'Übersicht Verwaltungsvorschriften' },
          children: [
            {
              path: '',
              name: ROUTE_NAMES.ADM.START_PAGE,
              component: DocumentUnits,
            },
          ],
        },
        {
          path: ROUTE_PATHS.ADM.DOCUMENT_UNIT.NEW,
          name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.NEW,
          component: NewDocument,
        },
        {
          path: ROUTE_PATHS.ADM.DOCUMENT_UNIT.EDIT,
          name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.EDIT,
          component: EditDocument,
          props: true,
          redirect: { name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.FUNDSTELLEN },
          children: [
            {
              path: ROUTE_PATHS.ADM.DOCUMENT_UNIT.FUNDSTELLEN,
              name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.FUNDSTELLEN,
              props: true,
              component: FundstellenPage,
            },
            {
              path: ROUTE_PATHS.ADM.DOCUMENT_UNIT.RUBRIKEN,
              name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.RUBRIKEN,
              props: true,
              component: AdmRubriken,
            },
            {
              path: ROUTE_PATHS.ADM.DOCUMENT_UNIT.ABGABE,
              name: ROUTE_NAMES.ADM.DOCUMENT_UNIT.ABGABE,
              props: true,
              component: AbgabePage,
            },
          ],
        },
      ],
    },
    {
      path: '/literatur-unselbststaendig/:pathMatch(.*)*',
      redirect: (to) => {
        const oldSuffix = Array.isArray(to.params.pathMatch) ? to.params.pathMatch.join('/') : ''
        const newPath = oldSuffix ? `${ROUTE_PATHS.ULI.BASE}/${oldSuffix}` : ROUTE_PATHS.ULI.BASE
        return { path: newPath }
      },
    },
    {
      path: ROUTE_PATHS.ULI.BASE,
      meta: {
        requiresRole: [USER_ROLES.LITERATURE_USER],
        documentCategory: DocumentCategory.LITERATUR_UNSELBSTAENDIG,
      },
      children: [
        {
          path: '',
          name: ROUTE_NAMES.ULI.START_PAGE,
          component: LiteratureOverviewPage,
        },
        {
          path: ROUTE_PATHS.ULI.DOCUMENT_UNIT.NEW,
          name: ROUTE_NAMES.ULI.DOCUMENT_UNIT.NEW,
          component: NewDocument,
        },
        {
          path: ROUTE_PATHS.ULI.DOCUMENT_UNIT.EDIT,
          name: ROUTE_NAMES.ULI.DOCUMENT_UNIT.EDIT,
          component: EditDocument,
          props: true,
          redirect: { name: ROUTE_NAMES.ULI.DOCUMENT_UNIT.RUBRIKEN },
          children: [
            {
              path: ROUTE_PATHS.ULI.DOCUMENT_UNIT.RUBRIKEN,
              name: ROUTE_NAMES.ULI.DOCUMENT_UNIT.RUBRIKEN,
              component: UliRubriken,
            },
            {
              path: ROUTE_PATHS.ULI.DOCUMENT_UNIT.ABGABE,
              name: ROUTE_NAMES.ULI.DOCUMENT_UNIT.ABGABE,
              props: true,
              component: AbgabePage,
            },
          ],
        },
      ],
    },
    {
      path: ROUTE_PATHS.SLI.BASE,
      meta: {
        requiresRole: [USER_ROLES.LITERATURE_USER],
        documentCategory: DocumentCategory.LITERATUR_SELBSTAENDIG,
      },
      children: [
        {
          path: '',
          name: ROUTE_NAMES.SLI.START_PAGE,
          component: LiteratureOverviewPage,
        },
      ],
    },

    {
      // cf. https://router.vuejs.org/guide/essentials/dynamic-matching.html
      path: '/:pathMatch(.*)*',
      name: ROUTE_NAMES.NOT_FOUND,
      component: ErrorNotFound,
    },
    {
      path: ROUTE_PATHS.FORBIDDEN,
      name: ROUTE_NAMES.FORBIDDEN,
      component: Forbidden,
    },
  ],
})

router.beforeEach((to, from, next) => {
  const auth = useAuthentication()
  const requiredRoles = to.meta.requiresRole as string[] | undefined

  if (requiredRoles && requiredRoles.length > 0 && auth.isAuthenticated()) {
    const hasRequiredRole = requiredRoles.some((role) => auth.hasRealmRole(role))

    if (hasRequiredRole) {
      next()
    } else {
      // User does not have the required role, redirect to the 'Forbidden' page
      next({ name: ROUTE_NAMES.FORBIDDEN })
    }
  } else {
    // bareId / keycloak manages it
    next()
  }
})

export default router
