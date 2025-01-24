# 4. Frontend to Backend interaction

Date: 2025-01-24

## Status

Proposed

## Context

When new documentation units (DU) are created, edited and saved, several possibilities exist how to handle the state of form elements. In ADR 0002 we have decided to use Pinia to store state in the frontend (FE). But how do we keep the state in sync between the FE and the backend (BE) in a multi user environment where different actors work on the same DU until it reaches its desired state, where it's finally being pushed to the S3 bucket of the Portal?

Possibilities/Challenges are:

1. Generate DU number (example KSNR054920707) in the FE or BE?
1. Generate DU number when a new, empty DU is being created or when the process is finalized (Abgabe)?
1. Have BE API endpoints for every form element or one endpoint for the whole DU form state, where the Pinia state is being synced with?
1. Transform the form state to XML/LDML right away when data arrives at an endpoint or keep state somehow in an in-between/in-process format where it can be altered and send back to the FE very easily?
1. Transform form inputs into XML/LDML right after input?
1. Work with the database schema from migration or generate a new schema?

## Decision

We have decided to go with the following setup:

1. The DU number will be generated on the BE side.
1. The DU number is being generated when the DU is created and will be written to the database right away. Even if no save-action is permitted, there is a DU in draft state for later.
1. We'll have one endpoint where the whole Pinia state of all form elements combined is being pushed to in JSON format.
1. We'll keep the data in JSON format as long as the process is still ongoing without transforming it. The transformation to XML/LDML is done when the DU is finally submitted (Abgabe).
   1. Transforming the form state from JSON to XML/LDML means, that the requirements for are finally examined. As there is an actual XSD schema already existing from the migration project, the transformed information only needs to get schema validated.
   1. When the JSON gets transformed successfully, the JSON data is being eliminated.
1. The FE will not deal with XML/LDML.
1. We'll setup a new database schema in our to be defined BE.

## Consequences

The reasoning behind those decisions:

1. We want to keep the numbering schema that the BSG is using atm, where the last two digits represent an employee. Such information and what number is still free would be needed to fetch anyhow and therefore the whole process cannot work without the BE. Therefore we move the whole block to the backend.
1. To align with NeuRIS behavior (Case-Law).
1. The reasons and consequences are:
   1. It would be much more work to create models on the BE for each form-group and we would enter a route where an ER-Model representation of all form elements is being generated, which we wanted to get around in the first place, by defining LDML as first-class-citizen.
   1. Anyhow that comes with a price of much more data being transferred as actually needed. But we think that this is negligible in a sense that not many (1-10) users are interacting with the BE at the same time and therefore we do not see performance bottlenecks.
   1. The TBD BE API is only being accessed from this very FE. It's not public and no other application is supposed to access it. Therefore the API has only one purpose to make our own FE work. In other words: It's meaningless to server a fine grained API.
1. This approach allows us to only hold valid, schema validated XML/LDML in our database. When old DU are re-opened, the XML/LDML needs to be re-transformed to JSON, in a way that the Pinia store can read it.
1. The FE is kept clear of any XML domain knowledge.
1. The migration project had already defined a database schema and owns it. As this is a finite process and sharing schemas across multiple projects is difficult, we are opting to generate a new schema with all desires we have. Otherwise we would need to alter the migration schema to allow inserting new rows that have not been migrated. That leaves us with the todo to move the migrated data into our to be defined BE schema, when the migration is done. As this is planed as a big bang, we do not see any problem: At a certain date the BSG employees will stop creating DUs in der old environment. The data from the legacy system will be migrated just once. When this runs through, the user will continue in NeuRIS.
