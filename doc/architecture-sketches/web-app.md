<!-- Additional Macros (based on context diagram macros):
    Container(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link, ?baseShape)
    ContainerDb(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link)
    ContainerQueue(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link)
    Container_Ext(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link, ?baseShape)
    ContainerDb_Ext(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link)
    ContainerQueue_Ext(alias, label, ?techn, ?descr, ?sprite, ?tags, ?link)
    Container_Boundary(alias, label, ?tags, ?link)
 -->

```mermaid
C4Container
    title RIS-VwV, Container Diagram 🚧 Work in Progress! 🚧

    Person_Ext("documentalist", "BSG Documentalist", "BSG staff member, various roles")

    System_Boundary("ris-vwv", "RIS-VwV"){
        Container("frontend", "Single Page Web App", "Vue, TypeScript", "Provides all RIS-VwV functionality to <br>users via their web browser")
        Rel("documentalist", "frontend", "Creates and adjusts VwVs")

        Container("backend", "API Server", "Java, Spring Boot", "Provides functionality via a HTTPS/JSON API")
        Rel("frontend", "backend", "Makes API calls to", "HTTPS/JSON")

        ContainerDb("relationalDB", "Relational DB", "PostgreSQL", "Stores process data")
        Rel("backend", "relationalDB", "Reads from and writes to", "SQL/TCP")

        ContainerDb("sessionStorage", "Session Storage", "Redis", "Stores user session information")
        Rel("backend", "sessionStorage", "reads/writes  session information", "HTTPS")
    }

    Container_Ext("portal", "Portal", "Gives the public access to norms")
    Rel("backend", "portal", "Makes new norms accessible to the portal")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="2")
```
