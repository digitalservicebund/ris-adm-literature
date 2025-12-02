-- Fix typos in the 'documentation_unit' table
UPDATE documentation_unit
SET documentation_unit_type = CASE documentation_unit_type
                                  WHEN 'LITERATUR_UNSELBSTSTAENDIG' THEN 'LITERATUR_UNSELBSTAENDIG'
                                  WHEN 'LITERATUR_SELBSTSTAENDIG' THEN 'LITERATUR_SELBSTAENDIG'
                                  ELSE documentation_unit_type
    END
WHERE documentation_unit_type IN ('LITERATUR_UNSELBSTSTAENDIG', 'LITERATUR_SELBSTSTAENDIG');


-- Fix typos in the 'documentation_unit_index' table
UPDATE literature_documentation_unit_index
SET documentation_unit_type = CASE documentation_unit_type
                                  WHEN 'LITERATUR_UNSELBSTSTAENDIG' THEN 'LITERATUR_UNSELBSTAENDIG'
                                  WHEN 'LITERATUR_SELBSTSTAENDIG' THEN 'LITERATUR_SELBSTAENDIG'
                                  ELSE documentation_unit_type
    END
WHERE documentation_unit_type IN ('LITERATUR_UNSELBSTSTAENDIG', 'LITERATUR_SELBSTSTAENDIG');
