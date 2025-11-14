package de.bund.digitalservice.ris.adm_literature.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;

/**
 * Class for reading files for testing purposes.
 */
@UtilityClass
public class TestFile {

  /**
   * Reads the given filename and returns a string which its contents. The file must be located on the classpath.
   * @param name The filename
   * @return Content of the given filename
   */
  public static String readFileToString(String name) {
    try {
      URL resource = TestFile.class.getClassLoader().getResource(name);
      if (resource == null) {
        throw new FileNotFoundException(name);
      }
      Path path = Path.of(resource.toURI());
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
