package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Main service for converting LDML XML into business models.
 * It uses a list of strategies (autowired by Spring) to find the correct
 * converter for the given content type.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LdmlToObjectConverterService {

  private final List<LdmlToObjectConverterStrategy> strategies;

  /**
   * Converts the xml of the given documentation unit to a business model.
   *
   * @param documentationUnit The documentation unit to convert
   * @param clazz The class of the returned business model
   * @return Business model representation of given documentation unit's xml
   */
  public <T extends DocumentationUnitContent> T convertToBusinessModel(
    @Nonnull DocumentationUnit documentationUnit,
    Class<T> clazz
  ) {
    log.debug("Finding converter strategy for content type: {}", clazz.getSimpleName());

    LdmlToObjectConverterStrategy strategy = strategies
      .stream()
      .filter(s -> s.supports(clazz))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException(
          "No LdmlToObjectConverterStrategy found for content type: " + clazz.getName()
        )
      );

    log.debug("Using strategy: {}", strategy.getClass().getSimpleName());
    //noinspection unchecked
    return (T) strategy.convertToBusinessModel(documentationUnit);
  }
}
