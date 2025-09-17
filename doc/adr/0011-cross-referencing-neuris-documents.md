# 11. Cross-referencing neuris documents

Date: 2025-09-17

## Status

Accepted

## Context

NeuRIS must support cross-references between documents of heterogeneous types (e.g., caselaw, administrative directives) that are owned by different teams and reside in separate schemas for domain isolation.

Two reference directions must be represented:

- **Active References (Aktivzitierung)**: Explicit links created by a source document to a target.
- **Passive References (Passivzitierung)**: Implicit links indicating that a document is the target of another source's reference.

These references are included in the LDML documents published on the portal and the streams should control what references to include.

## Decision

- To represent references (in both directions, possibly across domains), a central reference table will be introduced: `ris_references` in a shared schema with read and write access for each stream.
- `ris-references` will hold the identifiers plus some meta-data on the references for any documents that are linked by references.
- Each stream-specific schema will maintain its own documents table for storing its document data, and will have read-only access to other streams documents tables. Streams do not reference foreign documents in their stream schema directly.
- For local development and CI/CD, the Docker initialization process will create:
  - The shared schema with the `ris_references` table.
  - All domain-specific schemas and their documents tables with seed data.

## Consequences

### Benefits:
- The central reference table acts as the source of truth for all references and can be accessed by all streams.
- Referential integrity is preserved by the shared schema.
- Domain isolation is preserved: Document ownership stays within its respective schema.
- Simplified local testing: CI/CD can spin up all schemas locally without needing remote services.

### Risks:
- Concurrency issues: Multiple streams writing to `ris_references` could create race conditions without proper locking or transactional handling.
- Access control complexity: Granting read/write to multiple teams may increase the risk of misconfiguration or accidental modification.
