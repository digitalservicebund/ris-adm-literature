import { createRouter, createWebHistory } from 'vue-router'
import StartPage from '@/routes/StartPage.vue'
import PeriodicalPage from '@/routes/PeriodicalPage.vue'

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
  ],
})

export default router
