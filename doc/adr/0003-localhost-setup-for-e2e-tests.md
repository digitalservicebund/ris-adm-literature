# 0. Template

Date: 2025-01-22

## Status

Suggested

## Context

End-to-end tests assure that our web app behaves _to the user_ as we intend it to behave. However, there are numerous options with respect to where to draw the line between what does and what does not count as part of the user's experiences _with respect to the scope of our project_.

Between the extremes (like: running requests via `curl`, ignoring the influence of the browser engines, or: observing the user in their location on their devices and their networks which ties the results to a lot of factors outside of our scope), there are two typical setups:

- Observing how the site behaves in a web browser when both, app and browser are run locally ("localhost" setup)
- Observing how the site behaves in a web browser when accessing a hosted version of our application ("testing against staging" setup)

Both come with their set of trade-offs, mainly related to two questions:

- What factors should or should not influence the result?
- What problem is the E2E test intended to solve?

## Decision

1. We'll run our E2E tests in a "localhost" environment.

   - More precisely: When running our E2E tests in our GitHub pipeline, the GitHub runner instance will start the application locally and then run `playwright` tests in three browser engines (`chromium`, `firefox` and `webkit`) against the local web app.

1. Passing the E2E tests is one of the requirements for a deployment.

## Consequences

This decision favors E2E tests running reliably (i.e. reducing some sources of flakyness) over E2E tests covering infrastructure behavior (e.g. bad deployments, failing database migrations).

If we want to guard against infrastructure failures when e.g. setting up a deployment "cascade" along multiple environments, we need to use other means than E2E tests.

With respect to the topics mentioned above these are the consequences:

- Factors that we want to influence the result:
  - The behavior of the browser engines
  - The behavior of the frontend code
  - The behavior of the backend code
  - The behavior of the database
- Factors that we do _not_ want to influence the behavior:
  - Networking between the browser and the application
  - Authentication via an external service
  - Specifics of the machine that runs the browser
- Problems that the E2E tests are intended to solve:
  - Break the pipeline if the interaction with the app running in a browser does not work as intended
    - Does the _combination_ of (unit tested) components behave as we want it to do?
    - Does _state_ get preserved as intended?
- Problems that the E2E tests are not intended to solve:
  - Prevent the deployment to `production` if deployment of other environments (`staging` or similar) failed for any reason
