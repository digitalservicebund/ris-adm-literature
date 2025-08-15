package de.bund.digitalservice.ris.adm_vwv.adapter.publishing;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Responsible for publishing documents to the s3 buckets
 */
@Slf4j
@RequiredArgsConstructor
public class S3PublishAdapter implements PublishPort {

  private final S3Client s3Client;
  private final String bucketName;
  private final String publisherName;

  @Override
  public String getName() {
    return this.publisherName;
  }

  @Override
  public void publish(@Nonnull Options options) {
    String documentNumber = options.documentNumber();
    String xmlKey = String.format("%s.akn.xml", documentNumber);

    try {
      log.info("Publishing document {} to S3 bucket '{}'", documentNumber, bucketName);
      PutObjectRequest xmlRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(xmlKey)
        .contentType("application/xml")
        .build();
      s3Client.putObject(xmlRequest, RequestBody.fromString(options.xmlContent()));
      log.info("Successfully published document {} to S3.", documentNumber);
    } catch (S3Exception e) {
      log.error("Failed to publish document {} to S3 bucket '{}'", documentNumber, bucketName, e);
    }
  }
}
