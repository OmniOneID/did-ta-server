import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    proxy: {
      // /noti/admin/v1 → http://localhost:8090/noti/admin/v1
      '/noti/admin/v1': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      // /tas/admin/v1 → http://localhost:8090/tas/admin/v1
      '/tas/admin/v1': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
    },
  },
});
