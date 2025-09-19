package de.bund.digitalservice.ris.adm_vwv.adapter.publishing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class XmlValidatorTest {

  private final String validXml =
    """
    <note>
        <to>Tove</to>
        <from>Frombert</from>
        <body>Remember me this weekend!</body>
    </note>
    """;

  private final String invalidXml =
    """
    <note>
        <to>Tove</to>
        <heading>Reminder</heading>
    </note>
    """;

  @Test
  void constructor_shouldThrowException_whenSchemaListIsNull() {
    assertThrows(IllegalArgumentException.class, () -> new XmlValidator(null));
  }

  @Test
  void constructor_shouldThrowException_whenSchemaListIsEmpty() {
    List<String> emptyList = Collections.emptyList();
    assertThrows(IllegalArgumentException.class, () -> new XmlValidator(emptyList));
  }

  @Test
  void constructor_shouldThrowException_whenSchemaPathIsNotFound() {
    List<String> nonExistentPath = List.of("/non/existent/path.xsd");
    assertThrows(IllegalArgumentException.class, () -> new XmlValidator(nonExistentPath));
  }

  @Test
  void validate_shouldSucceed_forValidXml() {
    XmlValidator validator = new XmlValidator(List.of("/schemas/test-schema.xsd"));
    assertDoesNotThrow(() -> validator.validate(validXml));
  }

  @Test
  void validate_shouldThrowException_forInvalidXml() {
    XmlValidator validator = new XmlValidator(List.of("/schemas/test-schema.xsd"));
    assertThrows(SAXException.class, () -> validator.validate(invalidXml));
  }
}
