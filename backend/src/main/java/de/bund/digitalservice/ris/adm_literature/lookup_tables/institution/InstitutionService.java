package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.RegionEntity;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for lookup table institution (in German either 'Organ' or 'Juristische Person').
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final InstitutionRepository institutionRepository;

  /**
   * Finds a paginated list of institutions based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link Institution}.
   */
  @Transactional(readOnly = true)
  public Page<Institution> findInstitutions(@Nonnull InstitutionQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var institutions = StringUtils.isBlank(searchTerm)
      ? institutionRepository.findAll(pageable)
      : institutionRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    return PageTransformer.transform(institutions, mapInstitutionEntity());
  }

  /**
   * Finds a single institution by its name and type.
   *
   * @param name            The name of the institution.
   * @param institutionType The type of the institution.
   * @return An {@link Optional} containing the found {@link Institution}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<Institution> findInstitutionByNameAndType(
    @Nonnull String name,
    @Nonnull InstitutionType institutionType
  ) {
    return institutionRepository
      .findByNameAndType(name, InstitutionTypeMapper.mapInstitutionType(institutionType))
      .map(mapInstitutionEntity());
  }

  private Function<InstitutionEntity, Institution> mapInstitutionEntity() {
    return institutionEntity ->
      new Institution(
        institutionEntity.getId(),
        institutionEntity.getName(),
        institutionEntity.getOfficialName(),
        InstitutionTypeMapper.mapInstitutionTypeString(institutionEntity.getType()),
        institutionEntity.getRegions().stream().map(mapRegionEntity()).toList()
      );
  }

  private Function<RegionEntity, Region> mapRegionEntity() {
    return regionEntity ->
      new Region(regionEntity.getId(), regionEntity.getCode(), regionEntity.getLongText());
  }
}
