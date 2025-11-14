package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.*;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

class IdentificationConverter {

  Identification convert(
    @Nonnull AdmDocumentationUnitContent admDocumentationUnitContent,
    List<RisZuordnung> zuordnungen
  ) {
    String aktenzeichen = findAktenzeichen(admDocumentationUnitContent, zuordnungen);
    Eli eli = new Eli(
      admDocumentationUnitContent.dokumenttyp(),
      admDocumentationUnitContent.normgeberList().getFirst(),
      aktenzeichen,
      admDocumentationUnitContent.zitierdaten().getFirst(),
      LocalDate.now()
    );
    Identification identification = new Identification();
    identification.setFrbrWork(convertFrbrWork(admDocumentationUnitContent, eli, aktenzeichen));
    identification.setFrbrExpression(convertFrbrExpression(admDocumentationUnitContent, eli));
    identification.setFrbrManifestation(createFrbrManifestation(eli));
    return identification;
  }

  private FrbrElement convertFrbrWork(
    @NotNull AdmDocumentationUnitContent admDocumentationUnitContent,
    Eli eli,
    String aktenzeichen
  ) {
    FrbrElement frbrWork = new FrbrElement();
    frbrWork.setFrbrThis(new FrbrThis(eli.toWork()));
    frbrWork.setFrbrUri(new FrbrUri(eli.toWork()));
    FrbrAlias frbrAlias = new FrbrAlias();
    frbrAlias.setName("Dokumentnummer");
    frbrAlias.setValue(admDocumentationUnitContent.documentNumber());
    frbrWork.setFrbrAlias(List.of(frbrAlias));
    FrbrDate erfassungsdatum = new FrbrDate();
    erfassungsdatum.setDate(eli.createdDate().toString());
    erfassungsdatum.setName("erfassungsdatum");
    frbrWork.setFrbrDate(erfassungsdatum);
    frbrWork.setFrbrAuthor(new FrbrAuthor());
    frbrWork.setFrbrCountry(new FrbrCountry());
    frbrWork.setFrbrSubtype(
      new FrbrSubtype(admDocumentationUnitContent.dokumenttyp().abbreviation())
    );
    if (StringUtils.isNotBlank(aktenzeichen)) {
      frbrWork.setFrbrNumber(new FrbrNumber(aktenzeichen));
    }
    frbrWork.setFrbrName(new FrbrName(eli.normgeber().format()));
    return frbrWork;
  }

  private FrbrElement convertFrbrExpression(
    AdmDocumentationUnitContent admDocumentationUnitContent,
    Eli eli
  ) {
    FrbrElement frbrExpression = new FrbrElement();
    frbrExpression.setFrbrThis(new FrbrThis(eli.toExpression()));
    frbrExpression.setFrbrUri(new FrbrUri(eli.toExpression()));
    FrbrDate zitierdatum = new FrbrDate();
    zitierdatum.setDate(admDocumentationUnitContent.zitierdaten().getFirst());
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
    AdmDocumentationUnitContent admDocumentationUnitContent,
    List<RisZuordnung> zuordnungen
  ) {
    String aktenzeichen = null;
    if (CollectionUtils.isNotEmpty(admDocumentationUnitContent.aktenzeichen())) {
      aktenzeichen = admDocumentationUnitContent.aktenzeichen().getFirst();
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
