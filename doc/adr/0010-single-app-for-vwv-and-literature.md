🚧 TODO: Resolve all construction markers/comments and remove them before merging to `main`

# 10. Template

🚧 Update date once we reach "Accepted"

Date: 2025-08-13

## Status

🚧 This should become "Accepted" before merging to `main`

Draft 

## Context

🚧 The issue motivating this decision, and any context that influences or constrains the decision.

Our team has built the BSG app for handling (creating/editing/publishing) administrative directives of the BSG (Bundessozialgericht).

Now we are tasked with handling more documents called "Literatur", which come with two new document types called "selbständige Literatur" and "unselbständige Literatur".

Therefore we face the question whether to
 * handle the new document types and user flows in their own app (or apps) or 
 * to stay with one web app handling multiple document types and user flows.

We looked at the following aspects with a focus on benefits (in terms of re-use) or drawbacks (mainly: complexity) when going for a single app:

- UX, backend, infrastructure for literature: mostly similar to the BSG app
  - CI/CD 
    - Workflows: can be kept (security, tests, deployment, documentation)
    - Configuration (branch rules, secrets): can be kept
  - Authentication
    - Authentication provider (bare.id) and flow: stay the same
    - Roles and access management: will be extended to handle multiple roles
  - Infrastructure
    - Persistence via NeuRIS database: stays the same
      - Documents stored schema-less: stays the same
    - App instances:  stay the same
    - Buckets user for publishing: will need separate buckets per document type
  - Monitoring: stays the same
  - Tech stack: stays the same
    - Code patterns: can stay the same
  - Frontend architecture: will mainly be extended
  - Backend / endpoints: 
    - Number of endpoints: stays the same for document handling (maybe we need to introduce some more wrt. lookup tables)
    - Implementation: additional effort for each document type (for the tranformation between JSON and XML/LDML)
- System load:
  - Number of documents: 10x higher (~500k)
  - Number of users: same ballpark (no longer ~10, but rather 50 than 100)
- Domain knowledge:
  - Domains do not differ that much in terms of items or processes: some additional complexity

We also considered that
- we're a single team of four engineers and need to implement and maintain any solution(s) for some time.
  - multiple repositories come with their own costs in terms of effort/work required to keep them in good state
- in general, separating apps is less risky than merging them later.

## Decision

🚧 The change that we're proposing or have agreed to implement.

We handle the "Literatur" documents in the same app we built for the BSG's administrative directives.

Each document type gets its own flow in the frontend.

We stay with the existing database schemas.

## Consequences

🚧 What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

We expect the following benefits:
- High delivery velocity:
  - implementation speed through reuse of existing patterns, components and tools (cf. the list in the "Context" section)
  - almost no time/effort required for setting up our tools until we can start delivering
- Moderate growth in maintainance costs

We accept the following risks:
- Higher reliance on well structured code and other engineering best practices
- The higher number of documents will require adjustments on our workflows, architecture, tooling in ways we do not know in detail, yet.
- The new business domains do come with additional complexity that we as engineers will need to handle in oder to support the whole team.
