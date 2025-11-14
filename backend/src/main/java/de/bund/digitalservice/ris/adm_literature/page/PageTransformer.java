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

  /**
   * Transforms the given Spring Data page into a custom page object and returns it.7
   * @param page Spring Data page to transform
   * @param mappingFunction Optional mapping function for mapping the page content
   * @return Transformed page record
   * @param <T> Spring Data page type
   * @param <R> Result page type
   */
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

  /**
   * Transforms the given Spring Data page into a custom page object and returns it.7
   * @param page Spring Data page to transform
   * @return Transformed page record
   * @param <T> Spring Data page type
   */
  public static <T> Page<T> transform(@Nonnull org.springframework.data.domain.Page<T> page) {
    return transform(page, Function.identity());
  }
}
