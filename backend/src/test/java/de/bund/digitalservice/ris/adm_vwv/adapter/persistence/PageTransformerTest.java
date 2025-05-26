package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.Page;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

class PageTransformerTest {

  @Test
  void transform() {
    // given
    var page = new PageImpl<>(List.of(new TestModel("name1"), new TestModel("name2")));

    // when
    Page<TestModel> actualPage = PageTransformer.transform(page);

    // then
    assertThat(actualPage)
      .isNotNull()
      .extracting(Page::number, Page::size, Page::totalElements)
      .containsExactly(0, 2, 2L);
  }

  @Test
  void transform_withMapping() {
    // given
    var page = new PageImpl<>(List.of(new TestEntity(1L, "name")));

    // when
    Page<TestModel> actualPage = PageTransformer.transform(page, testEntity ->
      new TestModel(testEntity.name)
    );

    // then
    assertThat(actualPage)
      .isNotNull()
      .extracting(Page::content, Page::size, Page::totalElements)
      .containsExactly(List.of(new TestModel("name")), 1, 1L);
  }

  private record TestEntity(long id, String name) {}

  private record TestModel(String name) {}
}
