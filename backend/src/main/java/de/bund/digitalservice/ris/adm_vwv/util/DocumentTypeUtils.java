package de.bund.digitalservice.ris.adm_vwv.util;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class for document type related operations.
 */
@UtilityClass
public class DocumentTypeUtils {

  /**
   * Get the document type from the request header.
   * @return The document type.
   */
  public static DocumentTypeCode getDocumentTypeCode() {
    String documentTypeHeader = null;
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
      HttpServletRequest request = servletRequestAttributes.getRequest();
      documentTypeHeader = request.getHeader("X-Document-Type");
    }

    if (documentTypeHeader == null) {
      throw new IllegalStateException("Missing required header: X-Document-Type");
    }

    DocumentTypeCode documentType;
    try {
      documentType = DocumentTypeCode.valueOf(documentTypeHeader);
    } catch (IllegalArgumentException _) {
      throw new IllegalStateException("Invalid value for X-Document-Type: " + documentTypeHeader);
    }
    return documentType;
  }
}
