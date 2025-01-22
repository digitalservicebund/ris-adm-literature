# 0. Template

Date: 2025-01-22

## Status

Suggested

## Context

End-to-end tests make sure that our web app behaves to the user as we intend it to behave. However, there are different options as to where to draw the line of what counts as "what the user experiences".

At the rather far out sides of the spectrum we'd find

* Observing frontend component unit tests - which we reject as it does not take the browser engines into account
* Observing users in their use of our app on their own devices - which we reject as well as the results would be subject to a lot of factors outside the scope of our solution

Between the extremes, there are two typical setups:

* Observing how the site behaves in a web browser when both, app and browser are run locally
* Observing The site as it behaves in a web browser when accessing a hosted version of our application.

Both come with their set of trade-offs, related to the question of what factors should or should not influence the result as well as the question of what problem the E2E test is intended to solve.

## Decision

The change that we're proposing or have agreed to implement.

We'll run our E2E tests in a "localhost" environment. 

## Consequences

What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.
