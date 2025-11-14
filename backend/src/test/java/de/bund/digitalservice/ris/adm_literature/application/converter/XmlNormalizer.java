package de.bund.digitalservice.ris.adm_literature.application.converter;

import java.util.function.UnaryOperator;

class XmlNormalizer {

  static final UnaryOperator<String> NORMALIZE_FUNCTION = text ->
    text
      // Remove whitespaces and linefeeds between end and start of tags
      .replaceAll(">\\s+<", "><")
      // Normalize line feeds to \n, strips leading white spaces
      .indent(Integer.MIN_VALUE)
      // Remove very last line feed inserted by ident() method
      .stripTrailing();
}
