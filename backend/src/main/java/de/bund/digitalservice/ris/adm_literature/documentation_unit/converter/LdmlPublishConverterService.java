package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.DocumentationUnitContent;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Main service for converting business models into LDML XML.
 * It uses a list of strategies (autowired by Spring) to find the correct
 * converter for the given content type.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LdmlPublishConverterService {

  private final List<LdmlConverterStrategy> strategies;

  /**
   * Converts the given business model to LDML xml.
   *
   * @param content            The documentation unit content to convert
   * @param previousXmlVersion Previous xml version, or null if none exists.
   * @return LDML xml representation
   * @throws IllegalArgumentException if no matching converter strategy is found
   */
  public String convertToLdml(
    @Nonnull DocumentationUnitContent content,
    String previousXmlVersion
  ) {
    log.debug(
      "Finding converter strategy for content type: {}",
      content.getClass().getSimpleName()
    );

    LdmlConverterStrategy strategy = strategies
      .stream()
      .filter(s -> s.supports(content))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException(
          "No LdmlConverterStrategy found for content type: " + content.getClass().getName()
        )
      );

    log.debug("Using strategy: {}", strategy.getClass().getSimpleName());
    return strategy.convertToLdml(content, previousXmlVersion);
  }
}
