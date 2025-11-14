# 12. Environments (staging, UAT, production)

Date: ❗2025-11-xx

## Status

Proposed ❗ (should become "accepted")

## Context

Due to stakeholder needs we will introduce two new environments in which our application will be running.

The term "environment" is meant to indicate that while each of them runs all components of our application (details below), they share none of them. Any component may behave differently in each environment.

Our applicaction's components are:
* The `ris-adm-literature` application code running in two services (frontend, backend)
* Data persistence via the NeuRIS database
* Several S3 compatible buckets for publishing documents (exchanging data with the portal)
* A 3rd party authentication service instance

## Decision

* Our app gets deployed to three environments for three main use cases:
    * Staging - Used by the product team. Highly volatile: features and data may show up, change or disappear at any time.
    * UAT (user acceptance testing) - Used by anyone "trying out our app". Behaves like production, but nothing in here becomes visible to the public.
    * Production (or "prod") - Used by documentalists, only. This is the product our end users in the documentation offices are using to perform their duties.

* Each of these purposes comes with its own sets of rules that are described below.

### Rules and Purposes

_Note: The following is how we aim to set up and use the environments. It does not mean that all the aspects do exist already. E.g. our description of how repeated migrations should behave does __not__ imply that there are repeated migrations already._

* Staging:
    * Purpose
        * Supporting the product development team's work.
        * Use cases may include: UX or functional or infrastructure experiments, half-done features

    * Access
        * The product team (primarily)
        * DigitalService staff
        * Testers
        * Documentalists
        * Other stakeholders

    * Features
        * Anything that's done
        * Some things that are under development

    * Stabilitiy
        * Staging may be down any time. 
        * Must not be used for product demos.

    * Data 
        * Staging data may come into existence, disappear or be changed any time.

    * Repeated migrations
        * Migrations may add, remove or change imported documents in staging at any time.

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

    * Stability
        * We aim at keeping UAT stable, so it can be a reliable tool for the given use cases.

    * Data
        * Data in UAT is expected to persist and only be created, updated or deleted by the users (or migration, cf. below).

    * Repeated migration
        * Migrations may add, remove or change imported documents in UAT at any time.

* Production:
    * Primary goal
        * Production is used by documentalists for performing their documentation duties.
        * What's published is made available to end users of the NeuRIS portal.

    * Access
        * Documentalists, only

    * Features
        * Anything that's "done" (i.e implemented and has passed tech and functional reviews)

    * Stability
        * Keeping production stable so it can be a reliable tool for the documentalists is important to us.

    * Data
        * Data in production is expected to persist and only be created, updated or deleted by the users (or by migration, cf. below).

    * Repeated migration
        * Migrations may add, remove or change imported documents in production at any time.
        * This will change once we have replaced the legacy system.


## Consequences

What we think will become more easy:

* Supporting the needs of various user groups besides documentalists (product team, DigitalService staff, testers, other stakeholders)

* Managing risks wrt. data integrity and access controls.

What we expect to become more difficult:

* Operating three environments.
* Release may become a more involved process (with or without feature flags)


