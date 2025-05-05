package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class XmlWriterTest {

  private final XmlWriter xmlWriter = new XmlWriter();

  @Test
  void writeXml() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    Preface preface = new Preface();
    LongTitle longTitle = new LongTitle();
    JaxbHtml block = new JaxbHtml();
    block.setName("longTitle");
    block.setHtml(List.of("Langer Titel"));
    longTitle.setBlock(block);
    preface.setLongTitle(longTitle);
    doc.setPreface(preface);
    akomaNtoso.setDoc(doc);

    // when
    String xml = xmlWriter.writeXml(akomaNtoso);

    // then
    assertThat(xml)
      .isNotNull()
      .isEqualTo(
        """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <akn:akomaNtoso xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0" xmlns:ris="http://ldml.neuris.de/metadata/">
            <akn:doc name="offene-struktur">
                <akn:preface>
                    <akn:longTitle>
                        <akn:block name="longTitle">Langer Titel</akn:block>
                    </akn:longTitle>
                </akn:preface>
            </akn:doc>
        </akn:akomaNtoso>
        """
      );
  }

  @Test
  void writeXml_exceptionDueToNonRootElement() {
    // given
    Doc doc = new Doc();
    Preface preface = new Preface();
    LongTitle longTitle = new LongTitle();
    JaxbHtml block = new JaxbHtml();
    block.setName("longTitle");
    block.setHtml(List.of("Langer Titel"));
    longTitle.setBlock(block);
    preface.setLongTitle(longTitle);
    doc.setPreface(preface);

    // when
    Exception exception = catchException(() -> xmlWriter.writeXml(doc));

    // then
    assertThat(exception).isNotNull().isInstanceOf(IllegalStateException.class);
  }
}
