# 10. Single app for administrative directives and literature

Date: 2025-08-13

## Status

Accepted 

## Context

Our team has built the BSG app for handling (creating/editing/publishing) administrative directives of the BSG (Bundessozialgericht).

We are now tasked with supporting an additional document category: "Literatur", which includes two new document types:
  - selbständige Literatur (SLI)
  - unselbständige Literatur (ULI)

Because administrative directives and literature are two different business domains, they should be separated in the database, meaning two data sources and different database users to ensure strict isolation.

This raises the architectural question of whether to:
- Build a separate app (or apps) dedicated to these new document types, or
- Extend the existing BSG app to handle multiple document types and user flows.

### Considerations

We looked at the following aspects with a focus on benefits (in terms of re-use) or drawbacks (mainly: complexity) when going for a single app:

- **User Experience, Backend & Infrastructure**
  - CI/CD 
    - Workflows: unchanged (security, tests, deployment, documentation)
    - Configuration: unchanged (branch rules, secrets)
  - Authentication
    - Authentication provider (bare.id) and login flow: unchanged
    - Roles and access management: extended for additional roles
  - Infrastructure
    - Persistence via NeuRIS database:
      - Literature and administrative directives stored in separate schemas (different domains)
      - Each schema with its own database user
      - Documents stored schema-less: unchanged
    - Application instances: unchanged
    - Publishing: requires separate buckets per document type
  - Monitoring: unchanged
  - Tech stack: unchanged
  - Code patterns: reusable
  - Frontend: extended with new flows
  - Backend / APIs:
    - Multiple datasources, requiring two transaction managers
    - Dedicated routing layers per domain, secured with Spring Security
    - Additional effort for transformations (JSON ↔ XML/LDML)
- **System Load**
  - Number of documents: 10x higher (~500k)
  - Number of users: same ballpark (from ~10 to 50–100)
- **Domain Knowledge**
  - Domains are similar in items and processes, but introduce some additional complexity
- **Team & Maintenance**
  - We are a single team of four engineers responsible for both implementation and long-term maintenance
  - Multiple repositories would add overhead for setup, maintenance, and alignment
  - In general, separating apps is less risky than merging them later. There is a significant overlap of features and there is still some unknown about the system boundaries so splitting too early could potentially add some overhead cost.

## Decision

We will **integrate the Literatur documents into the existing BSG app**.
- Each document type will have its own frontend and backend flow.
- We will use separate database schemas per domain with their own users to ensure data isolation

## Consequences

### Benefits:
- **Higher delivery velocity**
  - Rapid implementation through reuse of existing patterns, components, and infrastructure
  - No overhead from setting up a new app and its tooling
- **Moderate maintenance growth**
  - One app to maintain, with slightly increased complexity
- **Data isolation in the database layer**
  - The business domain separation is guaranteed through the two different schemas and database users

### Risks:
- Increased reliance on well-structured code and engineering best practices
- The need for separation rules and architecture tests to ensure them
- Significant growth in the number of documents will require scaling workflows, architecture, and tooling in ways not yet fully known
- Supporting multiple domains introduces additional complexity for the engineering team
