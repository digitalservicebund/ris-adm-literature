package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.adapter.S3MockClient;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

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
   * Creates the MOCK S3Client bean that writes to the local filesystem.
   */
  @Bean("privateBsgS3Client")
  @Primary
  @Profile("!staging & !production & !uat")
  public S3Client privateBsgS3MockClient() {
    return new S3MockClient();
  }
}
