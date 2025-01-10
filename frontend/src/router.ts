import { createRouter, createWebHistory } from 'vue-router'
import StartPage from '@/routes/StartPage.vue'
import PeriodicalPage from '@/routes/PeriodicalPage.vue'
import FundstellenPage from './routes/FundstellenPage.vue'
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
      name: 'FundstellenPage',
      component: FundstellenPage,
    },
    {
      path: '/fundstellen',
      name: 'PeriodicalPage',
      component: PeriodicalPage,
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
