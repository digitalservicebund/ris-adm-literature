# 9. Editing Published Documents

🚧 Date: 202x-xx-xx

## Status

🚧 Proposed (Accepted)

## Context

🚧 The issue motivating this decision, and any context that influences or constrains the decision.

Here's the mental model our application should support with respect to editing published documents:

- Every document exists only once.
- That document is either public or not.
- The document can be edited in either state.
- In case the document is public, the changes are also public (maybe with a technical delay)
- The user decides when their change is applied to the document.

Another constraint is more technical in nature:

- We currenty store frontend state as JSON
- We currently store published document state as XML
- The XML contains data that the JSON does not.
  - Translating from XML to JSON looses data.

The question is: how do we map these requirements to our backend architecture?

## Decision

🚧 The change that we're proposing or have agreed to implement.

- We stay with the current architecture:
  - JSON for frontend state,
  - XML for published document state.
- We do not allow the JSON and XML to diverge (where they overlap)
- We stay with the requirement that all stored XML is always valid wrt. our schema.

- We do not allow the JSON and XML to diverge (where they overlap) and here's how we do it:
  - When a document is not published
    - there's only the JSON
    - if it's edited in the frontend and saved, the JSON gets updated
  - When a document is published
    - we have both, the JSON and the XML
      - the shared data between those is in sync
    - if it's edited in the frontend and the user tries to re-publish, we validate the data and
      - if it's invalid wrt. our schema
        - we reject the re-publishing
        - neither JSON nor XML are changed
      - if it's valid wrt. our schema
        - we update both the JSON andthe XML

## Consequences

🚧 What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

- We accept
  - the duplication of data between JSON and XML
  - the inherently higher complexity in code

- in favor of

  - keeping the benefits of the existing architecture (cf. [doc/0004-data-persistence-and-exchange](./0004-data-persistence-and-exchange.md))
  - keeping refactoring low wrt. the existing solution
  - keeping the requirement in place that any XML in our database is valid wrt. our schema

- The use case of "un-publishing" or "retracting" documents is not covered, yet, as it's no requirement so far.
