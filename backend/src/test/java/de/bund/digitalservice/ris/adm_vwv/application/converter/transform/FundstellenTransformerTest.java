package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_vwv.application.LegalPeriodical;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPersistencePort;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class FundstellenTransformerTest {

  @InjectMocks
  private FundstellenTransformer fundstellenTransformer;

  @Mock
  private LookupTablesPersistencePort lookupTablesPersistencePort;

  @Test
  @DisplayName("Transforms two Fundstellen with their legal periodicals")
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
      List.of(createOtherReference("BAnz", "BAnz Seite 5"), createOtherReference("DOK", "DOK 2021"))
    );
    given(lookupTablesPersistencePort.findLegalPeriodicalsByAbbreviation("BAnz")).willReturn(
      List.of(
        new LegalPeriodical(UUID.randomUUID(), "BAnz", "Bundesanzeiger", null, "2025, Seite 2")
      )
    );
    given(lookupTablesPersistencePort.findLegalPeriodicalsByAbbreviation("DOK")).willReturn(
      List.of(new LegalPeriodical(UUID.randomUUID(), "DOK", "Dokument", null, "2020"))
    );

    // when
    List<Reference> references = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(references)
      .hasSize(2)
      .extracting(Reference::legalPeriodicalRawValue, Reference::citation, r ->
        r.legalPeriodical().title()
      )
      .containsExactly(
        tuple("BAnz", "Seite 5", "Bundesanzeiger"),
        tuple("DOK", "2021", "Dokument")
      );
  }

  @Test
  @DisplayName("Missing analysis element is transformed to an empty list")
  void transform_noAnalysisElement() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);

    // when
    List<Reference> references = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(references).isEmpty();
  }

  @Test
  @DisplayName(
    "Transforms one fundstelle without legal periodical because abbreviation 'BRD' is ambiguous"
  )
  void transform_ambiguousLegalPeriodical() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Analysis analysis = new Analysis();
    meta.setAnalysis(analysis);
    analysis.setOtherReferences(List.of(createOtherReference("BRD", "BRD Seite 5")));
    given(lookupTablesPersistencePort.findLegalPeriodicalsByAbbreviation("BRD")).willReturn(
      List.of(
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "Bericht aus Deutschland",
          null,
          "2025, Seite 2"
        ),
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "Bericht aus Deutschland alt",
          null,
          "1998, Seite 2"
        )
      )
    );

    // when
    List<Reference> references = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(references)
      .singleElement()
      .extracting(
        Reference::legalPeriodicalRawValue,
        Reference::citation,
        Reference::legalPeriodical
      )
      .containsExactly("BRD", "Seite 5", null);
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
