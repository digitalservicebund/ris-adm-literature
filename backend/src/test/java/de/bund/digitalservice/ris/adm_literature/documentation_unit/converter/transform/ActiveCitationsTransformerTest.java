package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.ActiveCitation;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart.ZitierArt;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart.ZitierArtService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class ActiveCitationsTransformerTest {

  @InjectMocks
  private ActiveCitationsTransformer activeCitationsTransformer;

  @Mock
  private ZitierArtService zitierArtService;

  @Test
  void transform() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Analysis analysis = new Analysis();
    meta.setAnalysis(analysis);
    analysis.setOtherReferences(
      List.of(createOtherReference("Änderung", "XY-01"), createOtherReference("Übernahme", "ZZ-02"))
    );
    //noinspection unchecked
    given(
      zitierArtService.findZitierArtenByAbbreviation(
        anyString(),
        eq(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
      )
    ).willReturn(
      List.of(new ZitierArt(UUID.randomUUID(), "Änderung", "Änderung")),
      List.of(new ZitierArt(UUID.randomUUID(), "Übernahme", "Übernahme"))
    );
    // <akn:akomaNtoso>
    //   <akn:doc name="offene-struktur">
    //     <akn:meta>
    //       <akn:analysis source="attributsemantik-noch-undefiniert">
    //         <akn:otherReferences source="attributsemantik-noch-undefiniert">
    //          <akn:implicitReference shortForm="Änderung PhanGH XY-01" showAs="Änderung PhanGH XY-01 2025-06-06">
    //            <ris:caselawReference abbreviation="Änderung" court="PhanGH" date="2025-06-06" referenceNumber="XY-01"
    //                                  documentNumber="KSNR0000000001"/>
    //          </akn:implicitReference>
    //        </akn:otherReferences>
    //        <akn:otherReferences source="attributsemantik-noch-undefiniert">
    //          <akn:implicitReference shortForm="Übernahme PhanGH ZZ-02" showAs="Übernahme PhanGH ZZ-02 2025-06-06">
    //            <ris:caselawReference abbreviation="Übernahme" court="PhanGH" date="2025-06-06" referenceNumber="ZZ-02"
    //                                  documentNumber="KSNR0000000001"/>
    //          </akn:implicitReference>
    //        </akn:otherReferences>
    //       </akn:analysis>
    //     </akn:meta>
    //   </akn:doc>
    // </akn:akomaNtoso>

    // when
    List<ActiveCitation> activeCitations = activeCitationsTransformer.transform(akomaNtoso);

    // then
    assertThat(activeCitations)
      .hasSize(2)
      .extracting(ac -> ac.zitierArt().abbreviation(), ActiveCitation::fileNumber)
      .containsExactly(tuple("Änderung", "XY-01"), tuple("Übernahme", "ZZ-02"));
  }

  @Test
  void transform_noAnalysisElement() {
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
    List<ActiveCitation> activeCitations = activeCitationsTransformer.transform(akomaNtoso);

    // then
    assertThat(activeCitations).isEmpty();
  }

  private OtherReferences createOtherReference(String citationType, String referenceNumber) {
    OtherReferences otherReferences = new OtherReferences();
    ImplicitReference implicitReference = new ImplicitReference();
    RisCaselawReference risCaselawReference = new RisCaselawReference();
    risCaselawReference.setAbbreviation(citationType);
    risCaselawReference.setReferenceNumber(referenceNumber);
    risCaselawReference.setDate("2025-06-06");
    risCaselawReference.setDocumentNumber("KSNR0000000001");
    risCaselawReference.setCourt("PhanGH");
    risCaselawReference.setCourtLocation("Berlin");
    implicitReference.setCaselawReference(risCaselawReference);
    otherReferences.setImplicitReferences(List.of(implicitReference));
    return otherReferences;
  }
}
