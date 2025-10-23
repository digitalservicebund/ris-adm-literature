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

### Start the backend (this requires a docker demon)

```bash
cd ./backend
./gradlew bootRun
```

### Start the frontend in dev mode

```bash
cd ./frontend
npm run dev
```

The last step will tell you where to point your browser in order to access the app.

### Login
When visiting the app with your browser, you'll be asked to log in. Depending on which document type's app you want to use, there are two logins configured:

* One for BSG administrative directives
* One for literature

For the required emails/passwords, please refer to the local keycloak configuation file at [./backend/keycloak-config/realm.json](./backend/keycloak-config/realm.json).

## Database Schemas
The backend uses a PostgreSQL database containing multiple schemas:
* adm: Stores data related to administrative directives.
* literature:  Stores data related to literature documents.
* lookup_tables: Contains shared reference data (like institutions, regions) accessed via views from the adm and literature schemas.

Both the adm and literature schemas have identical table structures but hold distinct sets of documents. The application dynamically routes database operations to the correct schema based on the logged-in user's context.


## CI/CD Workflow

This project uses GitHub Actions to automate testing, builds, and deployments.

### Automated Deployments

* **Pull Requests:** All PRs automatically trigger builds and required status checks.
* **Staging Environment:** Merging a PR to the **`main`** branch automatically builds the final images and deploys the new version to the Staging environment.

### Manual Test Deployments to Staging

You can manually deploy a feature branch to the Staging environment for testing. This is a temporary deployment that will be overwritten by the next merge or push to `main`.

1.  **Build the Image:** Add the **`dev-env`** label to your pull request. This triggers a workflow to build and push a container image.

2.  **Find the Image Tag:** In the completed GitHub Actions run, find the full image tag in the logs of the `build-frontend-image` or `build-backend-image` job. It will look similar to this: `ris-adm-literature-backend:b33189fda5c7819df197a78e146fc20a75b65e64`.

3.  **Deploy the Image:**
  * Go to the [kustomization.yaml](https://github.com/digitalservicebund/ris-adm-literature-infra/blob/main/manifests/overlays/staging/kustomization.yaml) file in the infrastructure repository.
  * Update the `newTag` value for the corresponding image with the tag you copied and commit the change.
