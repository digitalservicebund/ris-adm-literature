DROP VIEW IF EXISTS adm_reference_view;

CREATE OR REPLACE VIEW adm_reference_view AS
SELECT du.document_number,
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
FROM adm.documentation_unit_index dui join adm.documentation_unit du on dui.documentation_unit_id = du.id
WHERE du.xml is not null;
