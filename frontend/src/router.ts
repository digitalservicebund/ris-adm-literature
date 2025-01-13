import { createRouter, createWebHistory } from 'vue-router'
import StartPage from './routes/StartPage.vue'
import PeriodicalPage from '@/routes/PeriodicalPage.vue'
import RubrikenPage from './routes/RubrikenPage.vue'
import AbgabePage from './routes/AbgabePage.vue'
import ErrorNotFound from './routes/ErrorNotFound.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'StartPage',
      component: StartPage,
    },
    {
      path: '/fundstellen',
      name: 'PeriodicalPage',
      component: PeriodicalPage,
    },
    {
      path: '/rubriken',
      name: 'RubrikenPage',
      component: RubrikenPage,
    },
    {
      path: '/abgabe',
      name: 'AbgabePage',
      component: AbgabePage,
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
