package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReferenceNumbersTransformerTest {

  @Test
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
    risMeta.setReferenceNumbers(List.of("AX-Y12345", "XX"));
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //           <ris:referenceNumbers>
    //             <ris:referenceNumber>AX-Y12345</ris:referenceNumber>
    //             <ris:referenceNumber>XX</ris:referenceNumber>
    //           </ris:referenceNumbers>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    List<String> referenceNumbers = new ReferenceNumbersTransformer(akomaNtoso).transform();

    // then
    assertThat(referenceNumbers).containsExactly("AX-Y12345", "XX");
  }

  @Test
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
    List<String> referenceNumbers = new ReferenceNumbersTransformer(akomaNtoso).transform();

    // then
    assertThat(referenceNumbers).isEmpty();
  }

  @Test
  void transform_noReferenceNumbersElement() {
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
    List<String> referenceNumbers = new ReferenceNumbersTransformer(akomaNtoso).transform();

    // then
    assertThat(referenceNumbers).isEmpty();
  }
}
