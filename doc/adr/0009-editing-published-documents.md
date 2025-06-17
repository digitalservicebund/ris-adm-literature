# 9. Editing Published Documents

🚧 Date: 202x-xx-xx

## Status

🚧 Proposed (Accepted)

## Context

🚧 The issue motivating this decision, and any context that influences or constrains the decision.

Here's the mental model our application should support with respect to editing published documents:
* Every document exists only once.
* That document is either public or not.
* The document can be edited in either state.
* In case the document is public, the changes are also public (maybe with a technical delay)
* The user decides when their change is applied to the document.

The question is: how do we map these requirements to our backend architecture?

## Decision

🚧 The change that we're proposing or have agreed to implement.

We follow the mental model closely:

* The document is either stored as JSON or as XML. Never both.
    * If it's public, there's only the XML (as published to the portal)
    * If it's not public, then there's only the JSON (representing the frontend state)
    
For editing, this means:
    
* If the document is public:
    * When the document is opened, we will read the XML and create and serve JSON to the frontend _on-the-fly_.   
        * The JSON does not get stored.
    * When the document is changed, the changed document is saved in XML. -
        * No JSON gets stored.
* If the document is not public:
    * On opening, we serve the JSON as stored
    * On editing, the changed document is stored as JSON.

For publishing, this means:

* We store the document as XML
* We delete the stored JSON 


## Consequences

🚧 What becomes easier or more difficult to do and any risks introduced by the change that will need to be mitigated.

* There is only ever one document in our database: either in JSON or in XML format.
    * If it's the XML, the document is public.
    * If it's the JSON, the document is not public.
* When updating a document, there is no way to "store" the intermediate state across browser sessions or reloads.
    * The user needs to confirm the update which then is public or not, depending on the document's state (of public or not).