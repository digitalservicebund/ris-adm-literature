import { createRouter, createWebHistory } from 'vue-router'
import StartPage from './routes/StartPage.vue'
import ErrorNotFound from './routes/ErrorNotFound.vue'
import DocumentUnitWrapper from './routes/documentUnit/[documentNumber].vue'
import AbgabePage from './routes/documentUnit/[documentNumber]/AbgabePage.vue'
import RubrikenPage from './routes/documentUnit/[documentNumber]/RubrikenPage.vue'
import FundstellenPage from '@/routes/documentUnit/[documentNumber]/FundstellenPage.vue'
import New from '@/routes/documentUnit/new.vue'

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
      component: New,
    },
    {
      path: '/documentUnit/:documentNumber',
      name: 'documentUnit-documentNumber',
      component: DocumentUnitWrapper,
      props: true,
      redirect: { name: 'documentUnit-documentNumber-fundstellen' },
      children: [
        {
          path: '/documentUnit/:documentNumber/fundstellen',
          name: 'documentUnit-documentNumber-fundstellen',
          props: true,
          component: FundstellenPage,
        },
        {
          path: '/documentUnit/:documentNumber/rubriken',
          name: 'documentUnit-documentNumber-rubriken',
          props: true,
          component: RubrikenPage,
        },
        {
          path: '/documentUnit/:documentNumber/abgabe',
          name: 'documentUnit-documentNumber-abgabe',
          props: true,
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
