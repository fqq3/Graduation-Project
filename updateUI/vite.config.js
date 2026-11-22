import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, __dirname, '')

  return {
    plugins: [
      vue(),
      vueJsx(),
      createSvgIconsPlugin({
        iconDirs: [path.resolve(__dirname, 'src/assets/icons/svg')],
        symbolId: 'icon-[name]'
      })
    ],
    esbuild: {
      include: /src\/.*\.[jt]sx?$/,
      loader: 'jsx'
    },
    optimizeDeps: {
      esbuildOptions: {
        loader: {
          '.js': 'jsx'
        }
      }
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
        '@riophae/vue-treeselect': path.resolve(__dirname, 'src/components/LegacyTreeselect/index.vue'),
        '@riophae/vue-treeselect/dist/vue-treeselect.css': path.resolve(__dirname, 'src/assets/styles/treeselect-shim.css'),
        'vuedraggable': path.resolve(__dirname, 'src/components/CompatDraggable/index.vue'),
        'file-saver': path.resolve(__dirname, 'src/shims/file-saver.js'),
        'clipboard': path.resolve(__dirname, 'src/shims/clipboard.js')
      }
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_PORT || 80),
      open: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_PROXY_TARGET || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler'
        }
      }
    }
  }
})
