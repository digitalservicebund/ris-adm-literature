package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.InstitutionType;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InstitutionTypeMapperTest {

  private static Stream<Arguments> institutionTypes() {
    return Stream.of(
      Arguments.of(InstitutionType.LEGAL_ENTITY, "jurpn"),
      Arguments.of(InstitutionType.INSTITUTION, "organ")
    );
  }

  @ParameterizedTest
  @MethodSource("institutionTypes")
  void mapInstitutionType(InstitutionType institutionType, String expected) {
    // given

    // when
    String type = InstitutionTypeMapper.mapInstitutionType(institutionType);

    // then
    assertThat(type).isEqualTo(expected);
  }

  private static Stream<Arguments> institutionTypesFromString() {
    return Stream.of(
      Arguments.of("jurpn", InstitutionType.LEGAL_ENTITY),
      Arguments.of("organ", InstitutionType.INSTITUTION)
    );
  }

  @ParameterizedTest
  @MethodSource("institutionTypesFromString")
  void mapInstitutionTypeString(String type, InstitutionType expected) {
    // given

    // when
    InstitutionType institutionType = InstitutionTypeMapper.mapInstitutionTypeString(type);

    // then
    assertThat(institutionType).isEqualTo(expected);
  }
}
