package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Cron job for executing indexing of documentation units.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentationUnitIndexJob {

  private final DocumentationUnitIndexService documentationUnitIndexService;

  /**
   * Execute indexing of all documentation units without documentation unit index.
   */
  @Scheduled(cron = "${cronjob.DocumentationUnitIndexJob:-}", zone = "Europe/Berlin")
  public void indexAll() {
    indexSchema(SchemaType.ADM);
    indexSchema(SchemaType.LITERATURE);
  }

  private void indexSchema(SchemaType schemaType) {
    StopWatch stopWatch = new StopWatch(
      String.format("Index documentation units in database schema %s.", schemaType)
    );
    stopWatch.start();
    long totalNumberOfElements = documentationUnitIndexService.updateIndex(schemaType);
    stopWatch.stop();
    log.info(
      "Indexing {} documentation units in database schema {} finished. \n{}",
      totalNumberOfElements,
      schemaType,
      stopWatch.prettyPrint(TimeUnit.SECONDS)
    );
  }
}
