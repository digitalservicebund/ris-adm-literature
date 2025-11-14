package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TableOfContentsTransformerTest {

  @Test
  @DisplayName("Transforms two table of content entries to one HTML string")
  void transform() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Proprietary proprietary = new Proprietary();
    meta.setProprietary(proprietary);
    RisMeta risMeta = new RisMeta();
    proprietary.setMeta(risMeta);
    risMeta.setTableOfContentsEntries(List.of("Kapitel 1", "Kapitel 2"));
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //           <ris:tableOfContentEntries>
    //             <ris:tableOfContentEntry>Kapitel 1</ris:tableOfContentEntry>
    //             <ris:tableOfContentEntry>Kapitel 2</ris:tableOfContentEntry>
    //           </ris:tableOfContentEntries>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    String gliederung = new TableOfContentsTransformer().transform(akomaNtoso);

    // then
    assertThat(gliederung).isEqualTo(
      """
      <p>Kapitel 1</p>
      <p>Kapitel 2</p>"""
    );
  }

  @Test
  @DisplayName("Missing proprietary element is transformed to an empty list")
  void transform_noProprietaryElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    String gliederung = new TableOfContentsTransformer().transform(akomaNtoso);

    // then
    assertThat(gliederung).isNull();
  }

  @Test
  @DisplayName("Missing tableOfContentEntries element is transformed to an empty list")
  void transform_noTableOfContentEntriesElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Proprietary proprietary = new Proprietary();
    meta.setProprietary(proprietary);
    RisMeta risMeta = new RisMeta();
    proprietary.setMeta(risMeta);
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    String gliederung = new TableOfContentsTransformer().transform(akomaNtoso);

    // then
    assertThat(gliederung).isNull();
  }
}
