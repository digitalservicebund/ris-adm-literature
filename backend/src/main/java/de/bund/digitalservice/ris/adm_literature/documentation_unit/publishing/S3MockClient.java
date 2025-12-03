package de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Mocked S3Client for local development
 */
@Slf4j
public class S3MockClient implements S3Client {

  @Value("${local.file-storage}")
  private Path relativeLocalStorageDirectory;

  private Path localStorageDirectory;

  @Override
  public String serviceName() {
    return null;
  }

  @Override
  public void close() {
    /* this method is empty because of mock */
  }

  /**
   * Inits the client.
   */
  @PostConstruct
  public void init() {
    this.localStorageDirectory = relativeLocalStorageDirectory.toAbsolutePath();
    this.localStorageDirectory.toFile().mkdirs();
  }

  @Override
  public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) {
    String fileName = putObjectRequest.key();
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      fileName = fileName.replace(':', '_');
    }

    File file = localStorageDirectory.resolve(fileName).toFile();
    file.getParentFile().mkdirs();
    try (
      FileOutputStream fos = new FileOutputStream(file, false);
      InputStream inputStream = requestBody.contentStreamProvider().newStream()
    ) {
      byte[] content = new byte[1024];
      int len = -1;
      while ((len = inputStream.read(content)) != -1) {
        fos.write(content, 0, len);
      }
    } catch (IOException ex) {
      log.info("Couldn't write file: {}", fileName);
      log.debug(ex.getMessage(), ex);
    }

    return PutObjectResponse.builder().build();
  }

  @Override
  public ListObjectsV2Response listObjectsV2(ListObjectsV2Request listObjectsV2Request) {
    String[] nameList = null;
    File localFileStorage;
    String prefix = listObjectsV2Request.prefix();
    if (Strings.isNotBlank(prefix)) {
      localFileStorage = localStorageDirectory.resolve(prefix).toFile();
    } else {
      localFileStorage = localStorageDirectory.toFile();
    }

    if (localFileStorage.isDirectory()) {
      nameList = localFileStorage.list();
    }

    List<S3Object> objectList = Collections.emptyList();
    if (nameList != null) {
      objectList = Arrays.stream(nameList)
        .map(name -> S3Object.builder().key(name).build())
        .toList();
    }

    return ListObjectsV2Response.builder().contents(objectList).build();
  }

  @Override
  public <T> T getObject(
    GetObjectRequest getObjectRequest,
    ResponseTransformer<GetObjectResponse, T> responseTransformer
  ) {
    byte[] bytes = new byte[] {};

    String fileName = getObjectRequest.key();
    File file = localStorageDirectory.resolve(fileName).toFile();
    try (FileInputStream fl = new FileInputStream(file)) {
      bytes = new byte[(int) file.length()];
      int readBytes = fl.read(bytes);
      if (readBytes != file.length()) {
        log.warn("different size between file length and read bytes");
      }
    } catch (IOException _) {
      log.error("Couldn't get object from local storage.");
    }

    return (T) ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), bytes);
  }

  @Override
  public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) {
    String fileName = deleteObjectRequest.key();
    File file = localStorageDirectory.resolve(fileName).toFile();
    if (file.exists()) {
      try {
        Files.delete(file.toPath());
      } catch (IOException ex) {
        log.error("Couldn't delete file", ex);
      }
    }

    return DeleteObjectResponse.builder().build();
  }
}
