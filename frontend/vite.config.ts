import path from 'path'
import { sentryVitePlugin } from '@sentry/vite-plugin'

import { defineConfig } from 'vite'
import Icons from 'unplugin-icons/vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    Icons({
      /* options */
    }),
    vue(),
    vueJsx(),
    vueDevTools(),
    sentryVitePlugin({
      org: 'digitalservice',
      project: 'ris-adm-vwv',
    }),
  ],

  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },

  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '~': path.resolve(__dirname, 'test'),
    },
  },

  build: {
    sourcemap: true,
  },
})
