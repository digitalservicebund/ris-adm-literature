package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import io.hypersistence.utils.hibernate.query.SQLExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
class UliDocumentationUnitSpecificationTest {

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    SchemaContextHolder.setSchema(SchemaType.LITERATURE);
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  @DisplayName("Should always include base filters (ULI type and published XML)")
  void toPredicate_shouldIncludeBaseFilters() {
    UliDocumentationUnitSpecification spec = new UliDocumentationUnitSpecification(
      null,
      null,
      null,
      null,
      null
    );

    String sql = getGeneratedSql(spec);

    assertThat(sql).contains("documentation_unit_type=?");
    assertThat(sql).contains("xml is not null");
  }

  @ParameterizedTest(name = "Field {0} should add SQL fragment: {2}")
  @MethodSource("provideFilterScenarios")
  @DisplayName("toPredicate with specific filters should add corresponding like clauses")
  void toPredicate_withFilters(
    String fieldName,
    UliDocumentationUnitSpecification spec,
    String expectedSqlFragment
  ) {
    String sql = getGeneratedSql(spec);

    assertThat(sql.toLowerCase()).contains(expectedSqlFragment.toLowerCase());
  }

  private static Stream<Arguments> provideFilterScenarios() {
    return Stream.of(
      Arguments.of(
        "documentNumber",
        new UliDocumentationUnitSpecification("DOC-123", null, null, null, null),
        "lower(due1_0.document_number) like ?"
      ),
      Arguments.of(
        "periodikum",
        new UliDocumentationUnitSpecification(null, "BB", null, null, null),
        "lower(dui1_0.fundstellen_combined) like ?"
      ),
      Arguments.of(
        "zitatstelle",
        new UliDocumentationUnitSpecification(null, null, "S. 100", null, null),
        "lower(dui1_0.fundstellen_combined) like ?"
      ),
      Arguments.of(
        "dokumenttypen",
        new UliDocumentationUnitSpecification(null, null, null, List.of("Aufsatz"), null),
        "lower(dui1_0.dokumenttypen_combined) like ?"
      ),
      Arguments.of(
        "verfasser",
        new UliDocumentationUnitSpecification(null, null, null, null, List.of("Bib")),
        "lower(dui1_0.verfasser_list_combined) like ?"
      )
    );
  }

  @Test
  @DisplayName("Should combine multiple collection values (AND logic)")
  void toPredicate_withMultipleAuthors() {
    UliDocumentationUnitSpecification spec = new UliDocumentationUnitSpecification(
      null,
      null,
      null,
      null,
      List.of("Müller", "Schmidt")
    );

    String sql = getGeneratedSql(spec);

    long count = sql.toLowerCase().codePoints().filter(ch -> ch == '?').count();
    assertThat(count).isGreaterThanOrEqualTo(2);
    assertThat(sql.toLowerCase()).contains("verfasser_list_combined) like ?").contains("and");
  }

  @Test
  @DisplayName("toPredicate with all parameters should include JOIN and all where clauses")
  void toPredicate_withAllParametersCombined() {
    // given
    UliDocumentationUnitSpecification spec = new UliDocumentationUnitSpecification(
      "DOC-123",
      "BB",
      "S. 10",
      List.of("Aufsatz"),
      List.of("Schmidt")
    );

    String sql = getGeneratedSql(spec);

    // then
    assertThat(sql).containsIgnoringCase("left join documentation_unit_index");
    assertThat(sql).contains("documentation_unit_type=?");
    assertThat(sql).contains("xml is not null");

    assertThat(sql).contains("lower(due1_0.document_number) like ?");
    assertThat(sql).contains("lower(dui1_0.fundstellen_combined) like ?");
    assertThat(sql).contains("lower(dui1_0.dokumenttypen_combined) like ?");
    assertThat(sql).contains("lower(dui1_0.verfasser_list_combined) like ?");
  }

  private String getGeneratedSql(UliDocumentationUnitSpecification spec) {
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    Predicate predicate = spec.toPredicate(root, query, cb);

    return SQLExtractor.from(entityManager.getEntityManager().createQuery(query.where(predicate)));
  }
}
