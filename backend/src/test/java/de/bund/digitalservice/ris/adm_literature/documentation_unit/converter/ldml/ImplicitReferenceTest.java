package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImplicitReferenceTest {

  private static Stream<Arguments> implicitReferences() {
    ImplicitReference fundstelle = new ImplicitReference();
    ImplicitReference activeReference = new ImplicitReference();
    activeReference.setNormReference(new RisNormReference());
    ImplicitReference activeCitation = new ImplicitReference();
    activeCitation.setCaselawReference(new RisCaselawReference());
    return Stream.of(
      Arguments.of(fundstelle, ImplicitReferenceType.FUNDSTELLE),
      Arguments.of(activeReference, ImplicitReferenceType.ACTIVE_REFERENCE),
      Arguments.of(activeCitation, ImplicitReferenceType.ACTIVE_CITATION)
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
