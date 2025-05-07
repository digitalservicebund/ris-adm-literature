package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
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
    RisMetadata risMetadata = new RisMetadata();
    proprietary.setMetadata(risMetadata);
    risMetadata.setDateToQuoteList(List.of("2021-12-12", "2021-12-31"));
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
    String actualDateToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDateToQuote).isEqualTo("2021-12-12");
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
    String actualDateToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDateToQuote).isNull();
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
    RisMetadata risMetadata = new RisMetadata();
    proprietary.setMetadata(risMetadata);
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
    String actualDateToQuote = new DateToQuoteTransformer(akomaNtoso).transform();

    // then
    assertThat(actualDateToQuote).isNull();
  }
}
