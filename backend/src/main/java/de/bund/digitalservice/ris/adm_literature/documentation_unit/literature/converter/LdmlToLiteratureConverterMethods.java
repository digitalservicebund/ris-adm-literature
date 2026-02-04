package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungRechtsprechung;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungSli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungUli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.xml.NodeToList;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathNodes;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

@Component
@RequiredArgsConstructor
class LdmlToLiteratureConverterMethods {

  private final DocumentTypeService documentTypeService;
  private static final String DOCUMENT_NUMBER = "documentNumber";

  @NonNull
  String mapVeroeffentlichungsJahr(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    XPathNodes risVeroeffentlichungsjahreNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/proprietary/meta/veroeffentlichungsJahre/veroeffentlichungsJahr",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    return concatenateNodeTextContent(risVeroeffentlichungsjahreNodes);
  }

  List<DocumentType> mapDokumenttypen(
    LdmlDocument ldmlDocument,
    XPath xPath,
    DocumentCategory documentCategory
  ) throws XPathExpressionException {
    XPathNodes dokumenttypenNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/classification[@source='doktyp']/keyword/@value",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    List<DocumentType> documentTypes = new ArrayList<>();
    for (Node dokumenttypNode : dokumenttypenNodes) {
      String abbreviation = dokumenttypNode.getTextContent();
      documentTypeService
        .findDocumentTypeByAbbreviation(abbreviation, documentCategory)
        .ifPresent(documentTypes::add);
    }
    return documentTypes;
  }

  String mapHauptsachtitel(LdmlDocument ldmlDocument, XPath xPath) throws XPathExpressionException {
    XPathNodes haupttitelNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/identification/FRBRWork/FRBRalias[@name='haupttitel']/@value",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    return concatenateNodeTextContent(haupttitelNodes);
  }

  String mapHauptsachtitelZusatz(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    return xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/identification/FRBRWork/FRBRalias[@name='haupttitelZusatz']/@value",
      ldmlDocument.getDocument(),
      String.class
    );
  }

  String mapDokumentarischerTitel(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    return xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/identification/FRBRWork/FRBRalias[@name='dokumentarischerTitel']/@value",
      ldmlDocument.getDocument(),
      String.class
    );
  }

  List<AktivzitierungSli> mapAktivzitierungSli(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    XPathNodes selbstaendigeLiteraturReferenceNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/analysis/otherReferences[@source='active']/implicitReference/selbstaendigeLiteraturReference",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    List<AktivzitierungSli> aktivzitierungenSli = new ArrayList<>();
    for (Node selbstaendigeLiteraturReferenceNode : selbstaendigeLiteraturReferenceNodes) {
      Map<String, String> attributes = NodeToList.toAttributeMap(
        selbstaendigeLiteraturReferenceNode.getAttributes()
      );
      aktivzitierungenSli.add(
        new AktivzitierungSli(
          UUID.randomUUID(),
          attributes.get(DOCUMENT_NUMBER),
          attributes.get("veroeffentlichungsJahr"),
          attributes.get("buchtitel"),
          null,
          attributes.get("autor") != null ? List.of(attributes.get("autor")) : List.of(),
          null
        )
      );
    }
    return aktivzitierungenSli;
  }

  List<AktivzitierungAdm> mapAktivzitierungAdm(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    XPathNodes verwaltungsvorschriftReferenceNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/analysis/otherReferences[@source='active']/implicitReference/verwaltungsvorschriftReference",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    List<AktivzitierungAdm> aktivzitierungenAdm = new ArrayList<>();
    for (Node verwaltungsvorschriftReferenceNode : verwaltungsvorschriftReferenceNodes) {
      Map<String, String> attributes = NodeToList.toAttributeMap(
        verwaltungsvorschriftReferenceNode.getAttributes()
      );
      String periodikum = xPath.evaluateExpression(
        "fundstelle/@periodikum",
        verwaltungsvorschriftReferenceNode,
        String.class
      );
      String zitatstelle = xPath.evaluateExpression(
        "fundstelle/@zitatstelle",
        verwaltungsvorschriftReferenceNode,
        String.class
      );
      aktivzitierungenAdm.add(
        new AktivzitierungAdm(
          UUID.randomUUID(),
          attributes.get(DOCUMENT_NUMBER),
          attributes.get("abbreviation"),
          StringUtils.trimToNull(periodikum),
          StringUtils.trimToNull(zitatstelle),
          attributes.get("inkrafttretedatum"),
          attributes.get("aktenzeichen"),
          attributes.get("dokumenttyp"),
          attributes.get("normgeber")
        )
      );
    }
    return aktivzitierungenAdm;
  }

  List<AktivzitierungRechtsprechung> mapAktivzitierungRechtsprechung(
    LdmlDocument ldmlDocument,
    XPath xPath
  ) throws XPathExpressionException {
    XPathNodes caselawReferenceNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/analysis/otherReferences[@source='active']/implicitReference/caselawReference",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    List<AktivzitierungRechtsprechung> aktivzitierungenRechtsprechung = new ArrayList<>();
    for (Node caselawReferenceNode : caselawReferenceNodes) {
      Map<String, String> attributes = NodeToList.toAttributeMap(
        caselawReferenceNode.getAttributes()
      );
      aktivzitierungenRechtsprechung.add(
        new AktivzitierungRechtsprechung(
          UUID.randomUUID(),
          attributes.get(DOCUMENT_NUMBER),
          attributes.get("abbreviation"),
          attributes.get("date"),
          attributes.get("referenceNumber"),
          attributes.get("dokumenttyp"),
          attributes.get("court"),
          attributes.get("courtLocation")
        )
      );
    }
    return aktivzitierungenRechtsprechung;
  }

  List<AktivzitierungUli> mapAktivzitierungUli(LdmlDocument ldmlDocument, XPath xPath)
    throws XPathExpressionException {
    XPathNodes uliReferenceNodes = xPath.evaluateExpression(
      "/akomaNtoso/doc/meta/analysis/otherReferences[@source='active']/implicitReference/unselbstaendigeLiteraturReference",
      ldmlDocument.getDocument(),
      XPathNodes.class
    );
    List<AktivzitierungUli> aktivzitierungenUli = new ArrayList<AktivzitierungUli>();
    for (Node uliReferenceNode : uliReferenceNodes) {
      Map<String, String> attributes = NodeToList.toAttributeMap(uliReferenceNode.getAttributes());
      aktivzitierungenUli.add(
        new AktivzitierungUli(
          UUID.randomUUID(),
          attributes.get(DOCUMENT_NUMBER),
          attributes.get("periodikum"),
          attributes.get("zitatstelle"),
          attributes.get("verfasser") != null ? List.of(attributes.get("verfasser")) : List.of(),
          attributes.get("dokumenttyp")
        )
      );
    }
    return aktivzitierungenUli;
  }

  private String concatenateNodeTextContent(XPathNodes xPathNodes) {
    StringBuilder textContent = new StringBuilder();
    for (Node node : xPathNodes) {
      textContent.append(node.getTextContent()).append(" ");
    }
    return textContent.toString().trim();
  }
}
