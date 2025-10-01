import { createRouter, createWebHistory } from 'vue-router'
import ErrorNotFound from './routes/ErrorNotFound.vue'
import DocumentUnitWrapper from './routes/vwv/documentUnit/[documentNumber].vue'
import AbgabePage from './routes/vwv/documentUnit/[documentNumber]/AbgabePage.vue'
import RubrikenPage from './routes/vwv/documentUnit/[documentNumber]/RubrikenPage.vue'
import FundstellenPage from '@/routes/vwv/documentUnit/[documentNumber]/FundstellenPage.vue'
import New from '@/routes/vwv/documentUnit/new.vue'
import { useAuthentication } from '@/services/auth.ts'
import Forbidden from '@/routes/Forbidden.vue'
import { roleToHomeRouteMap, USER_ROLES } from '@/config/roles.ts'
import StartPageTemplate from './routes/StartPageTemplate.vue'
import DocumentUnits from './components/document-units/DocumentUnits.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'RootRedirect',
      redirect: () => {
        const auth = useAuthentication()
        const userRoles = auth.getRealmRoles()
        // Implementation logic if user has multiple roles will be implemented with RISDEV-9446
        for (const role of userRoles) {
          const routeName = roleToHomeRouteMap[role]
          if (routeName) {
            return { name: routeName }
          }
        }
        // Fallback for users with no matching role or anonymous users
        return { path: '/forbidden' }
      },
    },
    {
      path: '/verwaltungsvorschriften',
      meta: {
        requiresRole: USER_ROLES.VWV_USER,
      },
      children: [
        {
          path: '',
          component: StartPageTemplate,
          props: { title: 'Übersicht Verwaltungsvorschriften' },
          children: [
            {
              path: '',
              name: 'vwv-start-page',
              component: DocumentUnits,
            },
          ],
        },
        {
          path: 'documentUnit/new',
          name: 'vwv-documentUnit-new',
          component: New,
        },
        {
          path: 'documentUnit/:documentNumber',
          name: 'vwv-documentUnit-documentNumber',
          component: DocumentUnitWrapper,
          props: true,
          redirect: { name: 'vwv-documentUnit-documentNumber-fundstellen' },
          children: [
            {
              path: 'fundstellen',
              name: 'vwv-documentUnit-documentNumber-fundstellen',
              props: true,
              component: FundstellenPage,
            },
            {
              path: 'rubriken',
              name: 'vwv-documentUnit-documentNumber-rubriken',
              props: true,
              component: RubrikenPage,
            },
            {
              path: 'abgabe',
              name: 'vwv-documentUnit-documentNumber-abgabe',
              props: true,
              component: AbgabePage,
            },
          ],
        },
      ],
    },
    {
      path: '/literatur-unselbstaendig',
      meta: {
        requiresRole: USER_ROLES.LIT_BAG_USER,
      },
      children: [
        {
          path: '',
          name: 'uli-start-page',
          component: StartPageTemplate,
          props: { title: 'Übersicht Unselbstständige Literatur' },
        },
        {
          path: 'documentUnit/new',
          name: 'uli-documentUnit-new',
          component: New,
        },
        {
          path: 'documentUnit/:documentNumber',
          name: 'uli-documentUnit-documentNumber',
          component: DocumentUnitWrapper,
          props: true,
          redirect: { name: 'uli-documentUnit-documentNumber-rubriken' },
          children: [
            {
              path: 'rubriken',
              name: 'uli-documentUnit-documentNumber-rubriken',
              component: { template: '<div />' }, // renders nothing visible
            },
          ],
        },
      ],
    },
    {
      // cf. https://router.vuejs.org/guide/essentials/dynamic-matching.html
      path: '/:pathMatch(.*)*',
      name: 'Error 404 not found',
      component: ErrorNotFound,
    },
    {
      path: '/forbidden',
      name: 'Forbidden',
      component: Forbidden,
    },
  ],
})

router.beforeEach((to, from, next) => {
  const auth = useAuthentication()
  const requiredRole = to.meta.requiresRole as string | undefined

  if (requiredRole && auth.isAuthenticated()) {
    if (auth.hasRealmRole(requiredRole)) {
      next()
    } else {
      // User does not have the required role, redirect to the 'Forbidden' page
      next({ name: 'Forbidden' })
    }
  } else {
    // bareId / keycloak manages it
    next()
  }
})

export default router
