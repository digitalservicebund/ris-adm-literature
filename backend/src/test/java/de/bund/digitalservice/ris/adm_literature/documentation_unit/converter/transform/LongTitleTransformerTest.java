package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class LongTitleTransformerTest {

  @Test
  void transform() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Preface preface = new Preface();
    doc.setPreface(preface);
    LongTitle longTitle = new LongTitle();
    preface.setLongTitle(longTitle);
    JaxbHtml block = new JaxbHtml();
    block.setHtml(List.of("Langer Titel"));
    longTitle.setBlock(block);
    preface.setLongTitle(longTitle);

    // when
    String actualLongTitle = new LongTitleTransformer(akomaNtoso).transform();

    // then
    assertThat(actualLongTitle).isEqualTo("Langer Titel");
  }

  @Test
  void transform_noPrefaceElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);

    // when
    String actualLongTitle = new LongTitleTransformer(akomaNtoso).transform();

    // then
    assertThat(actualLongTitle).isNull();
  }
}
