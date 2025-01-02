import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // 前端的端口號
    proxy: {
      '/lastwine': {
        target: 'http://localhost:8080', // 後端的地址
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
