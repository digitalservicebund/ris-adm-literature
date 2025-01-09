import {createRouter, createWebHistory} from "vue-router";
import StartPage from "@/routes/StartPage.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'StartPage',
      component: StartPage
    }]
})

export default router
