package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.XmlWriter;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.util.List;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class KurzreferatTransformerTest {

  private KurzreferatTransformer kurzreferatTransformer;

  @BeforeEach
  void beforeEach() throws JAXBException {
    kurzreferatTransformer = new KurzreferatTransformer(
      new XmlWriter(JAXBContext.newInstance(AkomaNtoso.class, JaxbHtml.class))
    );
  }

  @Test
  void transform() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    MainBody mainBody = new MainBody();
    doc.setMainBody(mainBody);
    JaxbHtml div = new JaxbHtml();
    mainBody.setDiv(div);
    String textNode = "\n";
    JAXBElement<String> line1 = new JAXBElement<>(QName.valueOf("akn:p"), String.class, "Zeile 1");
    JAXBElement<String> line2 = new JAXBElement<>(QName.valueOf("akn:p"), String.class, "Zeile 2");
    div.setHtml(List.of(textNode, line1, textNode, line2, textNode));

    // when
    String actualKurzreferat = kurzreferatTransformer.transform(akomaNtoso);

    // then
    assertThat(actualKurzreferat).startsWith("<p>Zeile 1</p>").endsWith("<p>Zeile 2</p>");
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
    String actualKurzreferat = kurzreferatTransformer.transform(akomaNtoso);

    // then
    assertThat(actualKurzreferat).isNull();
  }

  @Test
  @Tag("RISDEV-7821")
  void transform_mainBodyElementWithHContainer() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    MainBody mainBody = new MainBody();
    JaxbHtml hcontainer = new JaxbHtml();
    hcontainer.setName("crossheading");
    mainBody.setHcontainer(hcontainer);
    doc.setMainBody(mainBody);

    // when
    String actualKurzreferat = kurzreferatTransformer.transform(akomaNtoso);

    // then
    assertThat(actualKurzreferat).isNull();
  }
}
