package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ZitierArtServiceTest {

  @InjectMocks
  private ZitierArtService zitierArtService;

  @Mock
  private CitationTypeRepository citationTypeRepository;

  @Test
  void findZitierArten_all() {
    // given
    UUID uuid = UUID.randomUUID();
    CitationTypeEntity citationTypeEntity = new CitationTypeEntity();
    citationTypeEntity.setId(uuid);
    citationTypeEntity.setAbbreviation("Änderung");
    citationTypeEntity.setLabel("Änderung");
    //noinspection unchecked
    given(citationTypeRepository.findAll(any(Example.class), any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(citationTypeEntity))
    );

    // when
    var zitierArten = zitierArtService.findZitierArten(
      new ZitierArtQuery(
        null,
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        null,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(zitierArten.content()).contains(new ZitierArt(uuid, "Änderung", "Änderung"));
  }

  @Test
  void findZitierArten_something() {
    // given
    UUID uuid = UUID.randomUUID();
    CitationTypeEntity citationTypeEntity = new CitationTypeEntity();
    citationTypeEntity.setId(uuid);
    citationTypeEntity.setAbbreviation("Änderung");
    citationTypeEntity.setLabel("Änderung");
    given(
      citationTypeRepository.findBySourceAndTargetAndAbbreviationOrLabel(
        eq(DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        ArgumentMatchers.isNull(),
        eq("something"),
        eq("something"),
        any(Pageable.class)
      )
    ).willReturn(new PageImpl<>(List.of(citationTypeEntity)));

    // when
    var zitierArten = zitierArtService.findZitierArten(
      new ZitierArtQuery(
        "something",
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        null,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(zitierArten.content()).contains(new ZitierArt(uuid, "Änderung", "Änderung"));
  }

  @Test
  void findZitierArtenByAbbreviation() {
    // given
    CitationTypeEntity probe = new CitationTypeEntity();
    probe.setAbbreviation("Änderung");
    probe.setSourceDocumentCategory(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    CitationTypeEntity citationTypeEntity = new CitationTypeEntity();
    UUID uuid = UUID.randomUUID();
    citationTypeEntity.setId(uuid);
    citationTypeEntity.setAbbreviation("Änderung");
    citationTypeEntity.setLabel("Änderung");
    given(citationTypeRepository.findAll(Example.of(probe))).willReturn(
      List.of(citationTypeEntity)
    );

    // when
    var zitierArten = zitierArtService.findZitierArtenByAbbreviation(
      "Änderung",
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(zitierArten)
      .first()
      .extracting(ZitierArt::id, ZitierArt::abbreviation, ZitierArt::label)
      .containsExactly(uuid, "Änderung", "Änderung");
  }
}
