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

* Our app gets deployed to three environments:
    * Staging
    * UAT (user acceptance testing)
    * Production (or "prod" for short)
* Each environment has its own rules and purposes

### Rules and Purposes

Note: The following is how we aim to set up the environments. It does not mean that all the aspects do exist already (e.g. our goal of how migration should behave does not imply that there is a migration already).

* Staging:
    * Purpose
        * Supporting the product development team's work.

    * Access
        * The product team (primarily)
        * DigitalService staff
        * Testers
        * Documentalists
        * Other stakeholders

    * Features
        * What's under development

    * Uptime
        * Staging may be down any time due to any reason and for any amount of time.

    * Data 
        * Staging data may come into existence, disappear or be changed any time.

    * Repeated migrations
        * Staging data gets newly migrated documents 🚧 "upserted" (i.e. added new documents, update LDML if document exists) in irregular intervals.

* UAT
    * Purpose
        * Have a production-like environment, but with a safety net: no publishing to public(production) portal.

    * Access
        * The product team
        * DigitalService staff
        * Testers (primarily)
        * Documentalists (primarily)
        * Other stakeholders

    * Features
        * What's "done", i.e implemented and has passed tech and functional review

    * Uptime
        * UAT is expected to be up "all the time" with the following caveats:
            * We may announce downtimes.
            * We have no on call service, so it's reasonable to expect it to be up between 8:00 and 17:00 on working days.

    * Data
        * Data in UAT is expected to persist and only change on user interaction.

    * Repeated migration
        * UAT data gets newly migrated documents "upserted" (i.e. added new documents, update LDML if document exists) in irregular intervals.

* Production:
    * Primary goal
        * Production is used by documentalists for performing their documentation duties.
        * What's published is made available to end users of the NeuRIS portal.

    * Access
        * Documentalists, only

    * Features
        * What's "done", i.e implemented and has passed tech and functional review

    * Uptime
        * UAT is expected to be up "all the time" with the following caveats:
            * We may announce downtimes.
            * We have no on call service, so it's reasonable to expect it to be up between 8:00 and 17:00 on working days.

    * Data
        * Data in UAT is expected to persist and only change on user (= documentalists, only; cf. above) interaction.

    * ❓❓ Repeated migration
        * This is unsolved. Especially how to handle or prevent the case of a document being changed in both the old and the new system at the same time


## Consequences

❗What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

What we think will become more easy:

* Supporting the needs of various user groups (product team, DigitalService staff, testers, other stakeholders, documentalists)

* Managing risks wrt. data integrity and access controls.

What we expect to become more difficult:

* Operating three environments adds complexity.
* Separating code from releases (e.g. feature flags).


