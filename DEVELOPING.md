# Development / Tech Notes

The application consists of a Vue/TypeScript frontend and a Java/SpringBoot backend.

## Git Hooks Setup 👈 (do not skip this!)

This repository uses Git hooks for

- preventing accidentally pushing secrets or other sensitive information

In order to make use of these, do install the following tools:

- [`lefthook`](https://github.com/evilmartians/lefthook) (Git hooks)
- [`trivy`](https://github.com/aquasecurity/trivy) (Secret and Vulnerability scanning)

then install the hooks via

```bash
lefthook install
```

The git hook installed always executes lefthook which reads the configuration in `lefthook.yml`. Therefore, even the file is changed, re-running the `install` command is _not_ needed.

## Run Frontend with Docker

Details on each can be found in

- [./frontend/DEVELOPING.md](./frontend/DEVELOPING.md) and
- [./backend/DEVELOPING.md](./backend/DEVELOPING.md)

## Quick Start

Start the backend (this requires a docker demon)

```bash
cd ./backend
./gradlew bootRun
```

Start the frontend in dev mode

```bash
cd ./frontend
npm run dev
```

The last step will tell you where to point your browser in order to access the app.

## CI/CD Workflow

This project uses GitHub Actions to automate testing, builds, and deployments.

### Automated Deployments

* **Pull Requests:** All PRs automatically trigger builds and required status checks.
* **Staging Environment:** Merging a PR to the **`main`** branch automatically builds the final images and deploys the new version to the Staging environment.

### Manual Test Deployments to Staging

You can manually deploy a feature branch to the Staging environment for testing. This is a temporary deployment that will be overwritten by the next merge or push to `main`.

1.  **Build the Image:** Add the **`dev-env`** label to your pull request. This triggers a workflow to build and push a container image.

2.  **Find the Image Tag:** In the completed GitHub Actions run, find the full image tag in the logs of the `build-frontend-image` or `build-backend-image` job. It will look similar to this: `ris-adm-vwv-frontend:65969a5aa2935dc8f7cdeffffdb0f8ca823b3ec3`.

3.  **Deploy the Image:**
  * Go to the [kustomization.yaml](https://github.com/digitalservicebund/ris-adm-vwv-infra/blob/main/manifests/overlays/staging/kustomization.yaml) file in the infrastructure repository.
  * Update the `newTag` value for the corresponding image with the tag you copied and commit the change.
