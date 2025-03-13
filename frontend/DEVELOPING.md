## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

### Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customizing Configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Development Tooling

### Installing Dependencies

```sh
npm install
```

### Compilation and Startup Variants

#### Development Mode (Hot Reload)

```sh
npm run dev
```

#### Production Build via a Docker Build

```bash
docker build --tag ris-adm-vwv-frontend-local:dev .
docker run -p 5173:5173 ris-adm-vwv-frontend-local:dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Run Unit Tests With [Vitest](https://vitest.dev/)

```sh
npm run test:unit
```

### Continuously Run Unit Tests With [Vitest](https://vitest.dev/) in Watcher Mode

```sh
npm run test:watch
```

### Run Unit Test and Check Coverage with [Vitest](https://vitest.dev/)

```sh
npm run test:coverage
```

### Run End-to-End Tests With [Playwright](https://playwright.dev)

When running e2e tests locally you need to have the backend running already.

```sh
# Install browsers for the first run
npx playwright install

# Not required locally, but when testing on CI we must build the project first
npm run build

# Runs the end-to-end tests
npm run test:e2e
# Runs the tests only on Chromium
npm run test:e2e -- --project=chromium
# Runs the tests of a specific file
npm run test:e2e -- e2e/AbgabePage.spec.ts
# Runs the tests in debug mode
npm run test:e2e -- --debug
# Runs the tests only on Chromium and for a specific file and in debug mode
npm run test:e2e -- e2e/AbgabePage.spec.ts --debug --project=chromium
# Run tests in interactive UI mode.
npm run test:e2e -- --ui
# Run tests in headed browsers
npm run test:e2e -- --headed
```

### Lint With [ESLint](https://eslint.org/)

```sh
npm run lint
```

### Format With [Prettier](https://prettier.io)

```sh
npm run format
```
