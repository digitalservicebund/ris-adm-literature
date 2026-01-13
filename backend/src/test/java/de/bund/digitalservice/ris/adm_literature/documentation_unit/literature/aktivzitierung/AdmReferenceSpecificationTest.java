package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import static org.assertj.core.api.Assertions.assertThat;

import io.hypersistence.utils.hibernate.query.SQLExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class AdmReferenceSpecificationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("toPredicate with no parameters should generate select with no real filters")
  void toPredicate_withNoParameters() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    String whereClause = sql.substring(sql.toLowerCase().lastIndexOf("where"));
    assertThat(whereClause).isEqualToIgnoringCase("where 1=1");
  }

  @Test
  @DisplayName("toPredicate with documentNumber should add like clause")
  void toPredicate_withDocumentNumberOnly() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      "DOC-1",
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    assertThat(sql).contains("where").contains("lower(are1_0.document_number) like ?");
  }

  @Test
  @DisplayName("toPredicate with periodikum should add like clause on reference view")
  void toPredicate_withPeriodikumOnly() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      null,
      "BGBI",
      null,
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    assertThat(sql).contains("lower(are1_0.fundstellen_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with fundstelle should add like clause on reference view")
  void toPredicate_withZitatstelleOnly() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      null,
      null,
      "Seite 2",
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    assertThat(sql).contains("lower(are1_0.fundstellen_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with aktenzeichen should use combined field")
  void toPredicate_withAktenzeichenOnly() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      null,
      null,
      null,
      null,
      "AZ-123",
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    assertThat(sql).contains("lower(are1_0.aktenzeichen_list_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with normgeber should use combined field")
  void toPredicate_withNormgeberOnly() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      null,
      null,
      null,
      null,
      null,
      null,
      "BMJ",
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );
    assertThat(sql).contains("lower(are1_0.normgeber_list_combined) like ?");
  }

  @Test
  @DisplayName("toPredicate with all parameters should combine all clauses correctly")
  void toPredicate_withAllParameters() {
    // given
    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      "DOC-1",
      "BGBI",
      "S5",
      "2024",
      "AZ-1",
      "Type-A",
      "BMJ",
      "2025-01-01"
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<AdmReferenceEntity> query = cb.createQuery(AdmReferenceEntity.class);
    Root<AdmReferenceEntity> root = query.from(AdmReferenceEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, cb);

    // then
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    assertThat(sql)
      .contains("lower(are1_0.document_number) like ?")
      .contains("and lower(are1_0.fundstellen_combined) like ?")
      .contains("and lower(are1_0.inkrafttretedatum) like ?")
      .contains("and lower(are1_0.aktenzeichen_list_combined) like ?")
      .contains("and lower(are1_0.dokumenttyp) like ?")
      .contains("and lower(are1_0.normgeber_list_combined) like ?")
      .contains("and lower(are1_0.zitierdaten_combined) like ?");
  }
}
