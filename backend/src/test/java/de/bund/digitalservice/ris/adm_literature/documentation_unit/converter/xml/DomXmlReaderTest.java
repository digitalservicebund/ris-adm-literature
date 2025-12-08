package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

class DomXmlReaderTest {

  private final DomXmlReader domXmlReader = new DomXmlReader();

  @Test
  void readXml() {
    // given
    String xml = "<div><p>Zeile 1</p><p>Zeile 2</p></div>";

    // when
    Node node = domXmlReader.readXml(xml);

    // then
    assertThat(List.of(node.getFirstChild(), node.getLastChild()))
      .extracting(Node::getNodeName, Node::getTextContent)
      .containsExactly(Tuple.tuple("p", "Zeile 1"), Tuple.tuple("p", "Zeile 2"));
  }

  @Test
  void readXml_notValidXml() {
    // given
    String xml = "<div><p>Zeile 1";

    // when
    Exception exception = catchException(() -> domXmlReader.readXml(xml));

    // then
    assertThat(exception).isInstanceOf(IllegalStateException.class);
  }
}
