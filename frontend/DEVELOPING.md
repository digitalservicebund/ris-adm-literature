## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

### Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customizing Configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Development Tooling

### Node version

The `.node-version` file specifies the node version used in this project. You can use [nodenv](https://github.com/nodenv/nodenv) to switch between versions.

```sh
nodenv version # Display the currently active node version
nodenv install # Install the missing node version
```

### Installing Dependencies

```sh
pnpm install
```

### Compilation and Startup Variants

#### Development Mode (Hot Reload)

```sh
pnpm run dev
```

#### Production Build via a Docker Build

```bash
docker build --tag ris-adm-vwv-frontend-local:dev .
docker run -p 5173:5173 ris-adm-vwv-frontend-local:dev
```

### Type-Check, Compile and Minify for Production

```sh
pnpm run build
```

### Run Unit Tests With [Vitest](https://vitest.dev/)

```sh
pnpm run test:unit
```

### Continuously Run Unit Tests With [Vitest](https://vitest.dev/) in Watcher Mode

```sh
pnpm run test:watch
```

### Run Unit Test and Check Coverage with [Vitest](https://vitest.dev/)

```sh
pnpm run test:coverage
```

The reports (summaries as well as line-by-line details) can be found in `./frontend/coverage`.

### Run End-to-End Tests With [Playwright](https://playwright.dev)

When running e2e tests locally you need to have the backend running already.

```sh
# Install browsers for the first run
npx playwright install

# Create test-data.json
pnpm run test:e2e -- e2e/seed-data.ts

# Not required locally, but when testing on CI we must build the project first
pnpm run build

# Runs the end-to-end tests
pnpm run test:e2e
# Runs the tests only on Chromium
pnpm run test:e2e -- --project=chromium
# Runs the tests of a specific file
pnpm run test:e2e -- e2e/AbgabePage.spec.ts
# Runs the tests in debug mode
pnpm run test:e2e -- --debug
# Runs the tests only on Chromium and for a specific file and in debug mode
pnpm run test:e2e -- e2e/AbgabePage.spec.ts --debug --project=chromium
# Run tests in interactive UI mode.
pnpm run test:e2e -- --ui
# Run tests in headed browsers
pnpm run test:e2e -- --headed
```

### Lint With [Oxlint](https://oxc.rs/)

```sh
pnpm run lint
```

### Format With [Oxfmt](https://oxc.rs/)

```sh
pnpm run format
```
