package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * Integration test for the S3PublishService.
 * This test uses Testcontainers and LocalStack to spin up a real S3-compatible environment.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class S3PublishServiceIntegrationTest {

  private static final String BUCKET_NAME = "test-bucket";

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(
    DockerImageName.parse("localstack/localstack:3.5.0")
  ).withServices(LocalStackContainer.Service.S3);

  @Autowired
  @Qualifier("privateBsgPublisher")
  private PublishPort publishPort;

  @Autowired
  private S3Client s3Client;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("otc.region", localStack::getRegion);
    registry.add("otc.obs.endpoint", () ->
      localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString()
    );
    registry.add("otc.obs.private.bsg-access-key-id", localStack::getAccessKey);
    registry.add("otc.obs.private.bsg-access-key", localStack::getSecretKey);
    registry.add("otc.private-bsg-client.bucket-name", () -> BUCKET_NAME);
  }

  @BeforeEach
  void setUp() {
    s3Client.createBucket(b -> b.bucket(BUCKET_NAME));
  }

  @Test
  void publish_shouldUploadXmlToS3() {
    // given
    String docNumber = "doc-abc-456";
    String xmlContent = "<test-data>This is a test</test-data>";
    var options = new PublishPort.Options(docNumber, xmlContent);

    // when
    publishPort.publish(options);

    // then
    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();

    ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
    String contentFromS3 = responseBytes.asUtf8String();

    assertThat(contentFromS3).isEqualTo(xmlContent);
  }
}
