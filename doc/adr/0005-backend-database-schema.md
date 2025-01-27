# 5. Backend Database Schema

Date: 2025-01-27

## Status

Work In Progress

## Context

1. Work with the database schema from migration or generate a new schema?

## Decision

1. We'll setup a new database schema in our to be defined BE. The migrated cases from the migration project will be migrated to this schema when final.

## Consequences

1. The migration project had already defined a database schema and owns it. As this is a finite process and sharing schemas across multiple projects is difficult, we are opting to generate a new schema that satisfies the FE requirements (like user workflow management). Otherwise we would need to alter the migration schema to allow inserting new rows that have not been migrated. That leaves us with the todo to move the migrated data into our to be defined BE schema, when the migration is done. As this is planed as a big bang, we do not see any problem: At a certain date the BSG employees will stop creating DUs in der old environment. The data from the legacy system will be migrated just once. When this runs through, the users will continue in NeuRIS.
