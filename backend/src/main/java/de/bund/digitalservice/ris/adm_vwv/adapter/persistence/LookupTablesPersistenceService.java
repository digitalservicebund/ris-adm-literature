package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import java.util.*;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence service for lookup tables
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LookupTablesPersistenceService implements LookupTablesPersistencePort {

  private final DocumentTypesRepository documentTypesRepository;
  private final FieldOfLawRepository fieldOfLawRepository;
  private final LegalPeriodicalsRepository legalPeriodicalsRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentType> findDocumentTypes(@Nonnull DocumentTypeQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    Page<DocumentTypeEntity> documentTypes = StringUtils.isBlank(searchTerm)
      ? documentTypesRepository.findAll(pageable)
      : documentTypesRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );

    return documentTypes.map(documentTypeEntity ->
      new DocumentType(documentTypeEntity.getAbbreviation(), documentTypeEntity.getName())
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<FieldOfLaw> findFieldsOfLawChildren(@Nonnull String identifier) {
    return fieldOfLawRepository
      .findByIdentifier(identifier)
      .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, true, true)
      )
      .map(FieldOfLaw::children)
      .orElse(List.of());
  }

  @Override
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

  @Override
  @Transactional(readOnly = true)
  public Optional<FieldOfLaw> findFieldOfLaw(@Nonnull String identifier) {
    return fieldOfLawRepository
      .findByIdentifier(identifier)
      .map(fieldOfLawEntity -> FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, true, true)
      );
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FieldOfLaw> findFieldsOfLaw(@Nonnull FieldOfLawQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);

    List<String> textTerms = splitSearchTerms(query.text());
    List<String> normTerms = splitSearchTerms(
      StringUtils.trimToNull(StringUtils.replace(query.norm(), "§", ""))
    );
    FieldOfLawSpecification fieldOfLawSpecification = new FieldOfLawSpecification(
      query.identifier(),
      textTerms,
      normTerms
    );
    Page<FieldOfLaw> searchResult = fieldOfLawRepository
      .findAll(fieldOfLawSpecification, pageable)
      .map(fieldOfLawEntity ->
        FieldOfLawTransformer.transformToDomain(fieldOfLawEntity, false, true)
      );

    if (searchResult.isEmpty()) {
      // If no results found, do not re-sort result
      return searchResult;
    }

    String normParagraphsWithSpace = RegExUtils.replaceAll(
      StringUtils.trim(query.norm()),
      "§(\\d+)",
      "§ $1"
    );
    List<FieldOfLaw> orderedList = orderResults(textTerms, normParagraphsWithSpace, searchResult);
    return new PageImpl<>(orderedList, searchResult.getPageable(), searchResult.getTotalElements());
  }

  @Override
  public Page<LegalPeriodical> findLegalPeriodicals(@Nonnull LegalPeriodicalQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    Page<LegalPeriodicalEntity> legalPeriodicals = StringUtils.isBlank(searchTerm)
      ? legalPeriodicalsRepository.findAll(pageable)
      : legalPeriodicalsRepository.findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
        searchTerm,
        searchTerm,
        pageable
      );

    return legalPeriodicals.map(legalPeriodicalEntity ->
      new LegalPeriodical(
        legalPeriodicalEntity.getAbbreviation(),
        legalPeriodicalEntity.getTitle(),
        legalPeriodicalEntity.getSubtitle(),
        legalPeriodicalEntity.getCitationStyle()
      )
    );
  }

  private List<String> splitSearchTerms(String searchStr) {
    return searchStr != null ? List.of(searchStr.split("\\s+")) : List.of();
  }

  private List<FieldOfLaw> orderResults(
    List<String> textTerms,
    String normParagraphsWithSpace,
    Page<FieldOfLaw> searchResult
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
