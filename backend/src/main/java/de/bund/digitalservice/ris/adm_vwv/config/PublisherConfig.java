package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.application.PublishPort;
import de.bund.digitalservice.ris.adm_vwv.application.S3PublishService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Holds the config for all buckets published to.
 */
@Configuration
public class PublisherConfig {

  @Bean("privateBsgPublisher")
  public PublishPort privateBsgPublisher(
    @Qualifier("privateBsgS3Client") S3Client s3Client,
    @Value("${otc.private-bsg-client.bucket-name}") String bucketName
  ) {
    return new S3PublishService(s3Client, bucketName);
  }
}
