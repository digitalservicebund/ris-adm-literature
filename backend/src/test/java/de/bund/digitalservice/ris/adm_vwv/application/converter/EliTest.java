package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Normgeber;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestNormgeber;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EliTest {

  private static Stream<Arguments> workEliComponents() {
    return Stream.of(
      // documentType, normgeber, aktenzeichen, zitierdatum, createdDate, expected
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("DEU"),
        "Akt. 43",
        "2024-01-01",
        LocalDate.of(2024, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/deu/2024/akt-43"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByInstitution("Landesministerium", "ND"),
        "Akt. 43",
        null,
        LocalDate.of(2020, Month.DECEMBER, 11),
        "/eli/bund/verwaltungsvorschriften/vr/nd-landesministerium/2020/akt-43"
      ),
      Arguments.of(
        new DocumentType("ST", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("DEU"),
        "101123",
        null,
        LocalDate.of(1980, Month.MAY, 6),
        "/eli/bund/verwaltungsvorschriften/st/deu/1980/101123"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("Bundesausschuss ÜT"),
        null,
        "2024-01-01",
        LocalDate.of(2024, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet/2024"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("Bundesausschuss ÜT"),
        null,
        null,
        LocalDate.of(1999, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet/1999"
      )
    );
  }

  @ParameterizedTest
  @MethodSource("workEliComponents")
  void toWork(
    DocumentType documentType,
    Normgeber normgeber,
    String aktenzeichen,
    String dateToQuote,
    LocalDate createdDate,
    String expected
  ) {
    // given
    Eli eli = new Eli(documentType, normgeber, aktenzeichen, dateToQuote, createdDate);

    // when
    String actualEli = eli.toWork();

    // then
    assertThat(actualEli).isEqualTo(expected);
  }

  private static Stream<Arguments> expressionEliComponents() {
    return Stream.of(
      // documentType, normgeber, reference, dateToQuote, createdDate, expected
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("DEU"),
        "Akt. 43",
        "2024-01-01",
        LocalDate.of(2024, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/deu/2024/akt-43/2024-01-01/deu"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByInstitution("Landesministerium", "ND"),
        "Akt. 43",
        null,
        LocalDate.of(2020, Month.DECEMBER, 11),
        "/eli/bund/verwaltungsvorschriften/vr/nd-landesministerium/2020/akt-43/0000-00-00/deu"
      ),
      Arguments.of(
        new DocumentType("ST", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("DEU"),
        "101123",
        null,
        LocalDate.of(1980, Month.MAY, 6),
        "/eli/bund/verwaltungsvorschriften/st/deu/1980/101123/0000-00-00/deu"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("Bundesausschuss ÜT"),
        null,
        "2024-01-01",
        LocalDate.of(2024, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet/2024/2024-01-01/deu"
      ),
      Arguments.of(
        new DocumentType("VR", "Bekanntmachung"),
        TestNormgeber.createByLegalEntity("Bundesausschuss ÜT"),
        null,
        null,
        LocalDate.of(1999, Month.JANUARY, 4),
        "/eli/bund/verwaltungsvorschriften/vr/bundesausschuss-uet/1999/0000-00-00/deu"
      )
    );
  }
  //  @ParameterizedTest
  //  @MethodSource("expressionEliComponents")
  //  fun `toExpressionString returns eli with expression date`(
  //  documentType: DocumentType,
  //  normgeber: Normgeber,
  //  reference: String?,
  //  dateToQuote: String?,
  //  createdDate: String,
  //  expected: String,
  //    ) {
  //    // given
  //    val eli = Eli(documentType, normgeber, reference, dateToQuote, createdDate)
  //
  //    // when
  //    val actualEli = eli.toExpressionString()
  //
  //    // then
  //    assertThat(actualEli).isEqualTo(expected)
  //  }
  //
  //  companion object {
  //
  //    @JvmStatic
  //    fun expressionEliComponents(): List<Arguments> =
  //    listOf(
  //      // documentType, normgeber, reference, dateToQuote, createdDate, expected
  //      Arguments.of(
  //        DocumentType("VR", "Bekanntmachung"),
  //        Normgeber("DEU"),
  //        "Akt. 43",
  //        "2024-01-01",
  //        "2024-01-04",
  //        "/eli/bund/verwaltungsvorschriften/vr/deu/2024/akt-43/2024-01-01/deu",
  //        ),
  //      Arguments.of(
  //        DocumentType("VR", "Bekanntmachung"),
  //        Normgeber("ND", "Landesministerium"),
  //        "Akt. 43",
  //        null,
  //        "2020-12-11",
  //        "/eli/bund/verwaltungsvorschriften/vr/nd-landesministerium/2020/akt-43/0000-00-00/deu",
  //        ),
  //      Arguments.of(
  //        DocumentType("ST", "Bekanntmachung"),
  //        Normgeber("DEU"),
  //        "101123",
  //        null,
  //        "1980-05-06",
  //        "/eli/bund/verwaltungsvorschriften/st/deu/1980/101123/0000-00-00/deu",
  //        ),
  //      Arguments.of(
  //        DocumentType("VR", "Bekanntmachung"),
  //        Normgeber("Gemeinsamer Bundesausschuss"),
  //        null,
  //        "2024-01-01",
  //        "2024-01-04",
  //        "/eli/bund/verwaltungsvorschriften/vr/gemeinsamer-bundesausschuss/2024/2024-01-01/deu",
  //        ),
  //      Arguments.of(
  //        DocumentType("VR", "Bekanntmachung"),
  //        Normgeber("Gemeinsamer Bundesausschuss"),
  //        null,
  //        null,
  //        "1999-01-04",
  //        "/eli/bund/verwaltungsvorschriften/vr/gemeinsamer-bundesausschuss/1999/0000-00-00/deu",
  //        ),
  //      )
  //  }
}
