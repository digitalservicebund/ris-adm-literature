package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

class IdentificationConverter {

  Identification convert(
    @Nonnull DocumentationUnitContent documentationUnitContent,
    List<RisZuordnung> zuordnungen
  ) {
    String aktenzeichen = findAktenzeichen(documentationUnitContent, zuordnungen);
    Eli eli = new Eli(
      documentationUnitContent.dokumenttyp(),
      documentationUnitContent.normgeberList().getFirst(),
      aktenzeichen,
      documentationUnitContent.zitierdaten().getFirst(),
      LocalDate.now()
    );
    Identification identification = new Identification();
    identification.setFrbrWork(convertFrbrWork(documentationUnitContent, eli, aktenzeichen));
    identification.setFrbrExpression(convertFrbrExpression(documentationUnitContent, eli));
    identification.setFrbrManifestation(createFrbrManifestation(eli));
    return identification;
  }

  private FrbrElement convertFrbrWork(
    @NotNull DocumentationUnitContent documentationUnitContent,
    Eli eli,
    String aktenzeichen
  ) {
    FrbrElement frbrWork = new FrbrElement();
    frbrWork.setFrbrThis(new FrbrThis(eli.toWork()));
    frbrWork.setFrbrUri(new FrbrUri(eli.toWork()));
    FrbrDate erfassungsdatum = new FrbrDate();
    erfassungsdatum.setDate(eli.createdDate().toString());
    erfassungsdatum.setName("erfassungsdatum");
    frbrWork.setFrbrDate(erfassungsdatum);
    frbrWork.setFrbrAuthor(new FrbrAuthor());
    frbrWork.setFrbrCountry(new FrbrCountry());
    frbrWork.setFrbrSubtype(new FrbrSubtype(documentationUnitContent.dokumenttyp().abbreviation()));
    frbrWork.setFrbrNumber(new FrbrNumber(aktenzeichen));
    frbrWork.setFrbrName(new FrbrName(eli.normgeber().format()));
    return frbrWork;
  }

  private FrbrElement convertFrbrExpression(
    DocumentationUnitContent documentationUnitContent,
    Eli eli
  ) {
    FrbrElement frbrExpression = new FrbrElement();
    frbrExpression.setFrbrThis(new FrbrThis(eli.toExpression()));
    frbrExpression.setFrbrUri(new FrbrUri(eli.toExpression()));
    FrbrAlias frbrAlias = new FrbrAlias();
    frbrAlias.setName("documentNumber");
    frbrAlias.setValue(documentationUnitContent.documentNumber());
    frbrExpression.setFrbrAlias(List.of(frbrAlias));
    FrbrDate zitierdatum = new FrbrDate();
    zitierdatum.setDate(documentationUnitContent.zitierdaten().getFirst());
    zitierdatum.setName("zitierdatum");
    frbrExpression.setFrbrDate(zitierdatum);
    frbrExpression.setFrbrAuthor(new FrbrAuthor());
    frbrExpression.setFrbrLanguage(new FrbrLanguage());
    return frbrExpression;
  }

  private FrbrElement createFrbrManifestation(Eli eli) {
    FrbrElement frbrManifestation = new FrbrElement();
    frbrManifestation.setFrbrThis(new FrbrThis(eli.toManifestation()));
    frbrManifestation.setFrbrUri(new FrbrUri(eli.toManifestation()));
    FrbrDate generierungDate = new FrbrDate();
    generierungDate.setDate(LocalDate.now().toString());
    generierungDate.setName("generierung");
    frbrManifestation.setFrbrDate(generierungDate);
    frbrManifestation.setFrbrAuthor(new FrbrAuthor());
    frbrManifestation.setFrbrFormat(new FrbrFormat());
    return frbrManifestation;
  }

  private String findAktenzeichen(
    DocumentationUnitContent documentationUnitContent,
    List<RisZuordnung> zuordnungen
  ) {
    String aktenzeichen = null;
    if (CollectionUtils.isNotEmpty(documentationUnitContent.aktenzeichen())) {
      aktenzeichen = documentationUnitContent.aktenzeichen().getFirst();
    } else {
      if (CollectionUtils.isNotEmpty(zuordnungen)) {
        aktenzeichen = zuordnungen
          .stream()
          .filter(risZuordnung -> risZuordnung.getAspekt().equals("VRNr"))
          .map(RisZuordnung::getBegriff)
          .findFirst()
          .orElse(null);
      }
    }
    return aktenzeichen;
  }
}
