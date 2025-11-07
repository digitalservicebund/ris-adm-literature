package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Provides an efficient utility for converting language names to ISO 639-2 codes.
 * <p>
 * This class pre-builds a static map on startup to ensure fast lookups,
 * avoiding the need to loop through all system locales on every call.
 */
@UtilityClass
public class LanguageConverter {

  private static final Map<String, String> LANGUAGE_NAME_TO_ISO3_MAP;

  static {
    LANGUAGE_NAME_TO_ISO3_MAP = Locale.availableLocales()
      .filter(locale -> !locale.getISO3Language().isEmpty())
      .collect(
        Collectors.toMap(
          locale -> locale.getDisplayLanguage(Locale.GERMAN).toLowerCase(),
          Locale::getISO3Language,
          (existing, _) -> existing
        )
      );
  }

  public static String getIso3CodeForName(String languageName) {
    return LANGUAGE_NAME_TO_ISO3_MAP.get(languageName.toLowerCase());
  }
}
