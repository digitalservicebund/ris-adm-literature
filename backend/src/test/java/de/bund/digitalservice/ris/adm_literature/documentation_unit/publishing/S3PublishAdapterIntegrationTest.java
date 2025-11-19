package de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Integration test for the composite publisher logic.
 * This test uses Testcontainers and LocalStack to spin up a real S3-compatible environment
 * and verifies that the composite publisher correctly routes requests based on DocumentCategory.
 */
@Testcontainers
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
class S3PublishAdapterIntegrationTest {

  private static final String FIRST_BUCKET_NAME = "test-bucket-1";
  private static final String FIRST_PUBLISHER_NAME = "publicBsgPublisher";

  private static final String SECOND_BUCKET_NAME = "test-bucket-2";
  private static final String SECOND_PUBLISHER_NAME = "secondTestPublisher";

  private static final String CHANGELOG_DIR = "changelogs/";

  private static final DocumentCategory CATEGORY_FOR_FIRST_PUBLISHER =
    DocumentCategory.VERWALTUNGSVORSCHRIFTEN;
  private static final DocumentCategory CATEGORY_FOR_SECOND_PUBLISHER =
    DocumentCategory.LITERATUR_UNSELBSTAENDIG;

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(
    DockerImageName.parse("localstack/localstack:4.7.0")
  ).withServices(LocalStackContainer.Service.S3);

  @Autowired
  private XmlValidator xmlValidator;

  /**
   * Provides two S3Client beans to test the composite logic.
   */
  @TestConfiguration
  static class S3TestConfig {

    @Bean
    @Primary
    public XmlValidator mockXmlValidator() {
      return mock(XmlValidator.class);
    }

    @Bean("publicBsgS3Client")
    @Primary
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

    @Bean(FIRST_PUBLISHER_NAME)
    public Publisher firstTestPublisher(S3Client s3Client, XmlValidator xmlValidator) {
      Map<DocumentCategory, XmlValidator> validators = Map.of(
        CATEGORY_FOR_FIRST_PUBLISHER,
        xmlValidator
      );

      return new S3PublishAdapter(s3Client, validators, FIRST_BUCKET_NAME, FIRST_PUBLISHER_NAME);
    }

    @Bean(SECOND_PUBLISHER_NAME)
    public Publisher secondTestPublisher(S3Client s3Client, XmlValidator xmlValidator) {
      Map<DocumentCategory, XmlValidator> validators = Map.of(
        CATEGORY_FOR_SECOND_PUBLISHER,
        xmlValidator
      );

      return new S3PublishAdapter(s3Client, validators, SECOND_BUCKET_NAME, SECOND_PUBLISHER_NAME);
    }
  }

  @Autowired
  private Publisher publisher;

  @Autowired
  private S3Client s3Client;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("otc.private-bsg-client.bucket-name-ref", () -> FIRST_BUCKET_NAME);
  }

  @BeforeEach
  void setUp() {
    createBucket(FIRST_BUCKET_NAME);
    createBucket(SECOND_BUCKET_NAME);
  }

  @AfterEach
  void tearDown() {
    deleteBucket(FIRST_BUCKET_NAME);
    deleteBucket(SECOND_BUCKET_NAME);
  }

  private void deleteBucket(String bucketName) {
    try {
      // If the bucket doesn't exist we don't need to clean it and can return
      s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
    } catch (S3Exception _) {
      return;
    }

    List<S3Object> objects = listObjectsInDirectory(bucketName, "");
    if (!objects.isEmpty()) {
      List<ObjectIdentifier> toDelete = objects
        .stream()
        .map(o -> ObjectIdentifier.builder().key(o.key()).build())
        .toList();
      DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
        .bucket(bucketName)
        .delete(Delete.builder().objects(toDelete).build())
        .build();
      s3Client.deleteObjects(deleteRequest);
    }
    s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
  }

  private void createBucket(String bucketName) {
    s3Client.createBucket(b -> b.bucket(bucketName));
  }

  @Test
  void publish_shouldRouteToFirstPublisherAndUploadToCorrectBucket() {
    // given
    String docNumber = "KSNR456";
    String xmlContent = "<test-data>This is a test</test-data>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      xmlContent,
      CATEGORY_FOR_FIRST_PUBLISHER
    );

    // when
    publisher.publish(options);

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

    // Verify the changelog file exists in the FIRST bucket
    List<S3Object> firstBucketChangelogs = listObjectsInDirectory(FIRST_BUCKET_NAME, CHANGELOG_DIR);
    assertThat(firstBucketChangelogs).hasSize(1);
    S3Object changelog = firstBucketChangelogs.getFirst();
    assertThat(getObjectContent(FIRST_BUCKET_NAME, changelog.key())).isEqualTo(
      "{\"changed\":[\"KSNR456.akn.xml\"]}"
    );
  }

  // TODO: Fix test
  /*  @Test
  void publish_shouldRouteToSecondPublisherAndUploadToCorrectBucket() {
    // given
    String docNumber = "doc-xyz-789";
    String xmlContent = "<test-data>Another test</test-data>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      xmlContent,
      CATEGORY_FOR_SECOND_PUBLISHER
    );

    // when
    publisher.publish(options);

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

    // Verify the changelog file exists in the SECOND bucket
    List<S3Object> secondBucketChangelogs = listObjectsInDirectory(
      SECOND_BUCKET_NAME,
      CHANGELOG_DIR
    );
    assertThat(secondBucketChangelogs).hasSize(1);
    S3Object changelog = secondBucketChangelogs.getFirst();
    assertThat(getObjectContent(SECOND_BUCKET_NAME, changelog.key())).isEqualTo(
      "{\"changed\":[\"doc-xyz-789.akn.xml\"]}"
    );
  }*/

  @Test
  void publish_shouldThrowValidationFailedException_whenXmlIsInvalid() throws Exception {
    // given
    String docNumber = "doc-invalid-123";
    String invalidXmlContent = "<invalid>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      invalidXmlContent,
      CATEGORY_FOR_FIRST_PUBLISHER
    );

    doThrow(new SAXParseException("XML is malformed", null, null, 1, 10))
      .when(xmlValidator)
      .validate(invalidXmlContent);

    // when / then
    assertThatThrownBy(() -> publisher.publish(options))
      .isInstanceOf(ValidationFailedException.class)
      .hasMessageContaining(
        "XML validation failed for document doc-invalid-123 at line 1, column 10"
      );

    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    assertThatThrownBy(() -> s3Client.getObject(request)).isInstanceOf(S3Exception.class);
  }

  @Test
  void publish_shouldThrowPublishingFailedException_whenS3XmlUploadFails() {
    // given
    String docNumber = "doc-s3-fail-123";
    String xmlContent = "<test>will fail</test>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      xmlContent,
      CATEGORY_FOR_FIRST_PUBLISHER
    );

    s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(FIRST_BUCKET_NAME).build());

    // when / then
    assertThatThrownBy(() -> publisher.publish(options))
      .isInstanceOf(PublishingFailedException.class)
      .hasMessageContaining(
        "Failed to publish document doc-s3-fail-123 to S3. Call to external system failed."
      )
      .hasCauseInstanceOf(S3Exception.class);
  }

  @Test
  void publish_shouldThrowValidationFailedException_forGenericSAXException() throws Exception {
    // given
    String docNumber = "doc-sax-fail-456";
    String xmlContent = "<test>sax error</test>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      xmlContent,
      CATEGORY_FOR_FIRST_PUBLISHER
    );

    doThrow(new SAXException("Generic SAX error")).when(xmlValidator).validate(xmlContent);

    // when / then
    assertThatThrownBy(() -> publisher.publish(options))
      .isInstanceOf(ValidationFailedException.class)
      .hasMessageContaining(
        "Failed to publish document doc-sax-fail-456. Validation error: Generic SAX error"
      )
      .hasCauseInstanceOf(SAXException.class);

    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    assertThatThrownBy(() -> s3Client.getObject(request)).isInstanceOf(S3Exception.class);
  }

  @Test
  void publish_shouldThrowValidationFailedException_forIOException() throws Exception {
    // given
    String docNumber = "doc-io-fail-789";
    String xmlContent = "<test>io error</test>";

    var options = new Publisher.PublicationDetails(
      docNumber,
      xmlContent,
      CATEGORY_FOR_FIRST_PUBLISHER
    );

    doThrow(new IOException("Generic IO error")).when(xmlValidator).validate(xmlContent);

    // when / then
    assertThatThrownBy(() -> publisher.publish(options))
      .isInstanceOf(ValidationFailedException.class)
      .hasMessageContaining(
        "Failed to publish document doc-io-fail-789. Validation error: Generic IO error"
      )
      .hasCauseInstanceOf(IOException.class);

    GetObjectRequest request = GetObjectRequest.builder()
      .bucket(FIRST_BUCKET_NAME)
      .key(String.format("%s.akn.xml", docNumber))
      .build();
    assertThatThrownBy(() -> s3Client.getObject(request)).isInstanceOf(S3Exception.class);
  }

  private List<S3Object> listObjectsInDirectory(String bucketName, String directoryPrefix) {
    ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
      .bucket(bucketName)
      .prefix(directoryPrefix)
      .build();
    return s3Client.listObjectsV2(listRequest).contents();
  }

  private String getObjectContent(String bucketName, String key) {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();
    return s3Client.getObjectAsBytes(request).asUtf8String();
  }
}
