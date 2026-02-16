DROP VIEW IF EXISTS ref_view_adm;
CREATE OR REPLACE VIEW ref_view_adm AS
SELECT du.id,
       du.document_number,
       du.documentation_office,
       dui.dokumenttyp,
       dui.langueberschrift,
       dui.inkrafttretedatum,
       dui.zitierdaten,
       dui.zitierdaten_combined,
       dui.fundstellen,
       dui.fundstellen_combined,
       dui.normgeber_list,
       dui.normgeber_list_combined,
       dui.aktenzeichen_list,
       dui.aktenzeichen_list_combined
FROM adm.documentation_unit_index dui
         join adm.documentation_unit du on dui.documentation_unit_id = du.id
WHERE du.xml is not null;

DROP VIEW IF EXISTS  ref_view_active_reference_adm_sli;
CREATE OR REPLACE VIEW ref_view_active_reference_adm_sli AS
SELECT adm_ars.id,
       adm_ars.source_documentation_unit_id,
       adm_ars.target_documentation_unit_id
FROM adm.active_reference_sli adm_ars;

DROP VIEW IF EXISTS  ref_view_active_reference_adm_uli;
CREATE OR REPLACE VIEW ref_view_active_reference_adm_uli AS
SELECT adm_aru.id,
       adm_aru.source_documentation_unit_id,
       adm_aru.target_documentation_unit_id
FROM adm.active_reference_uli adm_aru;

DROP VIEW IF EXISTS  ref_view_active_reference_adm_caselaw;
CREATE OR REPLACE VIEW ref_view_active_reference_adm_caselaw AS
SELECT adm_arc.id,
       adm_arc.source_documentation_unit_id,
       adm_arc.target_documentation_unit_id
FROM adm.active_reference_caselaw adm_arc;

DROP VIEW IF EXISTS  ref_view_literature;
CREATE OR REPLACE VIEW ref_view_literature AS
SELECT du.id,
       du.document_number,
       du.documentation_office,
       du.documentation_unit_type as document_category,
       dui.titel,
       dui.veroeffentlichungsjahr,
       dui.dokumenttypen,
       dui.dokumenttypen_combined,
       dui.verfasser_list,
       dui.verfasser_list_combined
FROM literature.documentation_unit_index dui
         join literature.documentation_unit du on dui.documentation_unit_id = du.id
WHERE du.xml is not null;

DROP VIEW IF EXISTS  ref_view_active_reference_sli_adm;
CREATE OR REPLACE VIEW ref_view_active_reference_sli_adm AS
SELECT literature_ar_adm.id,
       literature_ar_adm.source_documentation_unit_id,
       literature_ar_adm.target_documentation_unit_id
FROM literature.active_reference_adm literature_ar_adm
         join literature.documentation_unit source_du on literature_ar_adm.source_documentation_unit_id = source_du.id
WHERE source_du.documentation_unit_type = 'LITERATUR_SELBSTAENDIG' AND literature_ar_adm.target_documentation_unit_id IS NOT NULL;

DROP VIEW IF EXISTS  ref_view_active_reference_uli_adm;
CREATE OR REPLACE VIEW ref_view_active_reference_uli_adm AS
SELECT literature_ar_adm.id,
       literature_ar_adm.source_documentation_unit_id,
       literature_ar_adm.target_documentation_unit_id
FROM literature.active_reference_adm literature_ar_adm
         join literature.documentation_unit source_du on literature_ar_adm.source_documentation_unit_id = source_du.id
WHERE source_du.documentation_unit_type = 'LITERATUR_UNSELBSTAENDIG' AND literature_ar_adm.target_documentation_unit_id IS NOT NULL;
