package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class FundstellenTransformerTest {

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
      List.of(createOtherReference("BAnz", "Seite 5"), createOtherReference("DOK", "2021"))
    );

    // when
    List<Reference> references = new FundstellenTransformer(akomaNtoso).transform();

    // then
    assertThat(references)
      .hasSize(2)
      .extracting(Reference::legalPeriodicalRawValue, Reference::citation)
      .containsExactly(tuple("BAnz", "Seite 5"), tuple("DOK", "2021"));
  }

  @Test
  void transform_noAnalysisElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);

    // when
    List<Reference> references = new FundstellenTransformer(akomaNtoso).transform();

    // then
    assertThat(references).isEmpty();
  }

  private OtherReferences createOtherReference(String legalPeriodicalRawValue, String citation) {
    OtherReferences otherReferences = new OtherReferences();
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setShowAs(citation);
    implicitReference.setShortForm(legalPeriodicalRawValue);
    otherReferences.setImplicitReference(implicitReference);
    return otherReferences;
  }
}
