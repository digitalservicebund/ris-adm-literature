package de.bund.digitalservice.ris.adm_literature.config;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.Publisher;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.S3PublishAdapter;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.XmlValidator;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Holds the config for all buckets published to.
 */
@Configuration
@Slf4j
public class PublisherConfig {

  /**
   * Creates a {@link Publisher} bean for publishing BSG documents.
   * @param s3Client The S3 client to use for publishing.
   * @param validator The validator to use for BSG documents.
   * @param bucketName The name of the bucket to publish to.
   * @return A configured {@link Publisher} instance.
   */
  @Bean("publicBsgPublisher")
  public Publisher publicBsgPublisher(
    @Qualifier("publicBsgS3Client") S3Client s3Client,
    @Qualifier("bsgVwvValidator") XmlValidator validator,
    @Value("${s3.bucket.adm.public.bucket-name-ref}") String bucketName
  ) {
    return new S3PublishAdapter(
      s3Client,
      Map.of(DocumentCategory.VERWALTUNGSVORSCHRIFTEN, validator),
      bucketName,
      "publicBsgPublisher"
    );
  }

  /**
   * Creates a {@link Publisher} bean for publishing literature documents.
   * @param s3Client The S3 client to use for publishing.
   * @param uliValidator The validator to use for ULI documents.
   * @param sliValidator The validator to use for SLI documents.
   * @param bucketName The name of the bucket to publish to.
   * @return A configured {@link Publisher} instance.
   */
  @Bean("publicLiteraturePublisher")
  public Publisher publicLiteraturePublisher(
    @Qualifier("publicLiteratureS3Client") S3Client s3Client,
    @Qualifier("uliLiteratureValidator") XmlValidator uliValidator,
    @Qualifier("sliLiteratureValidator") XmlValidator sliValidator,
    @Value("${s3.bucket.literature.public.bucket-name-ref}") String bucketName
  ) {
    return new S3PublishAdapter(
      s3Client,
      Map.of(
        DocumentCategory.LITERATUR_UNSELBSTAENDIG,
        uliValidator,
        DocumentCategory.LITERATUR_SELBSTAENDIG,
        sliValidator
      ),
      bucketName,
      "publicLiteraturePublisher"
    );
  }

  /**
   * Creates a composite {@link Publisher} bean that acts as a central dispatcher.
   * <p>
   * This bean is marked as {@link Primary} so that it becomes the default implementation
   * injected into services like {@code DocumentationUnitService}. Its role is to receive all
   * publish requests and delegate them to the appropriate concrete publisher instance
   * based on the document.targetPublisher specified in the {@link Publisher.PublicationDetails}.
   *
   * @param allPublishers A list of all other beans that implement the {@link Publisher} interface,
   *                      automatically injected by Spring. These publisher beans are defined as a bean like
   *                     {@link PublisherConfig#publicBsgPublisher} where each is configured with a specific S3 client
   *                      and bucket name.
   * @return A single {@link Publisher} instance that handles the routing logic.
   */
  @Bean
  @Primary
  public Publisher compositePublisher(List<Publisher> allPublishers) {
    Map<String, Publisher> publisherMap = allPublishers
      .stream()
      .collect(Collectors.toMap(Publisher::getName, Function.identity()));

    return new Publisher() {
      @Override
      public String getName() {
        return "compositePublisher";
      }

      @Override
      public void publish(@Nonnull PublicationDetails publicationDetails) {
        String target = publicationDetails.category().getPublisherName();
        Publisher selectedPublisher = publisherMap.get(target);
        if (selectedPublisher != null) {
          selectedPublisher.publish(publicationDetails);
        } else {
          log.error("No publisher found for target '{}'.", target);
          throw new IllegalArgumentException("No publisher found for target: " + target);
        }
      }
    };
  }
}
