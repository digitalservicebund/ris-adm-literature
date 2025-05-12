package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.ActiveReference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.NormAbbreviation;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.SingleNorm;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ActiveReferencesTransformerTest {

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
    RisActiveReference activeReference = new RisActiveReference();
    activeReference.setReference("PhanG");
    activeReference.setParagraph("§ 1");
    activeReference.setPosition("Abs. 2");
    activeReference.setTypeNumber("82");
    activeReference.setDateOfVersion("2025-07-07");
    risMetadata.setActiveReferences(List.of(activeReference));
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //           <ris:activeReferences>
    //             <ris:activeReference typeNumber="82" reference="PhanG" paragraph="§ 1"
    //                                  position="Abs. 2" dateOfVersion="2025-07-07" />
    //           </ris:activeReferences>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    List<ActiveReference> activeReferences = new ActiveReferencesTransformer(
      akomaNtoso
    ).transform();

    // then
    assertThat(activeReferences)
      .hasSize(1)
      .first()
      .extracting(
        ActiveReference::referenceDocumentType,
        ActiveReference::normAbbreviation,
        ActiveReference::singleNorms
      )
      .containsExactly(
        "administrative_regulation",
        new NormAbbreviation(null, "PhanG", null),
        List.of(new SingleNorm(null, "§ 1 Abs. 2", "2025-07-07", null))
      );
  }

  @ParameterizedTest
  @CsvSource(
    textBlock = """
    01, anwendung
    31, neuregelung
    82, rechtsgrundlage
    99, 99
    """
  )
  void transform_typeNumbers(String typeNumber, String expectedReferenceType) {
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
    RisActiveReference activeReference = new RisActiveReference();
    activeReference.setReference("PhanG");
    activeReference.setTypeNumber(typeNumber);
    risMetadata.setActiveReferences(List.of(activeReference));
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:proprietary>
    //         <ris:metadata>
    //           <ris:activeReferences>
    //             <ris:activeReference typeNumber="{typeNumber}" reference="PhanG" />
    //           </ris:activeReferences>
    //         </ris:metadata>
    //       </akn:proprietary>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    List<ActiveReference> activeReferences = new ActiveReferencesTransformer(
      akomaNtoso
    ).transform();

    // then
    assertThat(activeReferences)
      .hasSize(1)
      .first()
      .extracting(ActiveReference::referenceType)
      .isEqualTo(expectedReferenceType);
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
    List<ActiveReference> activeReferences = new ActiveReferencesTransformer(
      akomaNtoso
    ).transform();

    // then
    assertThat(activeReferences).isEmpty();
  }

  @Test
  void transform_noActiveReferencesElement() {
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
    List<ActiveReference> activeReferences = new ActiveReferencesTransformer(
      akomaNtoso
    ).transform();

    // then
    assertThat(activeReferences).isEmpty();
  }
}
