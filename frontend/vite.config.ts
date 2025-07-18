import path from 'path'

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
  ],

  server: {
    proxy: {
      '^/(api|environment)': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
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
