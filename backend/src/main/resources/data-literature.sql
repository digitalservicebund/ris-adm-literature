-- Insert 100 SLI documentation unit for testing. This file is only used in Spring Boot Profile "default".
-- This inserts 100 documentation units for e2e tests to check on the search pagination
WITH params AS (
    SELECT
        s.running_number,
        'KALS' || s.running_number::text AS final_doc_number
    FROM generate_series(999999999, 999999899, -1) AS s(running_number)
),
created AS (
    INSERT INTO literature.documentation_unit (id, document_number, xml, documentation_unit_type, documentation_office)
    SELECT
        gen_random_uuid(),
        p.final_doc_number,
        format(
            $$<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
                xmlns:ris="http://ldml.neuris.de/literature/selbstaendig/meta/">
  <akn:doc name="offene-struktur">
    <akn:meta>
      <akn:identification source="attributsemantik-noch-undefiniert">
        <akn:FRBRWork>
          <akn:FRBRthis value="TODO"/>
          <akn:FRBRuri value="TODO"/>
          <akn:FRBRalias name="Dokumentnummer" value="%s"/>
          <akn:FRBRalias name="haupttitel" value="Lexikon Soziologie und Sozialtheorie"/>
          <akn:FRBRalias name="haupttitelZusatz" value="Hundert Grundbegriffe"/>
          <akn:FRBRdate date="2019-04-30" name="Erfassungsdatum" ris:domainTerm="Erfassungsdatum"/>
          <akn:FRBRauthor as="#verfasser" href="#xx-verfasser-1"/>
          <akn:FRBRauthor as="#herausgeber-person" href="#farzin-sina-herausgeber-person-1"/>
          <akn:FRBRauthor as="#herausgeber-person" href="#jordan-stefan-herausgeber-person-2"/>
          <akn:FRBRauthor as="#bearbeiter" href="#satzky-manfred-bearbeiter-1"/>
          <akn:FRBRauthor as="#hochschule" href="#universitaet-erlangen-jurfak-hochschule-1"/>
          <akn:FRBRauthor as="#verlag" href="#reclam-verlag-1"/>
          <akn:FRBRcountry value="de"/>
          <akn:FRBRsubtype value="LS"/>
          <akn:FRBRnumber value="ISBN 978-3-15-019297-9"/>
          <akn:FRBRname value="TODO"/>
        </akn:FRBRWork>
        <akn:FRBRExpression>
          <akn:FRBRthis value="TODO"/>
          <akn:FRBRuri value="TODO"/>
          <akn:FRBRdate date="2025-09-17" name="Migration" ris:domainTerm="Migration"/>
          <akn:FRBRauthor href="TODO"/>
          <akn:FRBRlanguage language="deu"/>
        </akn:FRBRExpression>
        <akn:FRBRManifestation>
          <akn:FRBRthis value="TODO"/>
          <akn:FRBRuri value="TODO"/>
          <akn:FRBRdate date="2025-09-17" name="Migration" ris:domainTerm="Migration"/>
          <akn:FRBRauthor href="TODO"/>
          <akn:FRBRformat value="xml"/>
        </akn:FRBRManifestation>
      </akn:identification>
      <akn:classification source="doktyp">
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Bib" value="Bib"/>
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Dis" value="Dis"/>
      </akn:classification>
      <akn:classification source="schlagwort">
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Grundbegriff" value="Grundbegriff"/>
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Sozialtheorie" value="Sozialtheorie"/>
        <akn:keyword dictionary="attributsemantik-noch-undefiniert" showAs="Soziologie" value="Soziologie"/>
      </akn:classification>
      <akn:references source="attributsemantik-noch-undefiniert">
        <akn:TLCPerson eId="xx-verfasser-1" href="akn/ontology/persons/de/xx" showAs="XX"/>
        <akn:TLCPerson eId="farzin-sina-herausgeber-person-1" href="akn/ontology/persons/de/farzin-sina"
                       ris:name="Farzin, Sina"
                       ris:titel="M A, PhD-Fellow an der Bremen International Graduate School of Social Sciences der Universität Bremen"
                       showAs="Farzin, Sina, M A, PhD-Fellow an der Bremen International Graduate School of Social Sciences der Universität Bremen"/>
        <akn:TLCPerson eId="jordan-stefan-herausgeber-person-2" href="akn/ontology/persons/de/jordan-stefan"
                       ris:name="Jordan, Stefan"
                       ris:titel="Dr phil, wiss Angestellter der Historischen Kommission bei der Bayerischen Akademie der Wissenschaften"
                       showAs="Jordan, Stefan, Dr phil, wiss Angestellter der Historischen Kommission bei der Bayerischen Akademie der Wissenschaften"/>
        <akn:TLCPerson eId="satzky-manfred-bearbeiter-1" href="akn/ontology/persons/de/satzky-manfred"
                       ris:name="Satzky, Manfred" ris:titel="Amtsrat" showAs="Satzky, Manfred, Amtsrat"/>
        <akn:TLCOrganization eId="universitaet-erlangen-jurfak-hochschule-1"
                             href="akn/ontology/organizations/de/universitaet-erlangen-jurfak" ris:jahr="1984"
                             ris:name="Universität Erlangen, JurFak" showAs="Universität Erlangen, JurFak, 1984"/>
        <akn:TLCOrganization eId="reclam-verlag-1"
                             href="akn/ontology/organizations/de/reclam" ris:name="Reclam"
                             ris:ort="Stuttgart" showAs="Reclam, Stuttgart"/>
        <akn:TLCRole eId="verfasser" href="akn/ontology/roles/de/verfasser" showAs="Verfasser"/>
        <akn:TLCRole eId="herausgeber-person" href="akn/ontology/roles/de/herausgeber-person"
                     showAs="Herausgeber Person"/>
        <akn:TLCRole eId="bearbeiter" href="akn/ontology/roles/de/bearbeiter" showAs="Bearbeiter"/>
        <akn:TLCRole eId="hochschule" href="akn/ontology/roles/de/hochschule" showAs="Hochschule"/>
        <akn:TLCRole eId="verlag" href="akn/ontology/roles/de/verlag" showAs="Verlag"/>
      </akn:references>
      <akn:proprietary source="attributsemantik-noch-undefiniert">
        <ris:meta>
          <ris:sachgebiete>
            <ris:sachgebiet notation="NEW">RE-01-02-07</ris:sachgebiet>
            <ris:sachgebiet notation="NEW">RE-01-03-09</ris:sachgebiet>
          </ris:sachgebiete>
          <ris:veroeffentlichungsJahre>
            <ris:veroeffentlichungsJahr>2015</ris:veroeffentlichungsJahr>
          </ris:veroeffentlichungsJahre>
          <ris:gesamttitelAngaben>
            <ris:gesamttitel bandbezeichnung="Nr 19297" titel="Reclams Universal-Bibliothek"/>
          </ris:gesamttitelAngaben>
          <ris:zuordnungen>
            <ris:zuordnung aspekt="Titel" begriff="hundert Grundbegriffe"/>
            <ris:zuordnung aspekt="Titel" begriff="100 Grundbegriffe"/>
          </ris:zuordnungen>
          <ris:besitznachweise>
            <ris:besitznachweis institution="BSG" signatur="VwiK 1032"/>
          </ris:besitznachweise>
          <ris:kollation umfangsangabe="359 S"/>
        </ris:meta>
      </akn:proprietary>
    </akn:meta>
    <akn:preface>
      <akn:longTitle>
        <akn:block name="longTitle">Lexikon Soziologie und Sozialtheorie</akn:block>
      </akn:longTitle>
    </akn:preface>
    <akn:mainBody>
      <akn:hcontainer name="crossheading"/>
    </akn:mainBody>
  </akn:doc>
</akn:akomaNtoso>$$
            , p.final_doc_number),
        'LITERATUR_SELBSTAENDIG',
        'BAG'
    FROM params p
    ON CONFLICT DO NOTHING
    RETURNING id AS created_documentation_unit_id
)
INSERT
INTO literature.documentation_unit_index (id, documentation_unit_id, titel, veroeffentlichungsjahr, dokumenttypen, dokumenttypen_combined, verfasser_list, verfasser_list_combined)
SELECT gen_random_uuid(),
       created.created_documentation_unit_id,
       'Lexikon Soziologie und Sozialtheorie',
       '2015',
       array ['Bib', 'Dis'],
       'Bib Dis',
        array ['Farzin, Sina', 'Jordan, Stefan'],
       'Farzin, Sina Jordan, Stefan'
FROM created
ON CONFLICT DO NOTHING;
