package com.codec.system.error.infrastructure.primary;

import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.MessageSource;
import com.codec.system.LogsSpy;
import com.codec.system.UnitTest;
import com.codec.system.error.domain.CodecSystemApplicationException;
import codec.error.domain.StandardErrorKey;

@UnitTest
@ExtendWith(LogsSpy.class)
class CodecSystemApplicationErrorsHandlerTest {

  private static final CodecSystemApplicationErrorsHandler handler = new CodecSystemApplicationErrorsHandler(mock(MessageSource.class));

  private final LogsSpy logs;

  public CodecSystemApplicationErrorsHandlerTest(LogsSpy logs) {
    this.logs = logs;
  }

  @Test
  void shouldLogServerErrorAsError() {
    handler.handleCodecSystemApplicationException(
      CodecSystemApplicationException.internalServerError(StandardErrorKey.INTERNAL_SERVER_ERROR).message("Oops").build()
    );

    logs.shouldHave(Level.ERROR, "Oops");
  }

  @Test
  void shouldLogClientErrorAsInfo() {
    handler.handleCodecSystemApplicationException(CodecSystemApplicationException.badRequest(StandardErrorKey.BAD_REQUEST).message("Oops").build());

    logs.shouldHave(Level.INFO, "Oops");
  }
}
