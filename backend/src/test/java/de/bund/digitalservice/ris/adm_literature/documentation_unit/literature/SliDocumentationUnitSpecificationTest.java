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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class SliDocumentationUnitSpecificationTest {

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    SchemaContextHolder.setSchema(SchemaType.LIT);
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  @DisplayName("toPredicate with no parameters should only filter by category, no joins")
  void toPredicate_withNoParameters() {
    // given
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      null,
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("documentation_unit_type=?")
      .doesNotContain("join")
      .doesNotContain("like");
  }

  @Test
  @DisplayName("toPredicate with documentNumber should add like clause and no join")
  void toPredicate_withDocumentNumberOnly() {
    // given
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      "123",
      null,
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("where")
      .contains("lower(due1_0.document_number) like ?")
      .doesNotContain("join");
  }

  @Test
  @DisplayName("toPredicate with veroeffentlichungsjahr should add like clause and left join")
  void toPredicate_withVeroeffentlichungsjahrOnly() {
    // given
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      null,
      "2023",
      null,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(dui1_0.veroeffentlichungsjahr) like ?");
  }

  @Test
  @DisplayName("toPredicate with title should add like clause and left join")
  void toPredicate_withTitleOnly() {
    // given
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      null,
      null,
      null,
      "My Title",
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(dui1_0.titel) like ?");
  }

  @Test
  @DisplayName("toPredicate with multiple dokumenttypen should use AND logic")
  void toPredicate_withMultipleDokumenttypen_shouldUseAndLogic() {
    // given
    List<String> types = List.of("Buch", "Artikel");
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      null,
      null,
      types,
      null,
      null
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains(
        "lower(dui1_0.dokumenttypen) like ? escape '' and lower(dui1_0.dokumenttypen) like ? escape ''"
      );
  }

  @Test
  @DisplayName("toPredicate with multiple verfasser should use AND logic")
  void toPredicate_withMultipleVerfasser_shouldUseAndLogic() {
    // given
    List<String> authors = List.of("Müller", "Schmidt");
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      null,
      null,
      null,
      null,
      authors
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains(
        "lower(dui1_0.verfasser) like ? escape '' and lower(dui1_0.verfasser) like ? escape ''"
      );
  }

  @Test
  @DisplayName("toPredicate with all parameters should combine all clauses correctly")
  void toPredicate_withAllParameters() {
    // given
    SliDocumentationUnitSpecification spec = new SliDocumentationUnitSpecification(
      "123",
      "2023",
      List.of("Buch"),
      "Titel",
      List.of("Author")
    );
    CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<DocumentationUnitEntity> query = cb.createQuery(DocumentationUnitEntity.class);
    Root<DocumentationUnitEntity> root = query.from(DocumentationUnitEntity.class);

    // when
    Predicate predicate = spec.toPredicate(root, query, cb);
    String sql = SQLExtractor.from(
      entityManager.getEntityManager().createQuery(query.where(predicate))
    );

    // then
    assertThat(sql)
      .contains("left join documentation_unit_index")
      .contains("lower(due1_0.document_number) like ?")
      .contains("and lower(dui1_0.veroeffentlichungsjahr) like ?")
      .contains("and lower(dui1_0.titel) like ?")
      .contains("and lower(dui1_0.dokumenttypen) like ?")
      .contains("and lower(dui1_0.verfasser) like ?");
  }
}
