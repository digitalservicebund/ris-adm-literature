package de.bund.digitalservice.ris.adm_vwv.adapter.publishing;

import de.bund.digitalservice.ris.adm_vwv.application.PublishingFailedException;
import jakarta.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
  private final String datatype;
  private final String publisherName;

  /**
   * Creates a UTC timestamp for changelog filenames.
   * Example: 2025-08-21T07_25_07Z
   */
  private static final DateTimeFormatter CHANGELOG_TIMESTAMP_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH_mm_ss'Z'").withZone(ZoneOffset.UTC);

  @Override
  public String getName() {
    return this.publisherName;
  }

  @Override
  public void publish(@Nonnull Options options) {
    String documentNumber = options.documentNumber();
    String xmlKey = String.format("%s.akn.xml", documentNumber);

    try {
      // Publish the XML document
      log.info("Publishing document {} to S3 bucket '{}'", documentNumber, bucketName);
      PutObjectRequest xmlRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(xmlKey)
        .contentType("application/xml")
        .build();
      s3Client.putObject(xmlRequest, RequestBody.fromString(options.xmlContent()));
      log.info("Successfully published document {} to S3.", documentNumber);

      // Publish a new changelog file
      log.info("Publishing changelog file to S3 bucket '{}'", bucketName);
      String timestamp = CHANGELOG_TIMESTAMP_FORMATTER.format(Instant.now());
      String changelogKey = String.format("changelogs/%s-%s.json", timestamp, datatype);
      String changelogContent = "{\"change_all\": true}";

      PutObjectRequest changelogRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(changelogKey)
        .contentType("application/json")
        .build();
      s3Client.putObject(changelogRequest, RequestBody.fromString(changelogContent));
      log.info("Successfully published changelog file '{}' to S3.", changelogKey);
    } catch (S3Exception e) {
      log.error(
        "Failed to publish document {} or its changelog to S3 bucket '{}'",
        documentNumber,
        bucketName,
        e
      );
      throw new PublishingFailedException(
        "Failed to publish document " + documentNumber + " to S3",
        e
      );
    }
  }
}
