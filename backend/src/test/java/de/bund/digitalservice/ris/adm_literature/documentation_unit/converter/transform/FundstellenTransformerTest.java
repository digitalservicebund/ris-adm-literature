package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.Fundstelle;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical.LegalPeriodical;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical.LegalPeriodicalService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FundstellenTransformerTest {

  @InjectMocks
  private FundstellenTransformer fundstellenTransformer;

  @Mock
  private LegalPeriodicalService legalPeriodicalService;

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
    given(legalPeriodicalService.findLegalPeriodicalsByAbbreviation("BAnz")).willReturn(
      List.of(
        new LegalPeriodical(
          UUID.randomUUID(),
          "BAnz",
          "banz",
          "Bundesanzeiger",
          null,
          "2025, Seite 2"
        )
      )
    );
    given(legalPeriodicalService.findLegalPeriodicalsByAbbreviation("DOK")).willReturn(
      List.of(new LegalPeriodical(UUID.randomUUID(), "DOK", "dok", "Dokument", null, "2020"))
    );

    // when
    List<Fundstelle> fundstellen = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(fundstellen)
      .hasSize(2)
      .extracting(
        f -> f.periodikum().abbreviation(),
        Fundstelle::zitatstelle,
        f -> f.periodikum().title()
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
    List<Fundstelle> fundstellen = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(fundstellen).isEmpty();
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
    given(legalPeriodicalService.findLegalPeriodicalsByAbbreviation("BRD")).willReturn(
      List.of(
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "brd",
          "Bericht aus Deutschland",
          null,
          "2025, Seite 2"
        ),
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "brd-2",
          "Bericht aus Deutschland alt",
          null,
          "1998, Seite 2"
        )
      )
    );

    // when
    List<Fundstelle> fundstellen = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(fundstellen)
      .singleElement()
      .extracting(Fundstelle::ambiguousPeriodikum, Fundstelle::zitatstelle, Fundstelle::periodikum)
      .containsExactly("BRD", "Seite 5", null);
  }

  @Test
  @DisplayName(
    "Transforms one fundstelle with resolved ambiguous legal periodical because of given public id"
  )
  void transform_ambiguousLegalPeriodicalCanBeResolvedByPublicId() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Analysis analysis = new Analysis();
    meta.setAnalysis(analysis);
    analysis.setOtherReferences(
      List.of(createOtherReferenceWithFundstelle("BRD", "brd-2", "BRD Seite 5"))
    );
    given(legalPeriodicalService.findLegalPeriodicalsByAbbreviation("BRD")).willReturn(
      List.of(
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "brd",
          "Bericht aus Deutschland",
          null,
          "2025, Seite 2"
        ),
        new LegalPeriodical(
          UUID.randomUUID(),
          "BRD",
          "brd-2",
          "Bericht aus Deutschland alt",
          null,
          "1998, Seite 2"
        )
      )
    );

    // when
    List<Fundstelle> fundstellen = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(fundstellen)
      .singleElement()
      .extracting(
        f -> f.periodikum().abbreviation(),
        Fundstelle::zitatstelle,
        f -> f.periodikum().title()
      )
      .containsExactly("BRD", "Seite 5", "Bericht aus Deutschland alt");
  }

  @Test
  @DisplayName(
    "Transforms one fundstelle without legal periodical because abbreviation 'BRD' is ambiguous and public id is not found"
  )
  void transform_ambiguousLegalPeriodicalCannotBeResolvedByPublicId() {
    // given
    AkomaNtoso akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = new Meta();
    doc.setMeta(meta);
    Analysis analysis = new Analysis();
    meta.setAnalysis(analysis);
    analysis.setOtherReferences(
      List.of(createOtherReferenceWithFundstelle("DB", "db-3", "DB Seite 7"))
    );
    given(legalPeriodicalService.findLegalPeriodicalsByAbbreviation("DB")).willReturn(
      List.of(
        new LegalPeriodical(UUID.randomUUID(), "DB", "db", "Datenbank", null, "2025, Seite 2"),
        new LegalPeriodical(UUID.randomUUID(), "DB", "db-2", "Datenbank alt", null, "1998, Seite 2")
      )
    );

    // when
    List<Fundstelle> fundstellen = fundstellenTransformer.transform(akomaNtoso);

    // then
    assertThat(fundstellen)
      .singleElement()
      .extracting(Fundstelle::ambiguousPeriodikum, Fundstelle::zitatstelle, Fundstelle::periodikum)
      .containsExactly("DB", "Seite 7", null);
  }

  private OtherReferences createOtherReference(String abbreviation, String citation) {
    OtherReferences otherReferences = new OtherReferences();
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setShowAs(citation);
    implicitReference.setShortForm(abbreviation);
    otherReferences.setImplicitReferences(List.of(implicitReference));
    return otherReferences;
  }

  private OtherReferences createOtherReferenceWithFundstelle(
    String abbreviation,
    String publicId,
    String citation
  ) {
    OtherReferences otherReferences = new OtherReferences();
    ImplicitReference implicitReference = new ImplicitReference();
    implicitReference.setShowAs(citation);
    implicitReference.setShortForm(abbreviation);
    RisFundstelle risFundstelle = new RisFundstelle();
    risFundstelle.setAbbreviation(abbreviation);
    risFundstelle.setZitatstelle(citation);
    risFundstelle.setPublicId(publicId);
    implicitReference.setFundstelle(risFundstelle);
    otherReferences.setImplicitReferences(List.of(implicitReference));
    return otherReferences;
  }
}
