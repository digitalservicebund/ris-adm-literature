# 0. Template

Date: 2025-08-13

## Status

Draft

## Context

The issue motivating this decision, and any context that influences or constrains the decision.

- we want to create, edit and publish literature documents
- the UX, backend, infrastructure for literature will be relatively similar to the BSG app
  - CI/CD
  - Authentication
    - Authentication flow and provider similar
    - Roles and access management will differ
  - Infrastructure
    - Persistence stays the same
    - App instances stay the same
    - Buckets for publishing will differ
  - Monitoring
  - Tech stack
  - Frontend architecture will be mainly extended
- one team must implement and maintain
- documents are stored schema-less
- existing patterns can be reused
- the different types of documents are only required for the transformation between json and xml
- we dont expect new endpoints besides lookup tables
- we will have to deal with 10x more documents (500k)
- the amount of users will stay in the same ballpark
- business domains and processes dont differ too much
- separating apps is less risky than merging them later


## Decision

The change that we're proposing or have agreed to implement.

- we will handle the new literature documents in the same app where we handle vwv documents
- each document type gets its own flow in the FE
- we will stay with the existing database schemas

## Consequences

What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

- Faster implementation, reuse of existing patterns, components and tools
- Less setup time
- Simple maintenance

- Higher reliance on well structured code (and other engineering practices)
- Higher Number of documents may require adjustments on our workflows, architecture, tooling
- Developers need to handle more complexity with the different business domains
