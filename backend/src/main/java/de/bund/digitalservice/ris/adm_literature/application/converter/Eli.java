package de.bund.digitalservice.ris.adm_literature.application.converter;

import de.bund.digitalservice.ris.adm_literature.application.DocumentType;
import de.bund.digitalservice.ris.adm_literature.application.converter.business.Normgeber;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;

record Eli(
  @Nonnull DocumentType documentType,
  @Nonnull Normgeber normgeber,
  String aktenzeichen,
  String zitierdatum,
  @Nonnull LocalDate createdDate
) {
  String toWork() {
    String workEli =
      "/eli/bund/verwaltungsvorschriften/" +
      documentType.abbreviation().toLowerCase() +
      "/" +
      new Slug(normgeber.format()) +
      "/" +
      createdDate.getYear();
    if (aktenzeichen != null) {
      workEli += "/" + new Slug(aktenzeichen);
    }
    return workEli;
  }

  String toExpression() {
    return toWork() + "/" + (zitierdatum != null ? zitierdatum : "0000-00-00") + "/" + "deu";
  }

  String toManifestation() {
    return toExpression() + ".akn.xml";
  }
}
