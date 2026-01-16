DROP VIEW IF EXISTS literature_reference_view;

CREATE OR REPLACE VIEW literature_reference_view AS
SELECT du.document_number,
       du.documentation_office,
       dui.titel,
       dui.veroeffentlichungsjahr,
       dui.dokumenttypen,
       dui.dokumenttypen_combined,
       dui.verfasser_list,
       dui.verfasser_list_combined
FROM literature.documentation_unit_index dui join literature.documentation_unit du on dui.documentation_unit_id = du.id
WHERE du.xml is not null;
