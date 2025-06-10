# 8. Index Tabe for Document Units

🚧 Date: 202x-xx-xx

## Status

🚧 Draft (should become: Accepted)

## Context

🚧 The issue motivating this decision, and any context that influences or constrains the decision.

We're storing our documents in their "native" formats, which is JSON (for frontend document state) and XML (for published documents).
* We do not migrate the documents into a relational schema.
* For some use cases we have introduced additional fields in the relational schema for easier look-up (e.g. the `document number`).
    * These introduce duplication of data that we think is warranted and we did not run into issues, so far.

The start page's new feature of "filtering" the documents that are shown poses a new use case with these specific requirements:
1. Extract certain data (Langüberschrift, Zitierdatum, Fundstelle, Normgeber) from the two document formats in order to show them in a result list on the start page.
2. Filter over the extracted data (e.g. show only those documents where Langüberschrift contains a certain keyword)
3. Provide pagination

Again the question is: how far do we stay with the "native" formats and how far do we go for new fields and accept the duplication of data?

We have investigated multiple [approaches](https://digitalservicebund.atlassian.net/wiki/x/VYArcQ) (linked document not public) and here's what we have decided.

## Decision

🚧 The change that we're proposing or have agreed to implement.

In parallel to the schema that contains the "native" documents, we're introducing a new index table.

The index table consists of exactly the fields we need for supporting the use case and is filled with data from the "native" documents.

Neither the filtering, nor the pagination is done in backend code. It's all taking place on the database.

## Consequences

🚧 What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

* The index table supports all our requirements:
  * We can read the required data from the index table.
  * We can filter on the index table's fields.
  * We can paginate results.

* Our queries on the index table are fast enough for good UX given our domain, use case and amount of data.

* Our backend code stays "stateless" wrt. the document units. It's mainly translating requests into database lookups which include all of retrieval, filtering and pagination on the database side.

* We have introduced duplication: data is available in the "native" documents as well as in the new fields.
    * We need to make sure that the index table stays in sync with the "native" documents' data.
    * This is relevant whenever the native documents are changed.
    * In our app this is the case
      * when a user "saves" a document and 
      * when a document will get published (not supported, yet)

* For imported documents, we have a scheduled job that fills in the index, if it's missing for a document in the document unit table.

* Wrt. the list on the start page, the index table is the point of reference.

* Wrt. displaying, editing, saving and publishing the individual documents, the document unit table stays the point of reference.