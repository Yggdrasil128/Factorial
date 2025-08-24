import {fileURLToPath, URL} from 'node:url';

import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';

const devServerProxyHost: string = 'localhost:8080';

// https://vitejs.dev/config/
// noinspection HttpUrlsUsage
export default defineConfig({
  plugins: [
    vue()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://' + devServerProxyHost,
        changeOrigin: true,
      },
      '/websocket': {
        target: 'ws://' + devServerProxyHost,
        ws: true,
        changeOrigin: true,
        rewriteWsOrigin: true,
      },
    }
  }
});
