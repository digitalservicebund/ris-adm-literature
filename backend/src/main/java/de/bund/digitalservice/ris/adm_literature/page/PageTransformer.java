package de.bund.digitalservice.ris.adm_literature.page;

import jakarta.annotation.Nonnull;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

/**
 * Page transformer class to translate Spring Data' {@link org.springframework.data.domain.Page} to the application's
 * {@link Page}.
 */
@UtilityClass
public class PageTransformer {

  public static <T, R> Page<R> transform(
    @Nonnull org.springframework.data.domain.Page<T> page,
    Function<T, R> mappingFunction
  ) {
    return new Page<>(
      page.map(mappingFunction).getContent(),
      page.getSize(),
      page.getNumber(),
      page.getNumberOfElements(),
      page.getTotalElements(),
      page.isFirst(),
      page.isLast(),
      page.isEmpty()
    );
  }

  public static <T> Page<T> transform(@Nonnull org.springframework.data.domain.Page<T> page) {
    return transform(page, Function.identity());
  }
}
