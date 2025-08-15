package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.adapter.publishing.S3MockClient;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * This class provides beans for {@link S3Client} instances, It configures the S3 clients for the
 * different environments and buckets.
 */
@Configuration
public class AwsConfig {

  /**
   * Creates and configures the S3Client bean for the private BSG bucket on OTC for the 'staging'
   * profile.
   *
   * @param region          The OTC region.
   * @param endpoint        The endpoint URI for the OTC Object Storage Service.
   * @param accessKeyId     The access key ID for authentication.
   * @param secretAccessKey The secret access key for authentication.
   * @return A configured {@link S3Client} instance for the staging environment.
   */
  @Bean("privateBsgS3Client")
  @Profile("staging")
  public S3Client privateBsgS3Client(
    @Value("${otc.region}") String region,
    @Value("${otc.obs.endpoint}") URI endpoint,
    @Value("${otc.obs.private.bsg-access-key-id}") String accessKeyId,
    @Value("${otc.obs.private.bsg-access-key}") String secretAccessKey
  ) {
    return S3Client.builder()
      .region(Region.of(region))
      .endpointOverride(endpoint)
      .credentialsProvider(
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))
      )
      .build();
  }

  /**
   * Creates a mock S3Client bean that writes to the local filesystem.
   * <p>
   *
   * @return An {@link S3MockClient} instance that simulates S3 operations locally.
   */
  @Bean("privateBsgS3Client")
  @Profile("!staging & !production & !uat & !test")
  public S3Client privateBsgS3MockClient() {
    return new S3MockClient();
  }
}
