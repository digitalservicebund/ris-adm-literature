package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class VerweisTypServiceTest {

  @InjectMocks
  private VerweisTypService verweisTypService;

  @Test
  void findVerweisTypen_all() {
    // when
    var refTypes = verweisTypService.findVerweisTypen(
      new VerweisTypQuery(null, new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(refTypes.content())
      .extracting(VerweisTyp::name)
      .containsExactly("anwendung", "neuregelung", "rechtsgrundlage");
  }
}
