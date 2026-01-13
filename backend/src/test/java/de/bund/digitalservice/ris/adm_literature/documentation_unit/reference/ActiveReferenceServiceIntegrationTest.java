package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.List;
import org.assertj.core.groups.Tuple;
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
class ActiveReferenceServiceIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ActiveReferenceService activeReferenceService;

  @Test
  @DisplayName("Saves two active references and reads them from the database")
  void publish() {
    // given
    // Documentation units KALS999999999, KSNR999999999, and KSNR999999998 are already created by data initialization

    // when
    activeReferenceService.publish(
      new DocumentReference("KALS999999999", DocumentCategory.LITERATUR_SELBSTAENDIG),
      List.of(
        new DocumentReference("KSNR999999999", DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        new DocumentReference("KSNR999999998", DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
      )
    );

    // then
    List<ActiveReferenceEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery("select ar from ActiveReferenceEntity ar", ActiveReferenceEntity.class)
      .getResultList();
    assertThat(activeReferenceEntities)
      .extracting(
        ar -> ar.getSource().getLiteratureDocumentNumber(),
        ar -> ar.getTarget().getAdmDocumentNumber()
      )
      .contains(
        Tuple.tuple("KALS999999999", "KSNR999999999"),
        Tuple.tuple("KALS999999999", "KSNR999999998")
      );
  }

  @Test
  @DisplayName(
    "Saves one new active reference and deletes one existing and reads them from the database"
  )
  void publish_update() {
    // given
    // Documentation units KALS999999980, KSNR999999981, KSNR999999982, and KSNR999999983 are
    // already created by data initialization.
    activeReferenceService.publish(
      new DocumentReference("KALS999999980", DocumentCategory.LITERATUR_SELBSTAENDIG),
      List.of(
        new DocumentReference("KSNR999999981", DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        new DocumentReference("KSNR999999982", DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
      )
    );

    // when
    activeReferenceService.publish(
      new DocumentReference("KALS999999980", DocumentCategory.LITERATUR_SELBSTAENDIG),
      List.of(
        new DocumentReference("KSNR999999981", DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        new DocumentReference("KSNR999999983", DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
      )
    );

    // then
    List<ActiveReferenceEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery("select ar from ActiveReferenceEntity ar", ActiveReferenceEntity.class)
      .getResultList();
    assertThat(activeReferenceEntities)
      .extracting(
        ar -> ar.getSource().getLiteratureDocumentNumber(),
        ar -> ar.getTarget().getAdmDocumentNumber()
      )
      .contains(
        Tuple.tuple("KALS999999980", "KSNR999999981"),
        Tuple.tuple("KALS999999980", "KSNR999999983")
      );
  }

  @Test
  @DisplayName("Deletes active references on calling publish with empty target list")
  void publish_deleteOnly() {
    // given
    // Documentation units KALS999999996, KSNR999999996, and KSNR999999995 are already created by data initialization
    activeReferenceService.publish(
      new DocumentReference("KALS999999996", DocumentCategory.LITERATUR_SELBSTAENDIG),
      List.of(
        new DocumentReference("KSNR999999996", DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        new DocumentReference("KSNR999999995", DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
      )
    );

    // when
    activeReferenceService.publish(
      new DocumentReference("KALS999999996", DocumentCategory.LITERATUR_SELBSTAENDIG),
      List.of()
    );

    // then
    List<ActiveReferenceEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery(
        "select ar from ActiveReferenceEntity ar where ar.source.literatureDocumentNumber = 'KALS999999996'",
        ActiveReferenceEntity.class
      )
      .getResultList();
    assertThat(activeReferenceEntities).isEmpty();
  }
}
