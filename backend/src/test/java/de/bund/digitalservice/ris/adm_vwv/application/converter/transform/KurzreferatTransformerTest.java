package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import jakarta.xml.bind.JAXBElement;
import java.util.List;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class KurzreferatTransformerTest {

  @Test
  void transform() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    MainBody mainBody = new MainBody();
    doc.setMainBody(mainBody);
    JaxbHtml div = new JaxbHtml();
    div.setHtml(List.of("Langer Titel"));
    mainBody.setDiv(div);
    JAXBElement<String> line1 = new JAXBElement<>(QName.valueOf("akn:p"), String.class, "Zeile 1");
    JAXBElement<String> line2 = new JAXBElement<>(QName.valueOf("akn:p"), String.class, "Zeile 2");
    div.setHtml(List.of(line1, line2));

    // when
    String actualKurzreferat = new KurzreferatTransformer(akomaNtoso).transform();

    // then
    assertThat(actualKurzreferat).containsSubsequence("<p>Zeile 1</p>", "<p>Zeile 2</p>");
  }

  @Test
  void transform_noMainBodyElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);

    // when
    String actualKurzreferat = new KurzreferatTransformer(akomaNtoso).transform();

    // then
    assertThat(actualKurzreferat).isNull();
  }
}
