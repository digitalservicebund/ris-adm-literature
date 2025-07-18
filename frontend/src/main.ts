import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'
import { RisUiTheme, RisUiLocale } from '@digitalservicebund/ris-ui/primevue'
import '@digitalservicebund/ris-ui/fonts.css'
import App from './App.vue'
import '@/styles/global.css'
import router from '@/router.ts'
import { ToastService } from 'primevue'
import { useAuthentication } from '@/services/auth.ts'
import { getEnv } from '@/services/envService.ts'

try {
  // Fetch environment configuration
  const env = await getEnv()

  const app = createApp(App)

  // Configure authentication
  const auth = useAuthentication()
  await auth.configure({
    url: env.authUrl,
    clientId: env.authClientId,
    realm: env.authRealm,
  })

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
} catch (e: unknown) {
  // If an error occurs above, catch it here

  // Get references to the error message container and the loading message
  const errorContainer = document.getElementById('error-container')
  const loadingMessage = document.querySelector('.fallback p')

  if (errorContainer) {
    errorContainer.removeAttribute('hidden')
    if (loadingMessage) {
      loadingMessage.setAttribute('hidden', 'true')
    }
    if (e instanceof Error) {
      const errorDetails = errorContainer.querySelector('#error-detail')
      if (errorDetails) {
        errorDetails.textContent = e.message
      }
    }
  }
}
