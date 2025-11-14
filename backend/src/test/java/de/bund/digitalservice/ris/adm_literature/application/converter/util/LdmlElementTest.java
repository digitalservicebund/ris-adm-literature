package de.bund.digitalservice.ris.adm_literature.application.converter.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

class LdmlElementTest {

  @Test
  void appendElementOnceAndGet() throws Exception {
    // given
    LdmlDocument ldmlDocument = new MinimalLdmlDocument().create(LiteratureDocumentCategory.ULI);
    LdmlElement tlcRole = new LdmlElement(ldmlDocument.addReferences(), "akn:TLCRole")
      .addAttribute("eId", "eId")
      .addAttribute("showAs", "showAs");

    // when
    ldmlDocument.addReferences("different").appendElementOnceAndGet(tlcRole);
    ldmlDocument.addReferences("different").appendElementOnceAndGet(tlcRole);

    // then
    LSSerializer serializer = getLsSerializer(ldmlDocument);
    assertThat(serializer.writeToString(ldmlDocument.getMeta())).contains(
      """
      <akn:references source="different">
          <akn:TLCRole eId="eId" showAs="showAs"/>
      </akn:references>""".indent(4)
    );
  }

  @Test
  void appendNode() throws Exception {
    // given
    LdmlDocument ldmlDocument = new MinimalLdmlDocument().create(LiteratureDocumentCategory.ULI);

    // when
    ldmlDocument
      .mainBody()
      .appendNode(ldmlDocument.createElement("akn:div"))
      .appendNode(ldmlDocument.createElement("akn:div"));

    // then
    LSSerializer serializer = getLsSerializer(ldmlDocument);
    assertThat(serializer.writeToString(ldmlDocument.mainBody().getElement())).contains(
      """
      <akn:mainBody>
          <akn:div/>
          <akn:div/>
      </akn:mainBody>"""
    );
  }

  @Test
  void appendText() throws Exception {
    // given
    LdmlDocument ldmlDocument = new MinimalLdmlDocument().create(LiteratureDocumentCategory.ULI);

    // when
    LdmlElement p = ldmlDocument
      .preface()
      .appendElementAndGet("akn:div")
      .appendElementAndGet("akn:p")
      .appendText("Erste Zeile")
      .appendText("\nZweite Zeile")
      .appendText("\nDritte Zeile");

    // then
    LSSerializer serializer = getLsSerializer(ldmlDocument);
    assertThat(serializer.writeToString(p.getElement().getParentNode())).contains(
      """
      <akn:div>
          <akn:p>Erste Zeile
      Zweite Zeile
      Dritte Zeile</akn:p>
      </akn:div>"""
    );
  }

  private LSSerializer getLsSerializer(LdmlDocument ldmlDocument) {
    DOMImplementationLS domImplLS = (DOMImplementationLS) ldmlDocument
      .getDocument()
      .getImplementation();
    LSSerializer serializer = domImplLS.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
    serializer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
    return serializer;
  }
}
