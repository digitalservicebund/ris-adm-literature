# 4. Frontend to Backend Interaction

Date: 2025-01-24

## Status

Proposed

## Context

When new documentation units (DU) are created, edited and saved, several possibilities exist how to handle the state of form elements. In ADR 0002 we have decided to use Pinia to store state in the frontend (FE). But how do we keep the state in sync between the FE and the backend (BE) until the DU reaches its desired state (DE: "Abgabe"), where it's finally being pushed to the S3 bucket of the Portal?

Main questions:

1. Generate DU number (example KSNR054920707) in the FE or BE?
1. Generate DU number when a new, empty DU is being created or when the process is finalized (DE: "Abgabe")?
1. Will we have BE API endpoints for every form element or one endpoint for syncing the whole DU form (pinia) state?
1. Transform the form state to XML/LDML
   1. in the FE, or
   1. when data arrives at a BE API endpoint, or
   1. when it's published?
1. How do we fetch finally submitted DUs?
1. Do we keep the XML/LDML in the database when we push to the portal s3 bucket?

## Decision

We have decided to go with the following setup:

1. The DU number will be generated on the BE side.
1. The DU number is being generated when the DU is created and will be written to the database right away (DU will be in draft state).
1. There will be only one endpoint where the whole Pinia state for all form elements combined is being pushed to in JSON format.
1. We will keep the data in JSON format as long as the editing process is still ongoing without transforming it. The transformation to XML/LDML will be done when the DU is finally submitted (DE: "Abgabe").
   1. Once we successfully transform the JSON to XML/LDML, we will delete the JSON data from the database.
   1. The FE will deal with JSON only. The BE is responsible for transforming JSON into XML/LDML and vice-versa.
1. When a finally submitted DU is re-opened, the XML/LDML will be re-transformed to JSON on the BE, in a way that the Pinia store can read it in the FE.
1. We keep the XML/LDML for all DUs in the database as it remains the single source of truth.

## Consequences

First we will state the consequences while appending the reasoning for the decisions right underneath each ordered list item:

1. We provide one API endpoint to create new DUs. It will anser with the newly created DU number which uniquely identifies it.
   - The information of the next free sequential number is only known to the BE. We will **not** keep the numbering schema that the BSG is using at the moment, where the last four digits represent employees that shall take care of that DU (example KSNR054920707).
1. We create a database entry even if no form element is filled.
   - To align with NeuRIS behavior (Case-Law).
1. We provide one API endpoint where the whole Pinia state for all form elements combined will be pushed to, which accepts JSON format.
   - It would be much more work to create models on the BE for each form-group and we would enter a route where an ER-Model representation of all form elements is being generated, which we wanted to get around in the first place, by defining LDML as first-class-citizen.
   - Anyhow that comes with a price of much more data being transferred as actually needed. But we think that this is negligible in a sense that not many (1-10) users are interacting with the BE at the same time and therefore we do not see performance bottlenecks.
   - The to-be-defined BE API is only being accessed from this very FE. It's not public and no other application is supposed to access it. Therefore the API has only one purpose to make our own FE work. It's not a requirement to server a fine grained API.
1. Only valid, schema validated XML/LDML data is stored in the database.
   - This way we do not need to keep additional meta information about the state of the XML/LDML.
   - When transforming the form state from JSON to XML/LDML, all requirements to all input form-fields are finally examined. As there is an actual XSD schema already existing (from the migration project), the transformed document only needs to get schema validated. Which will result in errors, when required fields are empty or filled with wrongly formatted data.
   - The FE is kept clear of any XML domain knowledge.
1. We provide one API endpoint where the whole Pinia state for all form elements combined can be derived from, which will respond in JSON.
1. We will only push to the S3 bucket. We will never pull data from that S3 bucket.
   - We need to be able to replicate the portal from the database as only the database is being planned to have regular backups (NeuRIS wide strategy). The S3 buckets can not be trusted in that sense.
