package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class LegalPeriodicalServiceTest {

  @InjectMocks
  private LegalPeriodicalService legalPeriodicalService;

  @Mock
  private LegalPeriodicalsRepository legalPeriodicalsRepository;

  @Test
  void findLegalPeriodicals_all() {
    // given
    var lpAbbreviation = "BKK";
    var lpTitle = "Die Betriebskrankenkasse";
    var lpSubtitle = "Zeitschrift des Bundesverbandes der Betriebskrankenkassen Essen";
    var lpCitationStyle = "1969, 138-140; BKK 2007, Sonderbeilage, 1-5";
    LegalPeriodicalEntity legalPeriodicalEntity = new LegalPeriodicalEntity();
    legalPeriodicalEntity.setAbbreviation(lpAbbreviation);
    legalPeriodicalEntity.setTitle(lpTitle);
    legalPeriodicalEntity.setSubtitle(lpSubtitle);
    legalPeriodicalEntity.setCitationStyle(lpCitationStyle);
    given(legalPeriodicalsRepository.findAll(any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(legalPeriodicalEntity))
    );

    // when
    var legalPeriodicals = legalPeriodicalService.findLegalPeriodicals(
      new LegalPeriodicalQuery(
        null,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(legalPeriodicals.content())
      .singleElement()
      .extracting(
        LegalPeriodical::abbreviation,
        LegalPeriodical::title,
        LegalPeriodical::subtitle,
        LegalPeriodical::citationStyle
      )
      .containsExactly(lpAbbreviation, lpTitle, lpSubtitle, lpCitationStyle);
  }

  @Test
  void findLegalPeriodicals_something() {
    // given
    var lpAbbreviation = "BKK";
    var lpTitle = "Die Betriebskrankenkasse";
    var lpSubtitle = "Zeitschrift des Bundesverbandes der Betriebskrankenkassen Essen";
    var lpCitationStyle = "1969, 138-140; BKK 2007, Sonderbeilage, 1-5";
    LegalPeriodicalEntity legalPeriodicalEntity = new LegalPeriodicalEntity();
    legalPeriodicalEntity.setAbbreviation(lpAbbreviation);
    legalPeriodicalEntity.setTitle(lpTitle);
    legalPeriodicalEntity.setSubtitle(lpSubtitle);
    legalPeriodicalEntity.setCitationStyle(lpCitationStyle);
    given(
      legalPeriodicalsRepository.findByAbbreviationContainingIgnoreCaseOrTitleContainingIgnoreCase(
        eq("something"),
        eq("something"),
        any(Pageable.class)
      )
    ).willReturn(new PageImpl<>(List.of(legalPeriodicalEntity)));

    // when
    var legalPeriodicals = legalPeriodicalService.findLegalPeriodicals(
      new LegalPeriodicalQuery(
        "something",
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(legalPeriodicals.content())
      .singleElement()
      .extracting(
        LegalPeriodical::abbreviation,
        LegalPeriodical::title,
        LegalPeriodical::subtitle,
        LegalPeriodical::citationStyle
      )
      .containsExactly(lpAbbreviation, lpTitle, lpSubtitle, lpCitationStyle);
  }

  @Test
  void findLegalPeriodicalsByAbbreviation() {
    // given
    var lpAbbreviation = "BKK";
    var lpTitle = "Die Betriebskrankenkasse";
    var lpSubtitle = "Zeitschrift des Bundesverbandes der Betriebskrankenkassen Essen";
    var lpCitationStyle = "1969, 138-140; BKK 2007, Sonderbeilage, 1-5";
    LegalPeriodicalEntity legalPeriodicalEntity = new LegalPeriodicalEntity();
    legalPeriodicalEntity.setAbbreviation(lpAbbreviation);
    legalPeriodicalEntity.setTitle(lpTitle);
    legalPeriodicalEntity.setSubtitle(lpSubtitle);
    legalPeriodicalEntity.setCitationStyle(lpCitationStyle);
    LegalPeriodicalEntity probe = new LegalPeriodicalEntity();
    probe.setAbbreviation(lpAbbreviation);
    given(legalPeriodicalsRepository.findAll(Example.of(probe))).willReturn(
      List.of(legalPeriodicalEntity)
    );

    // when
    List<LegalPeriodical> legalPeriodicals =
      legalPeriodicalService.findLegalPeriodicalsByAbbreviation("BKK");

    // then
    assertThat(legalPeriodicals)
      .singleElement()
      .extracting(
        LegalPeriodical::abbreviation,
        LegalPeriodical::title,
        LegalPeriodical::subtitle,
        LegalPeriodical::citationStyle
      )
      .containsExactly(lpAbbreviation, lpTitle, lpSubtitle, lpCitationStyle);
  }
}
