# 5. Dealing With Historic Administrative Directives

Date: 2025-01-31

## Status

Work In Progress

## Context

1. The purpose of our web app is to support the handling of administrative directives (ADMs) which means creating new ADMs and: editing/updating existing ADMs

1. All the _currently existing_ ADMs are stored in management systems and data formats we strive to replace with our webapp, so we need to decide in which way we want to make these existing documents available in our app.

1. There exists is a CLI tool called [`ris-vwv-migration`](https://github.com/digitalservicebund/ris-vwv-migration) that

   - reads documents in the legacy format and
   - writes its results to a database, most importantly:
     - the legacy (input) document
     - details about the migration process (e.g. errors)
     - the migrated (output) documents in the format we want (XML/LDML)
   - writes the migrated documents as files to disk

1. Both projects use PostgreSQL databases for persistence.

## Decision

1. Data separation:
   1. We use separate places (e.g. separate databases or PostgreSQL schemas) for storing the data of our web app and the data of the migration tool
   1. There is no relation between these places on the level of tables/relations
1. A single import:
   1. Exactly once we import the migrated XML/LDML documents - but no other data from the migration - into our web app's data.
   1. We import these documents into the same place where our web app stores newly published documents.
   1. After the import, the migrated documents are handled exactly like newly published documents.

## Consequences

- Our web app can be built with a clear focus on _its_ needs, models and restrictions. There is no need to take legacy data into account, neither during development nor when using the web app.

- Our web app has no knowledge about the imported documents' histories.

- While separated conceptually and in terms of data persistence, the migration data is available as a reference should the need arise. The web app's database backups do ensure it does not get lost even if we may lose access to the original documents.
