package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import io.hypersistence.utils.hibernate.query.SQLExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
@ActiveProfiles("test")
class FieldOfLawSpecificationIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    SchemaContextHolder.setSchema(SchemaType.ADM);
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  void toPredicate() {
    // given
    FieldOfLawSpecification specification = new FieldOfLawSpecification(null, List.of(), List.of());
    CriteriaBuilder criteriaBuilder = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<FieldOfLawEntity> query = criteriaBuilder.createQuery(FieldOfLawEntity.class);
    Root<FieldOfLawEntity> root = query.from(FieldOfLawEntity.class);

    // when
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql).endsWith("where fole1_0.notation='NEW'");
  }

  @Test
  void toPredicate_withIdentifier() {
    // given
    FieldOfLawSpecification specification = new FieldOfLawSpecification("AR", List.of(), List.of());
    CriteriaBuilder criteriaBuilder = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<FieldOfLawEntity> query = criteriaBuilder.createQuery(FieldOfLawEntity.class);
    Root<FieldOfLawEntity> root = query.from(FieldOfLawEntity.class);

    // when
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql).endsWith(
      "where fole1_0.notation='NEW' and fole1_0.identifier like ? escape ''"
    );
  }

  @Test
  void toPredicate_withTextTerms() {
    // given
    FieldOfLawSpecification specification = new FieldOfLawSpecification(
      "AR",
      List.of("term1", "term2"),
      List.of()
    );
    CriteriaBuilder criteriaBuilder = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<FieldOfLawEntity> query = criteriaBuilder.createQuery(FieldOfLawEntity.class);
    Root<FieldOfLawEntity> root = query.from(FieldOfLawEntity.class);

    // when
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql).endsWith(
      "where fole1_0.notation='NEW' and lower(fole1_0.text) like ? escape '' and lower(fole1_0.text) like ? escape '' and fole1_0.identifier like ? escape ''"
    );
  }

  @Test
  void toPredicate_withNormTerms() {
    // given
    FieldOfLawSpecification specification = new FieldOfLawSpecification(
      "AR",
      List.of(),
      List.of("norm1", "norm2")
    );
    CriteriaBuilder criteriaBuilder = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<FieldOfLawEntity> query = criteriaBuilder.createQuery(FieldOfLawEntity.class);
    Root<FieldOfLawEntity> root = query.from(FieldOfLawEntity.class);

    // when
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .startsWith("select distinct")
      .endsWith(
        """
        where fole1_0.notation='NEW' and fole1_0.identifier like ? escape ''
        and (lower(n1_0.abbreviation) like ? escape '' or lower(n1_0.single_norm_description) like ? escape '')
        and (lower(n1_0.abbreviation) like ? escape ''
        or lower(n1_0.single_norm_description) like ? escape '')
        """.replaceAll("\n", " ").trim()
      );
  }

  @Test
  void toPredicate_withTextAndNormTerms() {
    // given
    FieldOfLawSpecification specification = new FieldOfLawSpecification(
      "AR",
      List.of("term1", "term2"),
      List.of("norm1", "norm2")
    );
    CriteriaBuilder criteriaBuilder = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<FieldOfLawEntity> query = criteriaBuilder.createQuery(FieldOfLawEntity.class);
    Root<FieldOfLawEntity> root = query.from(FieldOfLawEntity.class);

    // when
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .startsWith("select distinct")
      .endsWith(
        """
        where fole1_0.notation='NEW' and lower(fole1_0.text) like ? escape '' and lower(fole1_0.text) like ? escape ''
        and fole1_0.identifier like ? escape '' and (lower(n1_0.abbreviation) like ? escape '' or
        lower(n1_0.single_norm_description) like ? escape '') and (lower(n1_0.abbreviation) like ? escape ''
        or lower(n1_0.single_norm_description) like ? escape '')
        """.replaceAll("\n", " ").trim()
      );
  }
}
