import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'
import { RisUiTheme, RisUiLocale } from '@digitalservicebund/ris-ui/primevue'
import '@digitalservicebund/ris-ui/fonts.css'
import App from './App.vue'
import * as Sentry from '@sentry/vue'
import '@/styles/global.css'
import router from '@/router.ts'
import { ToastService } from 'primevue'
import keycloak from '@/services/keycloak.ts'
import { setupAxiosInterceptors } from '@/services/httpClient.ts'

const app = createApp(App)

// env.PROD does not mean the "prod" environment, but should be true for all hosted
// environments (as opposed to local development), cf: https://vite.dev/guide/env-and-mode
// if required locally, we can build and run the app from "/dist" - that one is PROD, too.
if (import.meta.env.PROD) {
  Sentry.init({
    app,
    environment: window.location.host,
    dsn: 'https://7c56d29d5dd2c9bd48fc72a8edaffe57@o1248831.ingest.us.sentry.io/4508482613084160',
    integrations: [],
  })
}

keycloak
  .init({
    onLoad: 'login-required',
  })
  .then((authenticated) => {
    if (authenticated) {
      console.log('Authenticated successfully!')
      setupAxiosInterceptors()
      const app = createApp(App)
      app
        .use(createPinia())
        .use(PrimeVue, {
          unstyled: true,
          pt: RisUiTheme,
          locale: RisUiLocale.deDE,
        })
        .use(router)
        .use(ToastService)
        .mount('#app')

      // clear keycloak info from pathname
      router.isReady().then(() => {
        window.history.replaceState(null, '', window.location.pathname)
      })
    } else {
      console.warn('Not authenticated!')
    }
  })
  .catch((error) => {
    console.error('Keycloak initialization failed:', error)
    document.getElementById('app').innerHTML = 'Failed to initialize authentication.'
  })
