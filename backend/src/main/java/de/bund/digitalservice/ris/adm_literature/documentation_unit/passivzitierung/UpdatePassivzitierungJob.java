package de.bund.digitalservice.ris.adm_literature.documentation_unit.passivzitierung;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cronjob to update 'Passivzitierungen'.
 */
@Component
@RequiredArgsConstructor
public class UpdatePassivzitierungJob {

  private final UpdatePassivzitierungService updatePassivzitierungService;

  /**
   * Execute updating 'Passivzitierungen'.
   */
  @Scheduled(cron = "${cronjob.UpdatePassivzitierungJob:-}", zone = "Europe/Berlin")
  public void execute() {
    updatePassivzitierungService.updateAllByDocumentCategory(
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );
  }
}
