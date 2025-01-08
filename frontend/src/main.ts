import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import * as Sentry from '@sentry/vue'
import '@/styles/global.scss'

const app = createApp(App)

Sentry.init({
  app,
  dsn: 'https://7c56d29d5dd2c9bd48fc72a8edaffe57@o1248831.ingest.us.sentry.io/4508482613084160',
  integrations: [],
})

app.mount('#app')
