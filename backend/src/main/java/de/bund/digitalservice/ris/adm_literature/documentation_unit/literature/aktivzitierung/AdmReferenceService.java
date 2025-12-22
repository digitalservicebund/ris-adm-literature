package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adm reference service for searching adm references (Aktivzitierungen).
 */
@Service
@RequiredArgsConstructor
public class AdmReferenceService {

  private final AdmReferenceRepository admReferenceRepository;

  /**
   * Searches for active citations (Aktivzitierungen) across adm documentation units.
   *
   * @param query The search criteria including document metadata and pagination options.
   * @return A paginated list of ADM overview elements transformed from the persistence layer.
   */
  @Transactional(readOnly = true)
  public Page<AdmAktivzitierungResult> findAktivzitierungen(@Nonnull AdmAktivzitierungQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);

    AdmReferenceSpecification spec = new AdmReferenceSpecification(
      query.documentNumber(),
      query.periodikum(),
      query.zitatstelle(),
      query.inkrafttretedatum(),
      query.aktenzeichen(),
      query.dokumenttyp(),
      query.normgeber(),
      query.zitierdatum()
    );
    var documentationUnitsPage = admReferenceRepository.findAll(spec, pageable);
    return PageTransformer.transform(documentationUnitsPage, admReferenceEntity -> {
      var admIndex = admReferenceEntity.getAdmIndex();
      return new AdmAktivzitierungResult(
        UUID.randomUUID(),
        admReferenceEntity.getDocumentNumber(),
        admIndex.getInkrafttretedatum(),
        admIndex.getLangueberschrift(),
        admIndex.getDokumenttyp(),
        admIndex.getNormgeberList(),
        admIndex.getFundstellen(),
        admIndex.getAktenzeichenList(),
        admIndex.getZitierdaten()
      );
    });
  }
}
