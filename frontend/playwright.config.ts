import process from 'node:process'
import { defineConfig, devices } from '@playwright/test'

const docTypeToAuthFile = {
  vwv: '../frontend/e2e/.auth/vwv.json',
  uli: '../frontend/e2e/.auth/uli.json',
}

// Map browsers to their devices
const browsers = {
  chromium: devices['Desktop Chrome'],
  firefox: devices['Desktop Firefox'],
  msedge: devices['Desktop Edge'],
  // Safari intentionally excluded because of storageState issues
  // https://github.com/microsoft/playwright/issues/20301
  // https://github.com/microsoft/playwright/issues/35712
};

const docTypeProjects = Object.entries(docTypeToAuthFile).flatMap(([docType, authFile]) =>
  Object.entries(browsers).map(([browserName, browserDevice]) => ({
    dependencies: ['setup', 'seed data'],
    name: `${browserName}-${docType}`,
    testMatch: new RegExp(`.*/${docType}/.*\\.spec\\.ts`),
    use: {
      ...browserDevice,
      storageState: authFile,
    },
    testIgnore: 'Login.spec.ts',
  }))
);

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true, // ignored in CI, as workers are 1, there
  /* Maximum time one test can run for. */
  timeout: 30 * 1000,
  expect: {
    /**
     * Maximum time expect() should wait for the condition to be met.
     * For example in `await expect(locator).toHaveText();`
     */
    timeout: 5 * 1000,
  },
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 2 : undefined,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: [
    ['html', { outputFolder: 'frontend-e2e-test-report-html' }],
    ['json', { outputFile: 'frontend-e2e-test-report.json' }],
    ['blob', { outputFile: 'frontend-e2e-test-report.blob' }],
  ],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    viewport: { width: 1280, height: 720 },
    /* Maximum time each action such as `click()` can take. Defaults to 0 (no limit). */
    actionTimeout: 5 * 1000,
    /* Base URL to use in actions like `await page.goto('/')`.
    needs to be in sync with "webserver" config below*/
    baseURL: 'http://localhost:5173',

    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'on-first-retry',

    screenshot: process.env.CI ? 'off' : 'only-on-failure',
  },

  /* Configure projects for major browsers */
  projects: [
    {
      name: 'setup',
      testMatch: 'auth-setup.ts',
    },
    {
      dependencies: ['setup'],
      name: 'seed data',
      testMatch: 'seed-data.ts',
      use: {
        storageState: docTypeToAuthFile['vwv'],
      },
      testIgnore: 'Login.spec.ts',
    },
    // The following tests test the login functionality itself, so they need to be separate projects which run without
    // the playwright login as a dependency.
    {
      name: 'loginChromium',
      testMatch: 'login.spec.ts',
      use: {
        ...devices['Desktop Chrome'],
      },
    },
    {
      name: 'loginFirefox',
      testMatch: 'login.spec.ts',
      use: {
        ...devices['Desktop Firefox'],
      },
    },
    {
      name: 'loginEdge',
      testMatch: 'login.spec.ts',
      use: {
        ...devices['Desktop Edge'],
      },
    },

    ...docTypeProjects,
  ],

  /* Run your local dev server before starting the tests */
  webServer: {
    /**
     * Use the dev server by default for faster feedback loop.
     * Use the preview server on CI for more realistic testing.
     * Playwright will re-use the local server if there is already a dev-server running.
     */
    // command: process.env.CI ? 'npm run preview' : 'npm run dev',
    command: 'npm run dev',
    // port: process.env.CI ? 4173 : 5173,
    port: 5173,
    reuseExistingServer: !process.env.CI,
  },
})
