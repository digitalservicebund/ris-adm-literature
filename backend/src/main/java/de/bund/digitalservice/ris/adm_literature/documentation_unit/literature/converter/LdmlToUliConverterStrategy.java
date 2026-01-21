package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.LdmlToObjectConverterStrategy;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * Strategy for converting ldml to an instance of Uli ('unselbständige Literatur').
 */
@RequiredArgsConstructor
@Service
public class LdmlToUliConverterStrategy implements LdmlToObjectConverterStrategy {

  private final LdmlDocumentFactory ldmlDocumentFactory;
  private final LdmlToLiteratureConverterMethods converterMethods;
  private final XPathFactory xPathFactory = XPathFactory.newInstance();

  @Override
  public UliDocumentationUnitContent convertToBusinessModel(
    @NonNull DocumentationUnit documentationUnit
  ) {
    try {
      LdmlDocument ldmlDocument = ldmlDocumentFactory.createDocument(documentationUnit.xml());
      XPath xPath = xPathFactory.newXPath();
      return new UliDocumentationUnitContent(
        documentationUnit.id(),
        documentationUnit.documentNumber(),
        converterMethods.mapVeroeffentlichungsJahr(ldmlDocument, xPath),
        converterMethods.mapDokumenttypen(
          ldmlDocument,
          xPath,
          DocumentCategory.LITERATUR_UNSELBSTAENDIG
        ),
        converterMethods.mapHauptsachtitel(ldmlDocument, xPath),
        converterMethods.mapHauptsachtitelZusatz(ldmlDocument, xPath),
        converterMethods.mapDokumentarischerTitel(ldmlDocument, xPath),
        null
      );
    } catch (
      IOException | SAXException | XPathExpressionException | ParserConfigurationException e
    ) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean supports(Class<? extends DocumentationUnitContent> clazz) {
    return clazz.equals(UliDocumentationUnitContent.class);
  }
}
