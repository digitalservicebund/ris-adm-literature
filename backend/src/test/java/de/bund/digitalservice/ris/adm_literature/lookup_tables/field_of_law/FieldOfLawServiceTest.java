package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class FieldOfLawServiceTest {

  @InjectMocks
  private FieldOfLawService fieldOfLawService;

  @Mock
  private FieldOfLawRepository fieldOfLawRepository;

  @Test
  void findFieldsOfLawChildren() {
    // given
    FieldOfLawEntity childFieldOfLawEntity = createFieldOfLaw("PR-01", "Phantasierecht allgemein");
    FieldOfLawEntity parentFieldOfLawEntity = createFieldOfLaw("AR", "Phantasierecht");
    parentFieldOfLawEntity.setChildren(Set.of(childFieldOfLawEntity));
    given(fieldOfLawRepository.findByIdentifier("AR")).willReturn(
      Optional.of(parentFieldOfLawEntity)
    );

    // when
    List<FieldOfLaw> fieldsOfLaw = fieldOfLawService.findFieldsOfLawChildren("AR");

    // then
    assertThat(fieldsOfLaw)
      .hasSize(1)
      .extracting(FieldOfLaw::text)
      .containsOnly("Phantasierecht allgemein");
  }

  @Test
  void findFieldsOfLawChildren_noChildren() {
    // given
    FieldOfLawEntity parentFieldOfLawEntity = createFieldOfLaw(
      "PR-01-05",
      "Phantasierecht speziell"
    );
    given(fieldOfLawRepository.findByIdentifier("PR-01-05")).willReturn(
      Optional.of(parentFieldOfLawEntity)
    );

    // when
    List<FieldOfLaw> fieldsOfLaw = fieldOfLawService.findFieldsOfLawChildren("PR-01-05");

    // then
    assertThat(fieldsOfLaw).isEmpty();
  }

  @Test
  void findFieldsOfLawChildren_identifierNotFound() {
    // given
    given(fieldOfLawRepository.findByIdentifier("BR")).willReturn(Optional.empty());

    // when
    List<FieldOfLaw> fieldsOfLaw = fieldOfLawService.findFieldsOfLawChildren("BR");

    // then
    assertThat(fieldsOfLaw).isEmpty();
  }

  @Test
  void findFieldsOfLawParents() {
    // given
    FieldOfLawEntity parentFieldOfLawEntity = createFieldOfLaw("AR", "Phantasierecht");
    given(fieldOfLawRepository.findByParentIsNullAndNotationOrderByIdentifier("NEW")).willReturn(
      List.of(parentFieldOfLawEntity)
    );

    // when
    List<FieldOfLaw> fieldsOfLaw = fieldOfLawService.findFieldsOfLawParents();

    // then
    assertThat(fieldsOfLaw).hasSize(1).extracting(FieldOfLaw::text).containsOnly("Phantasierecht");
  }

  @Test
  void findFieldOfLaw() {
    // given
    FieldOfLawEntity fieldOfLawEntity = createFieldOfLaw("AR", "Phantasierecht");
    given(fieldOfLawRepository.findByIdentifier("AR")).willReturn(Optional.of(fieldOfLawEntity));

    // when
    Optional<FieldOfLaw> actualFieldOfLaw = fieldOfLawService.findFieldOfLaw("AR");

    // then
    assertThat(actualFieldOfLaw)
      .isPresent()
      .hasValueSatisfying(fieldOfLaw -> assertThat(fieldOfLaw.text()).isEqualTo("Phantasierecht"));
  }

  @Test
  void findFieldOfLaw_notFound() {
    // given
    given(fieldOfLawRepository.findByIdentifier("BR")).willReturn(Optional.empty());

    // when
    Optional<FieldOfLaw> actualFieldOfLaw = fieldOfLawService.findFieldOfLaw("BR");

    // then
    assertThat(actualFieldOfLaw).isEmpty();
  }

  @Test
  void findFieldsOfLaw() {
    // given
    FieldOfLawQuery query = new FieldOfLawQuery(
      "PR-05",
      "arbeit",
      "PStG",
      new QueryOptions(0, 10, "identifier", Sort.Direction.ASC, true)
    );
    given(
      fieldOfLawRepository.findAll(any(FieldOfLawSpecification.class), any(Pageable.class))
    ).willReturn(
      new PageImpl<>(
        List.of(
          createFieldOfLaw("PR-05", "Beendigung der Phantasieverhältnisse"),
          createFieldOfLaw("BR-05", "Bericht")
        )
      )
    );

    // when
    var result = fieldOfLawService.findFieldsOfLaw(query);

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(FieldOfLaw::text)
      .containsOnly("Beendigung der Phantasieverhältnisse", "Bericht");
  }

  @Test
  void findFieldsOfLaw_byNormOnly() {
    // given
    FieldOfLawQuery query = new FieldOfLawQuery(
      null,
      null,
      "PStG",
      new QueryOptions(0, 10, "identifier", Sort.Direction.ASC, true)
    );
    given(
      fieldOfLawRepository.findAll(any(FieldOfLawSpecification.class), any(Pageable.class))
    ).willReturn(
      new PageImpl<>(List.of(createFieldOfLaw("PR-05", "Beendigung der Phantasieverhältnisse")))
    );

    // when
    var result = fieldOfLawService.findFieldsOfLaw(query);

    // then
    assertThat(result.content())
      .hasSize(1)
      .extracting(FieldOfLaw::text)
      .containsOnly("Beendigung der Phantasieverhältnisse");
  }

  @Test
  void findFieldsOfLaw_noResults() {
    // given
    FieldOfLawQuery query = new FieldOfLawQuery(
      "IDENTIFIER-UNKNOWN",
      "arbeitsbeschaffungsmaßnahmengegenentwurf",
      null,
      new QueryOptions(0, 10, "identifier", Sort.Direction.ASC, true)
    );
    given(
      fieldOfLawRepository.findAll(any(FieldOfLawSpecification.class), any(Pageable.class))
    ).willReturn(new PageImpl<>(List.of()));

    // when
    var result = fieldOfLawService.findFieldsOfLaw(query);

    // then
    assertThat(result.content()).isEmpty();
  }

  private FieldOfLawEntity createFieldOfLaw(String identifier, String text) {
    FieldOfLawEntity fieldOfLawEntity = new FieldOfLawEntity();
    fieldOfLawEntity.setId(UUID.randomUUID());
    fieldOfLawEntity.setIdentifier(identifier);
    fieldOfLawEntity.setText(text);
    fieldOfLawEntity.setNotation("NEW");
    FieldOfLawNormEntity normEntity = new FieldOfLawNormEntity();
    normEntity.setId(UUID.randomUUID());
    normEntity.setAbbreviation("PStG");
    normEntity.setSingleNormDescription("§ 99");
    fieldOfLawEntity.getNorms().add(normEntity);
    return fieldOfLawEntity;
  }
}
