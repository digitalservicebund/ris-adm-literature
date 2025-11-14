package de.bund.digitalservice.ris.adm_literature.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class ApplicationReadyListenerTest {

  @Test
  void logAfterStartup(CapturedOutput output) {
    // given
    ApplicationReadyListener listener = new ApplicationReadyListener();

    // when
    listener.logAfterStartup();

    // then
    assertThat(output).contains("Startup completed");
  }
}
