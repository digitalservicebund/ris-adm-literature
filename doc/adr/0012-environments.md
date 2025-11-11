# 12. Environments (staging, UAT, production)

Date: ❗202x-xx-xx

## Status

Proposed ❗ (should become "accepted")

## Context

❗ The issue motivating this decision, and any context that influences or constrains the decision.

Until now all of development and testing took place in a single environment called `staging`.<br>
Now we have introduced two new enviroments, `UAT` and `production` and need to clarify what roles they play and what to expect from them.

Our environments consist of the following components:
* The `ris-adm-literature` application code running in two services (frontend, backend)
* The NeuRIS database (shared across streams in NeuRIS)
* Several S3 compatible buckets for exchanging data with the Portal (for publishing)
* A 3rd party authentication service instance

The term "environment" is meant to indicate that these components (code and data) are not shared across `staging`, `UAT` and `production`.

## Decision

❗The change that we're proposing or have agreed to implement.

* Our app gets deployed to:
    * Staging
    * UAT (user acceptance testing)
    * Production (or "prod" for short)
* Each environment has its own rules and purposes

### Rules and Purposes

Note: The following is how we aim to set up the environments. It does not mean that all the aspects do exist already (e.g. our goal of how migration should work does not imply that there is a migration already).

* Staging:
    * Purpose: Supporting the product development team.
    * Access: Staging may be accessed by DigitalService staff, testers, documentalists and other stakeholders.
    * Codebase: Staging runs the current `main` branch of `ris-adm-literature` (continuous deployment).
    * Uptime: Staging may be down any time due to any reason and for any amount of time.
    * Data: Staging data may come into existence, disappear or be changed any time.
    * ❓ Migration: Staging data gets newly migrated documents "upserted" (i.e. added new documents, update LDML if document exists) in irregular intervals.
* UAT:
    * Purpose: Giving testers a predictable system that is only affected by the testers' actions.
    * Access: UAT may be accessed by testers and by product team members in order to cooperate with the testers.
    * Codebase: UAT runs the current `main` branch of `ris-adm-literature` (continuous deployment).
    * Uptime: UAT is expected to be up "all the time".
        * We have no on call service, so it's reasonable to expect it to be up between 8:00 and 17:00 on working days.
    * Data: Data in UAT is expected to persist and only change on user interaction.
    * ❓ Migration: Staging data gets newly migrated documents "upserted" (i.e. added new documents, update LDML if document exists) in irregular intervals.
* Production:
    * Primary goal: To be used by documentalists performing their documentation duties.
    * Access: Production may be accessed by documentalists and by product team members in order to cooperate with the testers.
    * Codebase: Production runs the current `main` branch of `ris-adm-literature` (continuous deployment).
    * Uptime: Production is expected to be up "all the time".
        * We have no on call service, so it's reasonable to expect it to be up between 8:00 and 17:00 on working days.
    * Data: Data in production is expected to persist and only change on user interaction.
    * ❓ Migration: Staging data gets newly migrated documents "upserted" (i.e. added new documents, update LDML if document exists) in irregular intervals.


## Consequences

❗What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

* Operating three environments adds complexity.
* By running three environments, we hope to better address the uses cases and expectations of end users just as well as testers and developers.