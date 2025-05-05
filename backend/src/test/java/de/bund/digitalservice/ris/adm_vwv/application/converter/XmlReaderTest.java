package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.*;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class XmlReaderTest {

  private final XmlReader xmlReader = new XmlReader();

  @Test
  void readXml() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/metadata/">
        <akn:doc name="offene-struktur">
          <akn:preface>
            <akn:longTitle>
              <akn:block name="longTitle">Ein länglicher Titel mit
                  Zeilenumbrüchen
              </akn:block>
            </akn:longTitle>
          </akn:preface>
        </akn:doc>
      </akn:akomaNtoso>
      """;

    // when
    AkomaNtoso akomaNtoso = xmlReader.readXml(xml);

    // then
    assertThat(akomaNtoso)
      .isNotNull()
      .extracting(akn -> akn.getDoc().getPreface().getLongTitle().getBlock().getHtml())
      .asInstanceOf(InstanceOfAssertFactories.LIST)
      .hasSize(1)
      .first()
      .isEqualTo("Ein länglicher Titel mit\n            Zeilenumbrüchen\n        ");
  }

  @Test
  void readXml_notValidXml() {
    // given
    String xml =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <akn:akomaNtoso
        xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
        xmlns:ris="http://ldml.neuris.de/metadata/">
        <akn:doc name="offene-struktur">
      </akn:akomaNtoso>
      """;

    // when
    Exception exception = catchException(() -> xmlReader.readXml(xml));

    // then
    assertThat(exception).isInstanceOf(IllegalStateException.class);
  }
}
