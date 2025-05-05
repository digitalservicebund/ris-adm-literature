package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImplicitReferenceTest {

  private static Stream<Arguments> implicitReferences() {
    ImplicitReference fundstelle = new ImplicitReference();
    ImplicitReference normReference = new ImplicitReference();
    normReference.setNormReference(new RisNormReference());
    ImplicitReference caselawReference = new ImplicitReference();
    caselawReference.setCaselawReference(new RisCaselawReference());
    return Stream.of(
      Arguments.of(fundstelle, ImplicitReferenceType.FUNDSTELLE),
      Arguments.of(normReference, ImplicitReferenceType.NORM),
      Arguments.of(caselawReference, ImplicitReferenceType.CASELAW)
    );
  }

  @ParameterizedTest
  @MethodSource("implicitReferences")
  void getReferenceType(ImplicitReference implicitReference, ImplicitReferenceType expected) {
    // given

    // when
    ImplicitReferenceType referenceType = implicitReference.getReferenceType();

    // then
    assertThat(referenceType).isEqualTo(expected);
  }
}
