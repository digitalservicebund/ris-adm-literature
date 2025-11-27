package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

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
 * Service for lookup table 'Verweistyp'.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerweisTypService {

  private final VerweisTypRepository verweisTypRepository;

  /**
   * Finds a paginated list of reference types (VerweisTypen) (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link VerweisTyp}.
   */
  @Transactional(readOnly = true)
  public Page<VerweisTyp> findVerweisTypen(@Nonnull VerweisTypQuery query) {
    QueryOptions queryOptions = query.queryOptions();
    String searchTerm = query.searchTerm();
    Sort sort = Sort.by(queryOptions.sortDirection(), queryOptions.sortByProperty());
    Pageable pageable = queryOptions.usePagination()
      ? PageRequest.of(queryOptions.pageNumber(), queryOptions.pageSize(), sort)
      : Pageable.unpaged(sort);
    var verweisTypen = StringUtils.isBlank(searchTerm)
      ? verweisTypRepository.findAll(pageable)
      : verweisTypRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    return PageTransformer.transform(verweisTypen, mapVerweisTypEntity());
  }

  /**
   * Returns an instance of 'Verweistyp' with the specified 'Typnummer' or an empty optional if not found.
   * @param typNummer The 'Typnummer'
   * @return 'Verweistyp' optional
   */
  @Transactional(readOnly = true)
  public Optional<VerweisTyp> findVerweisTypByTypNummer(@Nonnull String typNummer) {
    return verweisTypRepository.findByTypNummer(typNummer).map(mapVerweisTypEntity());
  }

  private Function<VerweisTypEntity, VerweisTyp> mapVerweisTypEntity() {
    return verweisTypEntity ->
      new VerweisTyp(
        verweisTypEntity.getId(),
        verweisTypEntity.getName(),
        verweisTypEntity.getTypNummer()
      );
  }
}
