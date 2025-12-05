# 8. Duplicating native documents' data

Date: 2025-12-❓❓❓
Previous version: 2025-06-11

## Status

Proposal (❓❓❓ should become: Accepted)

## Context

- We store literature documents in their "native" formats of consumption, which is JSON (for frontend document state) and XML (for end-user documents).
- We do not migrate the documents into a relational schema. This reduces complexity and effort.
- We need to handle use cases that include queries and pagination over the data inside the JSON or XML and we want these queries to be handled _by the database_ and not in code.
- Instead of switching to specialized JSON or XML based (document) databases, we want to stick to the default database of NeuRIS which is PostgreSQL where querying over these structures is not trivial (cf. [PostgreSQL cannot create an index on the XML type](https://www.postgresql.org/docs/current/datatype-xml.html#DATATYPE-XML-ACCESSING-XML-VALUES)). The reason here is reduction in complexity within NeuRIS.

We have investigated multiple [approaches](https://digitalservicebund.atlassian.net/wiki/x/VYArcQ) (linked document is not public) to tackle this situation and here's what we have decided.

## Decision

We'll accept data duplication and add fields to our tables that make _some_ information of the native documents accessible in relational database fields and data types. E.g. in order to quickly search documents by their document ID - currently stored in JSON or XML - we may introduce a `documentId` field of `VARCHAR` type in a relational database table.

Some of these pieces of data may be stored "alongside" the native documents and some may be stored in separate tables.

## Consequences

We solve our initial problem:

- This setup supports all our requirements: We can search, filter and paginate right on the database. In data structures that the database is optimized for.
- Sample use cases are:
  - Searching for documents that match certain criteria of an active reference.
  - Providing a paginated list of documents as a point of entry for the user who can filter the list by multiple criteria.

We add complexity when it comes to data handling:

- Data duplication: Data of the native documents is "copied" into relational database fields.
- Data synchronization: We need to assure that the relational database fields stays in sync with the native documents' data. <br>Our current approach is
  - To have a scheduled job that fills in missing relational database fields if required (e.g. for documents that were not created through our app, but through migrating existing documents.)
  - To fill in / update the missing relational database fields when documents are created or edited through our app.
