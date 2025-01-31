# 4. Data Persistence Supporting the Documentation Workflow

Date: 2025-01-31

## Status

Accepted

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
