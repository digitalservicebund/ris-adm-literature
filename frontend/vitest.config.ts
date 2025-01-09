import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'node',
      environmentMatchGlobs: [['src/components/**', 'jsdom']],
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      pool: 'threads',
      coverage: {
        provider: 'v8',
        reporter: ['lcov', 'text', 'html'],
        // Changes to this also need to be reflected in the sonar-project.properties
        exclude: [
          // Configuration and generated outputs
          '**/[.]**',
          'coverage/**/*',
          'dist/**/*',
          '**/.*rc.{?(c|m)js,yml}',
          '*.config.{js,ts}',

          // Types
          '**/*.d.ts',

          // Tests
          'test/**/*',
          'e2e/**/*',

          // App content we're not interested in covering with unit tests. If you
          // add something here, please also add a comment explaining why the
          // exclusion is necessary.

          // Views are too complex too set up and mock in unit tests, we're covering
          // those with E2E test instead. (App is also a view)
          'src/views/**/*',
          'src/App.vue',

          // If necessary to use e.g. guards, we'll have a router-guards file that
          // then should be tested
          'src/router.ts',

          // Just the init file, nothing much to test here.
          'src/main.ts',

          // Stories are just for internal development use and don't need to be
          // tested
          'src/**/*.story.vue',
        ],
      },
    },
  }),
)
