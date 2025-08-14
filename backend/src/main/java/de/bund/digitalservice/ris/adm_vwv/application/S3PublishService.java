package de.bund.digitalservice.ris.adm_vwv.application;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
//TODO: Is this really a service? Where to put the class?
public class S3PublishService implements PublishPort {

  private final S3Client s3Client;
  private final String bucketName;

  public S3PublishService(S3Client s3Client, String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
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
