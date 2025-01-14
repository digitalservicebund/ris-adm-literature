import { createRouter, createWebHistory } from 'vue-router'
import StartPage from './routes/StartPage.vue'
import ErrorNotFound from './routes/ErrorNotFound.vue'
import DocumentUnitNew from './routes/documentUnit/new.vue'
import DocumentUnitWrapper from './routes/documentUnit/[documentNumber].vue'
import AbgabePage from './routes/documentUnit/[documentNumber]/AbgabePage.vue'
import RubrikenPage from './routes/documentUnit/[documentNumber]/RubrikenPage.vue'
import FundstellenPage from '@/routes/documentUnit/[documentNumber]/FundstellenPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'StartPage',
      component: StartPage,
    },
    {
      path: '/documentUnit/new',
      name: 'documentUnit-new',
      component: DocumentUnitNew,
    },
    {
      path: '/documentUnit/:documentNumber',
      name: 'documentUnit-documentNumber',
      component: DocumentUnitWrapper,
      children: [
        {
          path: '/documentUnit/:documentNumber/fundstellen',
          name: 'documentUnit-documentNumber-fundstellen',
          component: FundstellenPage,
        },
        {
          path: '/documentUnit/:documentNumber/rubriken',
          name: 'documentUnit-documentNumber-rubriken',
          component: RubrikenPage,
        },
        {
          path: '/documentUnit/:documentNumber/abgabe',
          name: 'documentUnit-documentNumber-abgabe',
          component: AbgabePage,
        },
      ],
    },
    {
      // cf. https://router.vuejs.org/guide/essentials/dynamic-matching.html
      path: '/:pathMatch(.*)*',
      name: 'Error 404 not found',
      component: ErrorNotFound,
    },
  ],
})

export default router
