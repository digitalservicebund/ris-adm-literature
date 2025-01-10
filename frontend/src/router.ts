import { createRouter, createWebHistory } from 'vue-router'
import StartPage from '@/routes/StartPage.vue'
import FundstellenPage from './routes/FundstellenPage.vue'

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
  ],
})

export default router
