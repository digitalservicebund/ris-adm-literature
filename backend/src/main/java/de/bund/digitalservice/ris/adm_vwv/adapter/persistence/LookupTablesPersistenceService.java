package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static de.bund.digitalservice.ris.adm_vwv.adapter.persistence.InstitutionTypeMapper.*;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.application.Page;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Court;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.NormAbbreviation;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.VerweisTyp;
import jakarta.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence service for lookup tables.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService {

  private final DocumentTypeRepository documentTypeRepository;
  private final FieldOfLawRepository fieldOfLawRepository;
  private final LegalPeriodicalsRepository legalPeriodicalsRepository;
  private final RegionRepository regionRepository;
  private final InstitutionRepository institutionRepository;
  private final CitationTypeRepository citationTypeRepository;

  /**
   * Finds a paginated list of document types based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link DocumentType}.
   */
  @Transactional(readOnly = true)
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var documentTypes = StringUtils.isBlank(searchTerm)
      ? documentTypeRepository.findAll(pageable)
      : documentTypeRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );
    return PageTransformer.transform(documentTypes, mapDocumentTypeEntity());
  }

  /**
   * Finds a single document type by its abbreviation.
   *
   * @param abbreviation The abbreviation of the document type to find.
   * @return An {@link Optional} containing the found {@link DocumentType}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<DocumentType> findDocumentTypeByAbbreviation(@Nonnull String abbreviation) {
    return documentTypeRepository.findByAbbreviation(abbreviation).map(mapDocumentTypeEntity());
  }

  private Function<DocumentTypeEntity, DocumentType> mapDocumentTypeEntity() {
    return documentTypeEntity ->
      new DocumentType(documentTypeEntity.getAbbreviation(), documentTypeEntity.getName());
  }

  /**
   * Finds all direct children of a field of law by its identifier.
   *
   * @param identifier The unique identifier of the parent field of law.
   * @return A list of child {@link FieldOfLaw}.
   */
  @Transactional(readOnly = true)
  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return fieldOfLawRepository
      .findByIdentifier(identifier)
      .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, true, true)
      )
      .map(FieldOfLaw::children)
      .orElse(List.of());
  }

  /**
   * Finds all top-level fields of law (those without a parent).
   *
   * @return A list of parent {@link FieldOfLaw}.
   */
  @Transactional(readOnly = true)
  public List<FieldOfLaw> findFieldsOfLawParents() {
    return fieldOfLawRepository
      .findByParentIsNullAndNotationOrderByIdentifier("NEW")
      .stream()
      .map(fieldOfLawEntity ->
        FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true)
      )
      .toList();
  }

  /**
   * Finds a single field of law by its identifier.
   *
   * @param identifier The unique identifier of the field of law.
   * @return An {@link Optional} containing the found {@link FieldOfLaw}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<FieldOfLaw> findFieldOfLaw(@Nonnull String identifier) {
    return fieldOfLawRepository
      .findByIdentifier(identifier)
      .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, true, true)
      );
  }

  /**
   * Finds a paginated list of fields of law based on a search query.
   *
   * @param query The query containing search terms and pagination options.
   * @return A page of {@link FieldOfLaw}.
   */
  @Transactional(readOnly = true)
  public Page<FieldOfLaw> findFieldsOfLaw(@Nonnull FieldOfLawQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);

    List<String> textTerms = splitSearchTerms(query.text());
    List<String> normTerms = splitSearchTerms(
      StringUtils.trimToNull(Strings.CS.replace(query.norm(), "§", ""))
    );
    FieldOfLawSpecification fieldOfLawSpecification = new FieldOfLawSpecification(
      query.identifier(),
      textTerms,
      normTerms
    );
    var searchResult = fieldOfLawRepository
      .findAll(fieldOfLawSpecification, pageable)
      .map(fieldOfLawEntity ->
        FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true)
      );

    if (searchResult.isEmpty()) {
      // If no results found, do not re-sort result
      return PageTransformer.transform(searchResult);
    }

    String normParagraphsWithSpace = RegExUtils.replaceAll(
      StringUtils.trim(query.norm()),
      "§(\\d+)",
      "§ $1"
    );
    List<FieldOfLaw> orderedList = orderResults(textTerms, normParagraphsWithSpace, searchResult);
    return PageTransformer.transform(
      new PageImpl<>(orderedList, searchResult.getPageable(), searchResult.getTotalElements())
    );
  }

  /**
   * Finds a paginated list of legal periodicals based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link LegalPeriodical}.
   */
  @Transactional(readOnly = true)
  public Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var legalPeriodicals = StringUtils.isBlank(searchTerm)
      ? legalPeriodicalsRepository.findAll(pageable)
      : legalPeriodicalsRepository.findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );

    return PageTransformer.transform(legalPeriodicals, mapLegalPeriodicalEntity());
  }

  private Function<LegalPeriodicalEntity, LegalPeriodical> mapLegalPeriodicalEntity() {
    return legalPeriodicalEntity ->
      new LegalPeriodical(
        legalPeriodicalEntity.getId(),
        legalPeriodicalEntity.getAbbreviation(),
        legalPeriodicalEntity.getPublicId(),
        legalPeriodicalEntity.getTitle(),
        legalPeriodicalEntity.getSubtitle(),
        legalPeriodicalEntity.getCitationStyle()
      );
  }

  /**
   * Finds all legal periodicals matching a given abbreviation.
   *
   * @param abbreviation The exact abbreviation to search for.
   * @return A list of matching {@link LegalPeriodical}.
   */
  @Transactional(readOnly = true)
  public List<LegalPeriodical> findLegalPeriodicalsByAbbreviation(@Nonnull String abbreviation) {
    LegalPeriodicalEntity probe = new LegalPeriodicalEntity();
    probe.setAbbreviation(abbreviation);
    return legalPeriodicalsRepository
      .findAll(Example.of(probe))
      .stream()
      .map(mapLegalPeriodicalEntity())
      .toList();
  }

  /**
   * Finds a paginated list of regions based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link Region}.
   */
  @Transactional(readOnly = true)
  public Page<Region> findRegions(@Nonnull RegionQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var regions = StringUtils.isBlank(searchTerm)
      ? regionRepository.findAll(pageable)
      : regionRepository.findByCodeContainingIgnoreCase(searchTerm, pageable);

    return PageTransformer.transform(regions, mapRegionEntity());
  }

  /**
   * Finds a single region by its code.
   *
   * @param code The code of the region to find.
   * @return An {@link Optional} containing the found {@link Region}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<Region> findRegionByCode(@Nonnull String code) {
    return regionRepository.findByCode(code).map(mapRegionEntity());
  }

  private Function<RegionEntity, Region> mapRegionEntity() {
    return regionEntity ->
      new Region(regionEntity.getId(), regionEntity.getCode(), regionEntity.getLongText());
  }

  /**
   * Finds a paginated list of norm abbreviations (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link NormAbbreviation}.
   */
  @Transactional(readOnly = true)
  public Page<NormAbbreviation> findNormAbbreviations(@Nonnull NormAbbreviationQuery query) {
    log.info(
      "Ignoring given query as mocked norm abbreviations result is always returned: {}.",
      query
    );
    return new Page<>(
      List.of(
        new NormAbbreviation(
          UUID.fromString("3f7c912-a2d1-4b3e-9d2a-41c2a8e5c1f7"),
          "SGB 5",
          "Sozialgesetzbuch (SGB) Fünftes Buch (V)"
        ),
        new NormAbbreviation(
          UUID.fromString("d9a04e5-b7c8-49f2-8a31-7fb024b39ce8"),
          "KVLG",
          "Gesetz zur Weiterentwicklung des Rechts der gesetzlichen Krankenversicherung"
        )
      ),
      2,
      0,
      2,
      2,
      true,
      true,
      false
    );
  }

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
   * @param name The name of the institution.
   * @param institutionType The type of the institution.
   * @return An {@link Optional} containing the found {@link Institution}, or empty if not found.
   */
  @Transactional(readOnly = true)
  public Optional<Institution> findInstitutionByNameAndType(
    @Nonnull String name,
    @Nonnull InstitutionType institutionType
  ) {
    return institutionRepository
      .findByNameAndType(name, mapInstitutionType(institutionType))
      .map(mapInstitutionEntity());
  }

  /**
   * Finds a paginated list of courts (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link Court}.
   */
  @Transactional(readOnly = true)
  public Page<Court> findCourts(@Nonnull CourtQuery query) {
    log.info("Ignoring given query as mocked courts result is always returned: {}.", query);
    return new Page<>(
      List.of(
        new Court(UUID.fromString("0e1b035-a7f4-4d88-b5c0-a7d0466b8752"), "AG", "Aachen"),
        new Court(
          UUID.fromString("8163531c-2c51-410a-9591-b45b004771da"),
          "Berufsgericht für Architekten",
          "Bremen"
        )
      ),
      2,
      0,
      2,
      2,
      true,
      true,
      false
    );
  }

  /**
   * Finds a paginated list of reference types (VerweisTypen) (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link VerweisTyp}.
   */
  @Transactional(readOnly = true)
  public Page<VerweisTyp> findVerweisTypen(@Nonnull VerweisTypQuery query) {
    log.info(
      "Ignoring given query as mocked reference types result is always returned: {}.",
      query
    );
    return new Page<>(
      List.of(
        new VerweisTyp(UUID.fromString("3b0c6c8c-bb5d-4c18-9d1d-6d3c93e88f45"), "anwendung"),
        new VerweisTyp(UUID.fromString("c8a27a4a-79d9-4f28-b462-47eeb03b6b6f"), "neuregelung"),
        new VerweisTyp(UUID.fromString("5e7c24f7-85f4-4e89-9113-4d5eae1b29d3"), "rechtsgrundlage")
      ),
      3,
      0,
      3,
      3,
      true,
      true,
      false
    );
  }

  /**
   * Finds a paginated list of citation types (ZitierArten) based on a search query.
   *
   * @param query The query containing search term and pagination options.
   * @return A page of {@link ZitierArt}.
   */
  @Transactional(readOnly = true)
  public Page<ZitierArt> findZitierArten(@Nonnull ZitierArtQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var citationTypes = StringUtils.isBlank(searchTerm)
      ? citationTypeRepository.findAll(pageable)
      : citationTypeRepository.findByAbbreviationContainingIgnoreCaseOrLabelContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );
    return PageTransformer.transform(citationTypes, mapCitationTypeEntity());
  }

  /**
   * Finds all citation types (ZitierArten) matching a given abbreviation.
   *
   * @param abbreviation The exact abbreviation to search for.
   * @return A list of matching {@link ZitierArt}.
   */
  @Transactional(readOnly = true)
  public List<ZitierArt> findZitierArtenByAbbreviation(@Nonnull String abbreviation) {
    CitationTypeEntity probe = new CitationTypeEntity();
    probe.setAbbreviation(abbreviation);
    return citationTypeRepository
      .findAll(Example.of(probe))
      .stream()
      .map(mapCitationTypeEntity())
      .toList();
  }

  private Function<CitationTypeEntity, ZitierArt> mapCitationTypeEntity() {
    return citationTypeEntity ->
      new ZitierArt(
        citationTypeEntity.getId(),
        citationTypeEntity.getAbbreviation(),
        citationTypeEntity.getLabel()
      );
  }

  private Function<InstitutionEntity, Institution> mapInstitutionEntity() {
    return institutionEntity ->
      new Institution(
        institutionEntity.getId(),
        institutionEntity.getName(),
        institutionEntity.getOfficialName(),
        mapInstitutionTypeString(institutionEntity.getType()),
        institutionEntity.getRegions().stream().map(mapRegionEntity()).toList()
      );
  }

  private List<String> splitSearchTerms(String searchStr) {
    return searchStr != null ? List.of(searchStr.split("\\s+")) : List.of();
  }

  private List<FieldOfLaw> orderResults(
    List<String> textTerms,
    String normParagraphsWithSpace,
    org.springframework.data.domain.Page<FieldOfLaw> searchResult
  ) {
    // Calculate scores and sort the list based on the score and identifier
    List<ScoredFieldOfLaw> scores = calculateScore(
      textTerms,
      normParagraphsWithSpace,
      searchResult.getContent()
    );
    return scores
      .stream()
      .sorted(
        Comparator.comparing(ScoredFieldOfLaw::score).thenComparing(scoredFieldOfLaw ->
          scoredFieldOfLaw.fieldOfLaw().identifier()
        )
      )
      .map(ScoredFieldOfLaw::fieldOfLaw)
      .toList();
  }

  private List<ScoredFieldOfLaw> calculateScore(
    List<String> textTerms,
    String normParagraphsWithSpace,
    List<FieldOfLaw> fieldsOfLaw
  ) {
    List<ScoredFieldOfLaw> scoredFieldsOfLaw = new ArrayList<>();
    fieldsOfLaw.forEach(fieldOfLaw -> {
      int score = 0;
      for (String textTerm : textTerms) {
        score += calculateScoreByTextTerm(fieldOfLaw, textTerm);
      }
      if (normParagraphsWithSpace != null) {
        score += calculateScoreByNormWithParagraphs(fieldOfLaw, normParagraphsWithSpace);
      }
      scoredFieldsOfLaw.add(new ScoredFieldOfLaw(fieldOfLaw, score));
    });
    return scoredFieldsOfLaw;
  }

  private int calculateScoreByTextTerm(@Nonnull FieldOfLaw fieldOfLaw, @Nonnull String textTerm) {
    int score = 0;
    textTerm = textTerm.toLowerCase();
    String text = fieldOfLaw.text().toLowerCase();
    if (text.startsWith(textTerm)) score += 5;
    // split by whitespace and hyphen to get words
    for (String textPart : text.split("[\\s-]+")) {
      if (textPart.equals(textTerm)) score += 4;
      else if (textPart.startsWith(textTerm)) score += 3;
      else if (textPart.contains(textTerm)) score += 1;
    }
    return score;
  }

  private int calculateScoreByNormWithParagraphs(
    @Nonnull FieldOfLaw fieldOfLaw,
    @Nonnull String normWithParagraphs
  ) {
    int score = 0;
    var normWithParagraphsLowerCase = normWithParagraphs.toLowerCase();
    for (Norm norm : fieldOfLaw.norms()) {
      String description = norm.singleNormDescription() == null
        ? ""
        : norm.singleNormDescription().toLowerCase();
      String normText = description + " " + norm.abbreviation().toLowerCase();
      if (description.equals(normWithParagraphsLowerCase)) score += 8;
      else if (description.startsWith(normWithParagraphsLowerCase)) score += 5;
      else if (normText.contains(normWithParagraphsLowerCase)) score += 5;
    }
    return score;
  }

  record ScoredFieldOfLaw(@Nonnull FieldOfLaw fieldOfLaw, @Nonnull Integer score) {}
}
