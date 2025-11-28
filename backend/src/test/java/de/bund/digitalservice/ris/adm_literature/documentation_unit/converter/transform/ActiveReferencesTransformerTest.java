package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.ActiveReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SingleNorm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation.NormAbbreviation;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp.VerweisTyp;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp.VerweisTypService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActiveReferencesTransformerTest {

  @InjectMocks
  private ActiveReferencesTransformer activeReferencesTransformer;

  @Mock
  private VerweisTypService verweisTypService;

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
    RisActiveReference activeReference = new RisActiveReference();
    activeReference.setReference("PhanG");
    activeReference.setParagraph("§ 1");
    activeReference.setPosition("Abs. 2");
    activeReference.setTypeNumber("82");
    activeReference.setDateOfVersion("2025-07-07");
    risMeta.setActiveReferences(List.of(activeReference));
    given(verweisTypService.findVerweisTypByTypNummer("82")).willReturn(
      Optional.of(new VerweisTyp(UUID.randomUUID(), "Rechtsgrundlage", "82"))
    );
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
    List<ActiveReference> activeReferences = activeReferencesTransformer.transform(akomaNtoso);

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
    01, Anwendung
    31, Neuregelung
    82, Rechtsgrundlage
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
    RisMeta risMeta = new RisMeta();
    proprietary.setMeta(risMeta);
    RisActiveReference activeReference = new RisActiveReference();
    activeReference.setReference("PhanG");
    activeReference.setTypeNumber(typeNumber);
    risMeta.setActiveReferences(List.of(activeReference));
    given(verweisTypService.findVerweisTypByTypNummer(typeNumber)).willReturn(
      Optional.of(new VerweisTyp(UUID.randomUUID(), expectedReferenceType, typeNumber))
    );
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
    List<ActiveReference> activeReferences = activeReferencesTransformer.transform(akomaNtoso);

    // then
    assertThat(activeReferences)
      .hasSize(1)
      .first()
      .extracting(ActiveReference::verweisTyp)
      .extracting(VerweisTyp::name)
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
    List<ActiveReference> activeReferences = activeReferencesTransformer.transform(akomaNtoso);

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
    List<ActiveReference> activeReferences = activeReferencesTransformer.transform(akomaNtoso);

    // then
    assertThat(activeReferences).isEmpty();
  }
}
