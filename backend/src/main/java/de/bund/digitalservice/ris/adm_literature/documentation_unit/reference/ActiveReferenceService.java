package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitRepository;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Active reference service for writing active references.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ActiveReferenceService {

  private final RefViewAdmRepository refViewAdmRepository;
  private final ActiveReferenceAdmRepository activeReferenceAdmRepository;
  private final DocumentationUnitRepository documentationUnitRepository;

  /**
   * Publishes for a given source document the given adm targets as active references. If the given targets
   * are empty, this method removes all active references to adm for the given source.
   * <p>
   * <b>IMPORTANT</b>: This method must be called once per documentation unit with all active references to adm given.
   * On each method call, at first all found active references to adm of the given source are deleted.
   * Calling this method for the same documentation unit with different targets (for example, one call
   * for an ADM reference to documentation unit A, second call for an ADM reference to documentation unit B)
   * results into loose of active reference to documentation unit A.
   * </p>
   *
   * @param sourceDocumentationUnit The source documentation unit
   * @param targets                 The list of 'Aktivzitierung Adm'
   * @return List of updated targets. If a target links an existing document, then the metadata are taken from the
   *         target documentation unit view.
   */
  public List<AktivzitierungAdm> publishAktivzitierungAdm(
    @NonNull DocumentationUnit sourceDocumentationUnit,
    @NonNull List<AktivzitierungAdm> targets
  ) {
    DocumentationUnitEntity source = documentationUnitRepository.getReferenceById(
      sourceDocumentationUnit.id()
    );
    // # 1. Delete all active references which are not given anymore or which are blind links
    List<Aktivzitierung> targetAktivzitierungen = targets
      .stream()
      .map(aa -> new Aktivzitierung(aa.citationType(), aa.documentNumber()))
      .toList();
    List<ActiveReferenceAdmEntity> existingActiveReferences =
      activeReferenceAdmRepository.findBySourceDocumentationUnit(source);
    List<ActiveReferenceAdmEntity> activeReferenceAdmEntitiesToDelete = existingActiveReferences
      .stream()
      // Filter from existing ones all blind links (will be deleted anyway) and real links which are
      // not included in the given target list (deleted by the user).
      .filter(ar -> {
        Aktivzitierung aktivzitierung = new Aktivzitierung(ar);
        return aktivzitierung.isBlindlink() || !targetAktivzitierungen.contains(aktivzitierung);
      })
      .toList();
    // Results into SQL: delete from active_reference where id in ()
    activeReferenceAdmRepository.deleteAllInBatch(activeReferenceAdmEntitiesToDelete);
    if (targets.isEmpty()) {
      log.info(
        "Deleted active references adm for source {}.",
        sourceDocumentationUnit.documentNumber()
      );
      return List.of();
    }
    // Flush needed before inserting new active references
    activeReferenceAdmRepository.flush();
    // # 2. Create new active references. Update existing active references if needed (real links only).
    List<ActiveReferenceAdmEntity> activeReferencesToUpdate = new ArrayList<>(
      existingActiveReferences
    );
    activeReferencesToUpdate.removeAll(activeReferenceAdmEntitiesToDelete);
    Map<Aktivzitierung, ActiveReferenceAdmEntity> activeReferencesToUpdateByAktivzitierung =
      activeReferencesToUpdate
        .stream()
        .collect(Collectors.toMap(Aktivzitierung::new, Function.identity()));
    List<ActiveReferenceAdmEntity> activeReferenceEntitiesToCreateOrUpdate = targets
      .stream()
      .map(target -> {
        // Find or create the active reference adm
        ActiveReferenceAdmEntity activeReferenceAdmEntity =
          activeReferencesToUpdateByAktivzitierung.getOrDefault(
            new Aktivzitierung(target),
            new ActiveReferenceAdmEntity()
          );
        activeReferenceAdmEntity.setSourceDocumentationUnit(source);
        activeReferenceAdmEntity.setZitierart(target.citationType());
        String targetDocumentNumber = target.documentNumber();
        if (targetDocumentNumber != null) {
          activeReferenceAdmEntity.setTargetDocumentNumber(targetDocumentNumber);
          refViewAdmRepository
            .findById(targetDocumentNumber)
            .ifPresentOrElse(
              rvae -> {
                AdmIndex admIndex = rvae.getAdmIndex();
                activeReferenceAdmEntity.setTargetDocumentationUnitId(rvae.getId());
                activeReferenceAdmEntity.setNormgeber(admIndex.getNormgeberList().getFirst());
                activeReferenceAdmEntity.setInkrafttretedatum(admIndex.getInkrafttretedatum());
                activeReferenceAdmEntity.setAktenzeichen(admIndex.getAktenzeichenList().getFirst());
                activeReferenceAdmEntity.setFundstelle(rvae.getFundstellen().getFirst());
                activeReferenceAdmEntity.setDokumenttyp(admIndex.getDokumenttyp());
              },
              () -> {
                // If a (former) published documentation unit is not found anymore, we have to convert the active
                // reference to a blind link.
                activeReferenceAdmEntity.setTargetDocumentationUnitId(null);
                activeReferenceAdmEntity.setTargetDocumentNumber(null);
                mapTargetToBlindlink(activeReferenceAdmEntity, target);
                log.info(
                  "Target document number {} not found, convert active reference to blindlink.",
                  targetDocumentNumber
                );
              }
            );
        } else {
          // Add a blind link
          mapTargetToBlindlink(activeReferenceAdmEntity, target);
        }
        return activeReferenceAdmEntity;
      })
      .toList();
    // # 3. Saves all created/updated active references
    activeReferenceAdmRepository.saveAll(activeReferenceEntitiesToCreateOrUpdate);
    log.info(
      "Saved {} created/updated active references for source {}.",
      activeReferenceEntitiesToCreateOrUpdate.size(),
      sourceDocumentationUnit.documentNumber()
    );
    return activeReferenceEntitiesToCreateOrUpdate
      .stream()
      .map(ar ->
        new AktivzitierungAdm(
          UUID.randomUUID(),
          ar.getTargetDocumentNumber(),
          ar.getZitierart(),
          StringUtils.substringBefore(ar.getFundstelle(), ","),
          StringUtils.substringAfter(ar.getFundstelle(), ", "),
          ar.getInkrafttretedatum(),
          ar.getAktenzeichen(),
          ar.getDokumenttyp(),
          ar.getNormgeber()
        )
      )
      .toList();
  }

  private void mapTargetToBlindlink(
    ActiveReferenceAdmEntity activeReferenceAdmEntity,
    AktivzitierungAdm target
  ) {
    activeReferenceAdmEntity.setNormgeber(target.normgeber());
    activeReferenceAdmEntity.setInkrafttretedatum(target.inkrafttretedatum());
    activeReferenceAdmEntity.setAktenzeichen(target.aktenzeichen());
    activeReferenceAdmEntity.setFundstelle(target.periodikum() + ", " + target.zitatstelle());
    activeReferenceAdmEntity.setDokumenttyp(target.dokumenttyp());
  }

  record Aktivzitierung(@NonNull String zitierart, String documentNumber) {
    Aktivzitierung(ActiveReferenceAdmEntity activeReferenceAdmEntity) {
      this(
        activeReferenceAdmEntity.getZitierart(),
        activeReferenceAdmEntity.getTargetDocumentNumber()
      );
    }

    Aktivzitierung(AktivzitierungAdm aktivzitierungAdm) {
      this(aktivzitierungAdm.citationType(), aktivzitierungAdm.documentNumber());
    }

    /**
     * Returns whether this Aktivizitierung is a blind link or not.
     * @return {@code true} if this instance is a blind link, {@code false} otherwise
     */
    public boolean isBlindlink() {
      return documentNumber == null;
    }
  }
}
