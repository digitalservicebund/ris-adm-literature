# 4. Data Persistence Supporting the Documentation Workflow

Date: 2025-02-❓❓

## Status

❓

## Context

When documenting administrative directives, information about the documents flows

- from the user's browser
- to our web application's backend (database) and from there
- to the portal

This, however, does not tell us in what data formats the data will be persisted and exchanged.

## Decision

- Data formats:
  - In the frontend, the documents are stored as JSON objects.
      - Data exchange between frontend and backend happens in JSON.
  - In the backend, the documents are stored
    - as JSON before they are published and
    - as XML/LDML after they have been published.
  - To the portal, the documents are sent as XML/LDML.
  - In all three places, there will be additional data in order to support various use cases around these documents.
- We will exchange complete documents between frontend and backend.
- The backend is understood as the single point of truth.

## Consequences

- All data that's manipulated in the frontend is in JSON format.

  - The frontend does not interpret or transform any XML.
  - This means we're staying with data types that the frontend stack is familiar with, which simplifies development (no XML libraries, no XML tooling, no XML concepts).
  - This does not rule out that users may get the ability to upload XML in order to pass it on to the backend for handling.

- The API endpoints will talk JSON exclusively

  - Again, this simplifies matters and matches the modern web stack
  - It does, however, require extra effort if XML _should_ need to be handled in the frontend

- The API endpoints will exchange complete documents

  - This is to avoid complexity.
  - We do not expect the size of the data exchanges to have a significant impact on the user.

- Transformations between JSON and XML/LDML will take place in the backend.

  - JSON will be transformed into XML/LDML on publishing a document.
    - This is where XML/LDML validation will take place.
    - Only valid XML/LDLM will be stored in the database.
  - XML/LDML will be transformed into JSON on loading a published document.
  - Manipulation of XML/LDML will also exclusively take place in the backend.

- While the backend will send XML/LDML documents to the portal, the backend's database stays the single source of truth and is part of the NeuRIS backup plan.

--- old text ---

## Context

When new documentation units (DU) are created, edited and published, several possibilities exist how to handle persistence. In the [Frontend Stack ADR (#2)](https://github.com/digitalservicebund/ris-adm-vwv/blob/main/doc/adr/0002-frontend-stack.md) we have decided to use Pinia to store state in the frontend (FE). But how to keep the state in sync between the FE and the backend (BE) including when the DU reaches the state where it's being pushed to the S3 bucket of the Portal (DE: "Abgabe")?

These are the main questions:

1. Do we generate the DU number (example KSNR054920707) in the FE or BE?
1. Do we generate the DU number when a new, empty DU is being created or only when the process is finalized (DE: "Abgabe")?
1. Will we have BE API endpoints for individual form elements or one endpoint for syncing the DU as a whole?
1. Do we transform the form state to XML/LDML
   1. in the FE, or
   1. when data arrives at a BE API endpoint, or
   1. only when it's eventually published?
1. How do we fetch successfully submitted DUs from the BE?
1. Do we keep the XML/LDML in the database when we publish the document to the portal?

## Decision

With respect to the questions above, we have decided to go with the following setup:

1. We generate the DU number on the BE. It will _not_ follow the numbering schema that the BSG is using at the moment, where the last four digits represent employees that shall take care of that DU (example KSNR054920707).
1. We generate the DU number when the DU is created and write it to the database right away (the DU will be in draft state).
1. There will be only one endpoint where the whole document is stored.
1. We will keep the data in JSON format as long as the editing process is still ongoing. We'll do the transformation to XML/LDML once the DU is finally submitted (DE: "Abgabe").
   1. Once we successfully transform the document to XML/LDML, it will become the point of reference.
   1. The FE will deal with JSON only. The BE is responsible for transforming JSON into XML/LDML and vice-versa.
1. When a published DU is re-opened, the XML/LDML will be transformed to JSON on the BE in a way that it can be used in the Pinia store on the FE.
1. We keep the XML/LDML for all DUs in the database as it remains the single source of truth.

## Consequences

With respect to the decisions above, here are the consequences and our reasoning behind:

1. We provide one API endpoint to create new DUs. It will anser with the newly created DU number which uniquely identifies it.
   - The information of the next free sequential number is only known to the BE.
1. We create a database entry even if no form element is filled.
   - This behavior aligns with other NeuRIS components (e.g. Case-Law).
1. We provide one API endpoint where the whole document will be pushed to.
   - This greatly simplifies the models: There's one used for unpublished documents (sent and received by FE and BE) and one for published documents. None for partial upgrades.
   - We assume that the amount of data will not make a noticeable difference to the (~10) users.
   - We are the only consumers of our API, therefore we can tailor it to our very specific needs.
1. Only valid, schema validated XML/LDML data is stored in the database.
   - This way we do not need to keep additional meta information about the state of the XML/LDML.
   - Form data validation becomes XML schema validation. Schema errors will need to be transferred to the FE and made understandable as form errors there.
   - The FE is kept clear of any XML or XML schema.
1. We provide an API endpoint where the whole document can be fetched from in JSON.
1. The relation between BE and the portal becomes very simple: The BE only pushes to the portal and never pulls.
   - There is a NeuRIS agreement that in case of disaster, we need to be able to replicate the portal from the database backups.
