package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.S3MockClient;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * This class provides beans for {@link S3Client} instances, It configures the S3 clients for the
 * different environments and buckets.
 */
@Configuration
@Slf4j
public class S3Config {

  private static final String MOCK_PROFILE = "!staging & !production & !uat";

  /**
   * Creates and configures the S3Client bean for the public BSG bucket.
   *
   * @param region          The OTC region.
   * @param endpoint        The endpoint URI for the OTC Object Storage Service.
   * @param accessKeyId     The access key ID for authentication.
   * @param secretAccessKey The secret access key for authentication.
   * @return A configured {@link S3Client} instance for the staging environment.
   */
  @Bean("publicBsgClient")
  @Profile("staging")
  public S3Client publicBsgClient(
    @Value("${s3.bucket.region}") String region,
    @Value("${s3.bucket.endpoint}") String endpoint,
    @Value("${s3.bucket.adm.public.access-key-id}") String accessKeyId,
    @Value("${s3.bucket.adm.public.access-key}") String secretAccessKey
  ) throws URISyntaxException {
    return getS3Client(region, endpoint, accessKeyId, secretAccessKey);
  }

  /**
   * Creates and configures the S3Client bean for the public Literature bucket.
   *
   * @param region          The OTC region.
   * @param endpoint        The endpoint URI for the OTC Object Storage Service.
   * @param accessKeyId     The access key ID for authentication.
   * @param secretAccessKey The secret access key for authentication.
   * @return A configured {@link S3Client} instance for the staging environment.
   */
  @Bean("publicLiteratureS3Client")
  @Profile("staging")
  public S3Client publicLiteratureS3Client(
    @Value("${s3.bucket.region}") String region,
    @Value("${s3.bucket.endpoint}") String endpoint,
    @Value("${s3.bucket.literature.public.access-key-id}") String accessKeyId,
    @Value("${s3.bucket.literature.public.access-key}") String secretAccessKey
  ) throws URISyntaxException {
    return getS3Client(region, endpoint, accessKeyId, secretAccessKey);
  }

  /**
   * Creates a mock S3Client bean that writes to the local filesystem.
   * <p>
   *
   * @return An {@link S3MockClient} instance that simulates S3 operations locally.
   */
  @Bean("publicBsgClient")
  @Profile(MOCK_PROFILE)
  public S3Client publicBsgMockClient() {
    log.warn("Using S3MockClient for 'publicBsgClient'");
    return new S3MockClient();
  }

  /**
   * Creates a mock S3Client bean that writes to the local filesystem.
   * <p>
   *
   * @return An {@link S3MockClient} instance that simulates S3 operations locally.
   */
  @Bean("publicLiteratureS3Client")
  @Profile(MOCK_PROFILE)
  public S3Client publicLiteratureMockClient() {
    log.warn("Using S3MockClient for 'publicLiteratureS3Client'");
    return new S3MockClient();
  }

  private static S3Client getS3Client(
    String region,
    String endpoint,
    String accessKeyId,
    String secretAccessKey
  ) throws URISyntaxException {
    log.info("Endpoint {}", endpoint);

    return S3Client.builder()
      .region(Region.of(region))
      .endpointOverride(new URI(endpoint))
      .credentialsProvider(
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))
      )
      .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
      .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
      .forcePathStyle(true)
      .build();
  }
}
