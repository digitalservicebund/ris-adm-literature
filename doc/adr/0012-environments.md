# 12. Environments (staging, UAT, production)

Date: ❗2025-11-xx

## Status

Proposed ❗ (should become "accepted")

## Context

❗ The issue motivating this decision, and any context that influences or constrains the decision.

Due to stakeholder needs we will introduce two new environments in which our application will be running.

The term "environment" is meant to indicate that while each of them runs all components of our application (details below), they share none of them. Any component may behave differently in each environment.

Our applicaction's components are:
* The `ris-adm-literature` application code running in two services (frontend, backend)
* The NeuRIS database (shared across streams in NeuRIS)
* Several S3 compatible buckets for exchanging data with the Portal (for publishing)
* A 3rd party authentication service instance

## Decision

❗The change that we're proposing or have agreed to implement.

* Our app gets deployed to three environments for three main use cases:
    * Staging - Used by the product team. Highly volatile: features and data may show up, change or disappear at any time.
    * UAT - Used by anyone "trying out our app". Behaves like production, but with a safety net: nothing in here becomes visible to the public.
    * Production (or "prod") - Used by documentalists, only. This is the product our end users in the documentation offices are using to perform their duties.

* Each of these purposes comes with its own sets of rules that are described below.

### Rules and Purposes

_Note: The following is how we aim to set up and use the environments. It does not mean that all the aspects do exist already. E.g. our description of how repeated migrations should behave does __not__ imply that there are repeated migrations already._

* Staging:
    * Purpose
        * Supporting the product development team's work.
        * Use cases include: UX or functional or infrastructure experiments, half-done features

    * Access
        * The product team (primarily)
        * DigitalService staff
        * Testers
        * Documentalists
        * Other stakeholders

    * Features
        * Anything that's done
        * Anything that's under development

    * Uptime
        * Staging may be down any time. 
        * Must not be used for product demos.

    * Data 
        * Staging data may come into existence, disappear or be changed any time.

    * Repeated migrations
        * Migrations may add, remove or change the documents in staging at any time.

* UAT
    * Purpose
        * Have a production-like environment, but with a safety net: nothing in here becomes visible to the public.
        * Use cases include: product demos, training sessions, user research, manual tests

    * Access
        * The product team
        * DigitalService staff
        * Testers
        * Documentalists
        * Other stakeholders

    * Features
        * Anything that's "done" (i.e. implemented and has passed tech and functional reviews)

    * Uptime
        * UAT is expected to be up "all the time" with the following caveats:
            * We may announce downtimes.
            * We have no on call service, so it's reasonable to expect it to be up between 8:00 and 17:00 on working days.

    * Data
        * Data in UAT is expected to persist and only be created, updated or deleted on user interaction.

    * Repeated migration
        * Migrations may add, remove or change the documents in staging at any time.

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


