package de.bund.digitalservice.ris.adm_literature.documentation_unit.passivzitierung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung.LiteratureReferenceEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung.LiteratureReferenceRepository;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.DocumentReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.PassiveReference;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.PassiveReferenceService;
import de.bund.digitalservice.ris.adm_literature.test.TestFile;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
class UpdatePassivzitierungJobIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UpdatePassivzitierungJob updatePassivzitierungJob;

  @MockitoBean
  private PassiveReferenceService passiveReferenceService;

  @MockitoBean
  private LiteratureReferenceRepository literatureReferenceRepository;

  @Test
  void execute() {
    // given
    DocumentationUnitEntity admDocumentationUnitEntity = new DocumentationUnitEntity();
    admDocumentationUnitEntity.setDocumentNumber("KSNR777777777");
    admDocumentationUnitEntity.setXml(TestFile.readFileToString("adm/ldml-example.akn.xml"));
    admDocumentationUnitEntity.setDocumentationUnitType(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    admDocumentationUnitEntity.setDocumentationOffice(DocumentationOffice.BSG);
    admDocumentationUnitEntity = entityManager.persistFlushFind(admDocumentationUnitEntity);
    DocumentationUnitEntity sliDocumentationUnitEntity = new DocumentationUnitEntity();
    sliDocumentationUnitEntity.setDocumentNumber("KVLS2025000009");
    sliDocumentationUnitEntity.setXml(
      TestFile.readFileToString("literature/sli/ldml-example.akn.xml")
    );
    sliDocumentationUnitEntity.setDocumentationUnitType(DocumentCategory.LITERATUR_SELBSTAENDIG);
    sliDocumentationUnitEntity.setDocumentationOffice(DocumentationOffice.BVERFG);
    sliDocumentationUnitEntity = entityManager.persistFlushFind(sliDocumentationUnitEntity);
    given(passiveReferenceService.findAll(DocumentCategory.VERWALTUNGSVORSCHRIFTEN)).willReturn(
      List.of(
        new PassiveReference(
          new DocumentReference(
            admDocumentationUnitEntity.getDocumentNumber(),
            DocumentCategory.VERWALTUNGSVORSCHRIFTEN
          ),
          new DocumentReference(
            sliDocumentationUnitEntity.getDocumentNumber(),
            DocumentCategory.LITERATUR_SELBSTAENDIG
          )
        )
      )
    );
    LiteratureReferenceEntity literatureReferenceEntity = new LiteratureReferenceEntity();
    literatureReferenceEntity.setDocumentNumber(sliDocumentationUnitEntity.getDocumentNumber());
    literatureReferenceEntity.setDocumentationOffice(
      sliDocumentationUnitEntity.getDocumentationOffice()
    );
    LiteratureIndex literatureIndex = literatureReferenceEntity.getLiteratureIndex();
    literatureIndex.setDokumenttypen(List.of("Bib", "Dis"));
    literatureIndex.setTitel("Hauptsache Titel");
    literatureIndex.setVeroeffentlichungsjahr("2025");
    literatureIndex.setVerfasserList(List.of("Name, Vorname"));
    given(
      literatureReferenceRepository.findById(sliDocumentationUnitEntity.getDocumentNumber())
    ).willReturn(Optional.of(literatureReferenceEntity));

    // when
    updatePassivzitierungJob.execute();

    // then
    assertThat(
      entityManager.find(DocumentationUnitEntity.class, admDocumentationUnitEntity.getId())
    )
      .isNotNull()
      .extracting(DocumentationUnitEntity::getXml)
      .asInstanceOf(InstanceOfAssertFactories.STRING)
      .contains(
        """
        <akn:implicitReference>
            <ris:referenz domainTerm="Referenz">
                <ris:richtung domainTerm="Richtung der Referenzierung">passiv</ris:richtung>
                <ris:referenzArt domainTerm="Art der Referenz">sli</ris:referenzArt>
                <ris:dokumentnummer domainTerm="Dokumentnummer">KVLS2025000009</ris:dokumentnummer>
                <ris:relativerPfad domainTerm="Pfad zur Referenz">/literature/KVLS2025000009.xml</ris:relativerPfad>
                <ris:dokumenttypAbkuerzung domainTerm="Dokumenttyp, abgekürzt">Bib, Dis</ris:dokumenttypAbkuerzung>
                <ris:dokumenttyp domainTerm="Dokumenttyp">Bibliographie, Dissertation</ris:dokumenttyp>
                <ris:titel domainTerm="Titel">Hauptsache Titel</ris:titel>
                <ris:autor domainTerm="Autor(en)">Name, Vorname</ris:autor>
                <ris:veroeffentlichungsjahr domainTerm="Veröffentlichungsjahr">2025</ris:veroeffentlichungsjahr>
                <ris:standardzusammenfassung>Name, Vorname, Hauptsache Titel, Bibliographie, Dissertation, 2025</ris:standardzusammenfassung>
            </ris:referenz>
        </akn:implicitReference>""".indent(20)
      );
  }
}
