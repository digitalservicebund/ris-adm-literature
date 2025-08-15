package de.bund.digitalservice.ris.adm_vwv.adapter.publishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Integration test for the composite publisher logic.
 * This test uses Testcontainers and LocalStack to spin up a real S3-compatible environment
 * and verifies that the composite publisher correctly routes requests to the appropriate bean.
 */
@Testcontainers
// This property is needed because our main AwsConfig provides a mock S3Client bean
// for the "test" profile, but this test needs to override it with a real one
// from the inner @TestConfiguration. This property allows that override to happen.
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
class S3PublishAdapterIntegrationTest {

  private static final String FIRST_BUCKET_NAME = "test-bucket-1";
  private static final String FIRST_PUBLISHER_NAME = "privateBsgPublisher";
  private static final String SECOND_BUCKET_NAME = "test-bucket-2";
  private static final String SECOND_PUBLISHER_NAME = "secondTestPublisher";

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(
    DockerImageName.parse("localstack/localstack:4.7.0")
  ).withServices(LocalStackContainer.Service.S3);

  /**
   * Provides two S3Client beans to test the composite logic.
   */
  @TestConfiguration
  static class S3TestConfig {

    @Bean("privateBsgS3Client")
    public S3Client s3Client() {
      return S3Client.builder()
        .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
        .credentialsProvider(
          StaticCredentialsProvider.create(
            AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
          )
        )
        .region(Region.of(localStack.getRegion()))
        .forcePathStyle(true)
        .build();
    }

    @Bean(SECOND_PUBLISHER_NAME)
    public PublishPort secondTestPublisher(S3Client s3Client) {
      return new S3PublishAdapter(s3Client, SECOND_BUCKET_NAME, SECOND_PUBLISHER_NAME);
    }
  }

  @Autowired
  private PublishPort publishPort;

  @Autowired
  private S3Client s3Client;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("otc.private-bsg-client.bucket-name", () -> FIRST_BUCKET_NAME);
  }

  @BeforeEach
  void setUp() {
    createBucket(FIRST_BUCKET_NAME);
    createBucket(SECOND_BUCKET_NAME);
  }

  private void createBucket(String bucketName) {
    s3Client.createBucket(b -> b.bucket(bucketName));
  }

  @Test
  void publish_shouldRouteToFirstPublisherAndUploadToCorrectBucket() {
    // given
    String docNumber = "doc-abc-456";
    String xmlContent = "<test-data>This is a test</test-data>";
    var options = new PublishPort.Options(docNumber, xmlContent, FIRST_PUBLISHER_NAME);

    // when
    publishPort.publish(options);

    // then
    // Verify the file exists in the FIRST bucket
    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
    assertThat(responseBytes.asUtf8String()).isEqualTo(xmlContent);

    // Verify the file does NOT exist in the SECOND bucket
    GetObjectRequest secondBucketRequest = GetObjectRequest.builder()
      .bucket(SECOND_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    assertThatThrownBy(() -> s3Client.getObject(secondBucketRequest)).isInstanceOf(
      S3Exception.class
    );
  }

  @Test
  void publish_shouldRouteToSecondPublisherAndUploadToCorrectBucket() {
    // given
    String docNumber = "doc-xyz-789";
    String xmlContent = "<test-data>Another test</test-data>";
    var options = new PublishPort.Options(docNumber, xmlContent, SECOND_PUBLISHER_NAME);

    // when
    publishPort.publish(options);

    // then
    // Verify the file exists in the SECOND bucket
    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(SECOND_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
    assertThat(responseBytes.asUtf8String()).isEqualTo(xmlContent);

    // Verify the file does NOT exist in the FIRST bucket
    GetObjectRequest firstBucketRequest = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    assertThatThrownBy(() -> s3Client.getObject(firstBucketRequest)).isInstanceOf(
      S3Exception.class
    );
  }

  @Test
  void publish_shouldNotPublish_whenTargetIsUnknown() {
    // given
    String docNumber = "doc-def-789";
    String xmlContent = "<test-data>This should not be published</test-data>";
    var options = new PublishPort.Options(docNumber, xmlContent, "unknown-publisher");

    // when
    publishPort.publish(options);

    // then
    // Verify that NO file was created in EITHER bucket
    GetObjectRequest request1 = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    GetObjectRequest request2 = GetObjectRequest.builder()
      .bucket(SECOND_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();

    assertThatThrownBy(() -> s3Client.getObject(request1)).isInstanceOf(S3Exception.class);
    assertThatThrownBy(() -> s3Client.getObject(request2)).isInstanceOf(S3Exception.class);
  }
}
