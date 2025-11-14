package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class DateToQuoteTransformerTest {

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
    risMeta.setDateToQuoteList(List.of("2021-12-12", "2021-12-31"));
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //           <ris:dateToQuoteList>
    //             <ris:dateToQuote>2021-12-12</ris:dateToQuote>
    //             <ris:dateToQuote>2021-12-31</ris:dateToQuote>
    //           </ris:dateToQuoteList>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    List<String> actualDatesToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDatesToQuote).containsExactly("2021-12-12", "2021-12-31");
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
    List<String> actualDatesToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDatesToQuote).isEmpty();
  }

  @Test
  void transform_noDateToQuoteListElement() {
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
    List<String> actualDatesToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDatesToQuote).isEmpty();
  }
}
