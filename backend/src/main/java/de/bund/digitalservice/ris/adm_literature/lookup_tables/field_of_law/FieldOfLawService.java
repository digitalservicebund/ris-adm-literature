package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.PageTransformer;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for lookup table field of law (in German 'Sachgebiet').
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FieldOfLawService {

  private final FieldOfLawRepository fieldOfLawRepository;

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
    for (FieldOfLawNorm norm : fieldOfLaw.norms()) {
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
