package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaContextHolder;
import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitService;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
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

  @Autowired
  private DocumentationUnitService documentationUnitService;

  @BeforeAll
  static void setUp() {
    SchemaContextHolder.setSchema(SchemaType.LITERATURE);
  }

  @Test
  @DisplayName("Saves two active references adm and reads them from the database")
  void publishAktivzitierungAdm() {
    // given
    DocumentationUnit kals999999999 = documentationUnitService
      .findByDocumentNumber("KALS999999999")
      .orElseThrow();

    // when
    activeReferenceService.publishAktivzitierungAdm(
      kals999999999,
      List.of(
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999999", "", "", "", "", "", "", ""),
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999998", "", "", "", "", "", "", "")
      )
    );

    // then
    List<ActiveReferenceAdmEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery("select ar from ActiveReferenceAdmEntity ar", ActiveReferenceAdmEntity.class)
      .getResultList();
    assertThat(activeReferenceEntities)
      .extracting(
        ar -> ar.getSourceDocumentationUnit().getDocumentNumber(),
        ActiveReferenceAdmEntity::getTargetDocumentNumber,
        ActiveReferenceAdmEntity::getNormgeber
      )
      .contains(
        Tuple.tuple("KALS999999999", "KSNR999999999", "Erste Jurpn"),
        Tuple.tuple("KALS999999999", "KSNR999999998", "Erste Jurpn")
      );
  }

  @Test
  @DisplayName(
    "Saves one new active reference adm and deletes one existing and reads them from the database"
  )
  void publishAktivzitierungAdm_update() {
    // given
    // Documentation units KALS999999980, KSNR999999981, KSNR999999982, and KSNR999999983 are
    // already created by data initialization.
    DocumentationUnit kals999999980 = documentationUnitService
      .findByDocumentNumber("KALS999999980")
      .orElseThrow();
    activeReferenceService.publishAktivzitierungAdm(
      kals999999980,
      List.of(
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999981", "", "", "", "", "", "", ""),
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999982", "", "", "", "", "", "", "")
      )
    );

    // when
    activeReferenceService.publishAktivzitierungAdm(
      kals999999980,
      List.of(
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999981", "", "", "", "", "", "", ""),
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999983", "", "", "", "", "", "", "")
      )
    );

    // then
    List<ActiveReferenceAdmEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery("select ar from ActiveReferenceAdmEntity ar", ActiveReferenceAdmEntity.class)
      .getResultList();
    assertThat(activeReferenceEntities)
      .extracting(
        ar -> ar.getSourceDocumentationUnit().getDocumentNumber(),
        ActiveReferenceAdmEntity::getTargetDocumentNumber
      )
      .contains(
        Tuple.tuple("KALS999999980", "KSNR999999981"),
        Tuple.tuple("KALS999999980", "KSNR999999983")
      );
  }

  @Test
  @DisplayName("Deletes active references adm on calling publish with empty target list")
  void publish_deleteOnly() {
    // given
    // Documentation units KALS999999996, KSNR999999996, and KSNR999999995 are already created by data initialization
    DocumentationUnit kals999999996 = documentationUnitService
      .findByDocumentNumber("KALS999999996")
      .orElseThrow();
    activeReferenceService.publishAktivzitierungAdm(
      kals999999996,
      List.of(
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999996", "", "", "", "", "", "", ""),
        new AktivzitierungAdm(UUID.randomUUID(), "KSNR999999995", "", "", "", "", "", "", "")
      )
    );

    // when
    activeReferenceService.publishAktivzitierungAdm(kals999999996, List.of());

    // then
    List<ActiveReferenceAdmEntity> activeReferenceEntities = entityManager
      .getEntityManager()
      .createQuery(
        "select ar from ActiveReferenceAdmEntity ar where ar.sourceDocumentationUnit.documentNumber = 'KALS999999996'",
        ActiveReferenceAdmEntity.class
      )
      .getResultList();
    assertThat(activeReferenceEntities).isEmpty();
  }
}
